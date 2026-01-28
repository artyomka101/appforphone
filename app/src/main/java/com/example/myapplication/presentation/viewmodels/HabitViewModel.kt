package com.example.myapplication.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.HabitRepository
import com.example.myapplication.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class NotificationItem(
    val id: Long = 0,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean = false
)

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val application: Application
) : ViewModel() {
    
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    private val _archivedHabits = MutableStateFlow<List<Habit>>(emptyList())
    val archivedHabits: StateFlow<List<Habit>> = _archivedHabits.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    private val _habitCompletions = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val habitCompletions: StateFlow<Map<Long, Boolean>> = _habitCompletions.asStateFlow()
    
    private val _completionCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val completionCounts: StateFlow<Map<Long, Int>> = _completionCounts.asStateFlow()
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var notificationsJob: Job? = null
    private var completionsJob: Job? = null
    private val dbMutex = Mutex()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()
    
    // Кэш для оптимизации
    private val completionsCache = mutableMapOf<String, Map<Long, Boolean>>()
    private val countsCache = mutableMapOf<Long, Int>()
    
    // Debouncing для частых операций
    private var saveJob: Job? = null
    private var lastUpdateTime = 0L
    private val updateThrottleMs = 50L // Ограничиваем обновления до 20 раз в секунду
    
    init {
        loadHabits()
        loadArchivedHabits()
        initProfile()
    }
    
    private fun loadArchivedHabits() {
        viewModelScope.launch {
            repository.getArchivedHabits().collect { archivedList ->
                _archivedHabits.value = archivedList
            }
        }
    }
    
    private fun initProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _profile.value = profile ?: UserProfile()
                if (profile == null) {
                    repository.upsertUserProfile(UserProfile())
                }
            }
        }
    }
    
    private fun loadHabits() {
        viewModelScope.launch {
            repository.getAllActiveHabits().collect { habitsList -> // Возвращаем getAllActiveHabits
                _habits.value = habitsList
                loadHabitCompletions()
            }
        }
    }
    
    private fun loadHabitCompletions() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime < updateThrottleMs) {
            return // Пропускаем слишком частые обновления
        }
        lastUpdateTime = currentTime
        
        completionsJob?.cancel()
        completionsJob = viewModelScope.launch {
            try {
                val dateString = _selectedDate.value.toString()
                val currentHabits = _habits.value
                
                // Проверяем кэш
                val cachedCompletions = completionsCache[dateString]
                if (cachedCompletions != null && cachedCompletions.keys.containsAll(currentHabits.map { it.id })) {
                    _habitCompletions.value = cachedCompletions
                } else {
                    // Загружаем из базы данных
                    val completions = currentHabits.associate { habit ->
                        habit.id to repository.isHabitCompletedOnDate(habit.id, dateString)
                    }
                    completionsCache[dateString] = completions
                    _habitCompletions.value = completions
                }
                
                // Загружаем счетчики с кэшированием
                val counts = currentHabits.associate { habit ->
                    habit.id to (countsCache[habit.id] ?: repository.getCompletionCount(habit.id).also { 
                        countsCache[habit.id] = it 
                    })
                }
                _completionCounts.value = counts
            } catch (_: Exception) {
            }
        }
    }
    
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
            // Очищаем кэш для обновления
            countsCache.clear()
            completionsCache.clear()
        }
    }
    
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            // Очищаем кэш для обновления
            countsCache.clear()
            completionsCache.clear()
        }
    }
    
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            // Удаляем из кэша
            countsCache.remove(habit.id)
            completionsCache.values.forEach { it.toMutableMap().remove(habit.id) }
        }
    }
    
    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            dbMutex.withLock {
                try {
                    val dateString = _selectedDate.value.toString()
                    
                    repository.getHabitById(habitId)?.let { habit ->
                        val wasCompleted = _habitCompletions.value[habitId] ?: false
                        
                        // Мгновенно обновляем UI
                        val currentCompletions = _habitCompletions.value.toMutableMap()
                        currentCompletions[habitId] = !wasCompleted
                        _habitCompletions.value = currentCompletions
                        
                        // Обновляем кэш
                        completionsCache[dateString] = currentCompletions
                        
                        // Debounced сохранение в БД
                        saveJob?.cancel()
                        saveJob = launch {
                            delay(100) // Ждем 100мс перед сохранением
                            repository.toggleHabitCompletion(habitId, dateString)
                        }
                        
                        if (!wasCompleted) {
                            // Обновляем счетчик
                            val newCount = (countsCache[habitId] ?: 0) + 1
                            countsCache[habitId] = newCount
                            val currentCounts = _completionCounts.value.toMutableMap()
                            currentCounts[habitId] = newCount
                            _completionCounts.value = currentCounts
                            
                            val title = "Задача выполнена! 🎉"
                            val message = "Поздравляем! Вы выполнили задачу: ${habit.title}"
                            val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            
                            val notification = SimpleNotification(
                                title = title,
                                message = message,
                                type = "completion",
                                habitId = habit.id,
                                habitTitle = habit.title,
                                createdAt = "${currentDateTime.date} ${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}"
                            )
                            repository.insertNotification(notification)
                            
                            // Показываем реальное Android уведомление
                            NotificationHelper.showNotification(
                                context = application,
                                title = title,
                                message = message
                            )
                            
                            checkGoalAchievement(habitId)
                        } else {
                            // Уменьшаем счетчик при отмене
                            val newCount = maxOf(0, (countsCache[habitId] ?: 0) - 1)
                            countsCache[habitId] = newCount
                            val currentCounts = _completionCounts.value.toMutableMap()
                            currentCounts[habitId] = newCount
                            _completionCounts.value = currentCounts
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
    
    private suspend fun checkGoalAchievement(habitId: Long) {
        try {
            repository.getHabitById(habitId)?.let { habit ->
                val completionCount = repository.getCompletionCount(habitId)
                
                if (completionCount >= habit.targetDays) {
                    repository.deactivateHabit(habit.id)
                    
                    val title = "Цель достигнута! ⭐"
                    val message = "Поздравляем! Вы достигли цели по задаче: ${habit.title} ($completionCount дней подряд)"
                    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    
                    val goalNotification = SimpleNotification(
                        title = title,
                        message = message,
                        type = "goal_achieved",
                        habitId = habit.id,
                        habitTitle = habit.title,
                        createdAt = "${currentDateTime.date} ${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}"
                    )
                    repository.insertNotification(goalNotification)
                    
                    // Показываем реальное Android уведомление
                    NotificationHelper.showNotification(
                        context = application,
                        title = title,
                        message = message
                    )
                }
            }
        } catch (_: Exception) {
        }
    }
    
    fun createTestNotification() {
        viewModelScope.launch {
            try {
                val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val testNotification = SimpleNotification(
                    title = "Тестовое уведомление 🧪",
                    message = "Это тестовое уведомление создано в ${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}:${currentDateTime.second.toString().padStart(2, '0')}",
                    type = "test",
                    createdAt = "${currentDateTime.date} ${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}"
                )
                repository.insertNotification(testNotification)
            } catch (_: Exception) {
            }
        }
    }
    
    fun loadCompletedTasks() {
        notificationsJob?.cancel()
        notificationsJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                
                repository.getAllNotifications().collect { notificationList ->
                    _notifications.value = notificationList.map { notification ->
                        NotificationItem(
                            id = notification.id,
                            title = notification.title,
                            message = notification.message,
                            date = notification.createdAt,
                            isRead = notification.isRead
                        )
                    }
                    _isLoading.value = false
                }
            } catch (_: Exception) {
                _notifications.value = emptyList()
                _isLoading.value = false
            }
        }
    }
    
    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                repository.clearAllNotifications()
                _notifications.value = emptyList()
            } catch (_: Exception) {
            }
        }
    }
    
    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notificationId)
            } catch (_: Exception) {
            }
        }
    }
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        // Проверяем кэш перед загрузкой
        val dateString = date.toString()
        val cachedCompletions = completionsCache[dateString]
        if (cachedCompletions != null) {
            _habitCompletions.value = cachedCompletions
        } else {
            loadHabitCompletions()
        }
    }
    
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            val current = _profile.value ?: UserProfile()
            repository.upsertUserProfile(current.copy(name = name))
        }
    }
}