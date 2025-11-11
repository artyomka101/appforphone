package com.example.myapplication.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Habit
import com.example.myapplication.data.Notification
import com.example.myapplication.data.UserProfile
import com.example.myapplication.ui.state.HabitUiEvent
import com.example.myapplication.ui.state.HabitUiState
import com.example.myapplication.ui.state.NotificationsUiEvent
import com.example.myapplication.ui.state.NotificationsUiState
import com.example.myapplication.ui.state.ProfileUiEvent
import com.example.myapplication.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// Data класс для простых уведомлений
data class SimpleNotification(
    val id: Long,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean = false
)

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: неизменяемые коллекции и функциональное программирование
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    // Оптимизированные неизменяемые Map для производительности
    private val _habitCompletions = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val habitCompletions: StateFlow<Map<Long, Boolean>> = _habitCompletions.asStateFlow()
    
    private val _completionCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val completionCounts: StateFlow<Map<Long, Int>> = _completionCounts.asStateFlow()
    
    // StateFlow для уведомлений - неизменяемый список
    private val _notifications = MutableStateFlow<List<SimpleNotification>>(emptyList())
    val notifications: StateFlow<List<SimpleNotification>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var notificationService: NotificationService? = null
    private var notificationsJob: Job? = null

    // Профиль
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()
    
    init {
        loadHabits()
        initProfile()
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
            repository.getAllActiveHabits().collect { habitsList ->
                _habits.value = habitsList
                // Загружаем состояния выполнения после обновления списка привычек
                loadHabitCompletions()
            }
        }
    }
    
    private fun loadHabitCompletions() {
        viewModelScope.launch {
            try {
                val dateString = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
                
                // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональное программирование с неизменяемыми коллекциями
                val currentHabits = _habits.value
                
                // Используем функциональные операции для создания неизменяемых Map
                val completions = currentHabits.associateWith { habit ->
                    repository.isHabitCompletedOnDate(habit.id, dateString)
                }
                
                val counts = currentHabits.associateWith { habit ->
                    repository.getCompletionCount(habit.id)
                }
                
                // Преобразуем в Map<Long, Boolean> и Map<Long, Int> для совместимости
                val completionsMap = completions.mapKeys { it.key.id }
                val countsMap = counts.mapKeys { it.key.id }
                
                
                _habitCompletions.value = completionsMap
                _completionCounts.value = countsMap
            } catch (_: Exception) {
            }
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональные методы с оптимизированными coroutines
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
            // Функциональное обновление состояний
            updateStatesAfterChange()
        }
    }
    
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            // Обновляем только при необходимости
            updateStatesAfterChange()
        }
    }
    
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            // Функциональное обновление состояний
            updateStatesAfterChange()
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: выделенный метод для обновления состояний
    private suspend fun updateStatesAfterChange() {
        loadHabitCompletions()
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональная обработка завершения задачи
    private suspend fun handleTaskCompletion(habit: Habit, habitId: Long) {
        try {
            // Показываем уведомление о выполнении задачи
            notificationService?.showTaskCompletedNotification(habit.title)
            
            // Создаем уведомление функциональным способом
            val notification = createTaskCompletedNotification(habit, habitId)
            repository.insertNotification(notification)
        } catch (e: Exception) {
            
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональное создание уведомления
    private fun createTaskCompletedNotification(habit: Habit, habitId: Long): Notification {
        return Notification(
            title = "Задача выполнена! 🎉",
            message = "Поздравляем! Вы выполнили задачу: ${habit.title}",
            type = "TASK_COMPLETED",
            habitId = habitId,
            habitTitle = habit.title,
            createdAt = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        )
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональная проверка достижения цели
    private suspend fun checkGoalAchievement(habitId: Long) {
        try {
            repository.getHabitById(habitId)?.let { habit ->
                val completionCount = repository.getCompletionCount(habitId)
                
                // Функциональная проверка достижения цели
                if (completionCount >= habit.targetDays) {
                    handleGoalAchievement(habit, completionCount)
                }
            }
        } catch (e: Exception) {
            
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональная обработка достижения цели
    private suspend fun handleGoalAchievement(habit: Habit, completionCount: Int) {
        try {
            repository.deactivateHabit(habit.id)
            
            // Показываем уведомление о достижении цели
            notificationService?.showGoalAchievedNotification(habit.title, habit.targetDays)
            
            // Создаем уведомление о достижении цели
            val goalNotification = createGoalAchievedNotification(habit, completionCount)
            repository.insertNotification(goalNotification)
        } catch (e: Exception) {
            
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональное создание уведомления о достижении цели
    private fun createGoalAchievedNotification(habit: Habit, completionCount: Int): Notification {
        return Notification(
            title = "Цель достигнута! ⭐",
            message = "Поздравляем! Вы достигли цели по задаче: ${habit.title} ($completionCount дней подряд)",
            type = "GOAL_ACHIEVED",
            habitId = habit.id,
            habitTitle = habit.title,
            createdAt = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        )
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональный подход с безопасной работой с null
    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            try {
                val dateString = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
                
                // Функциональный подход с безопасной работой с null
                repository.getHabitById(habitId)?.let { habit ->
                    val wasCompleted = repository.isHabitCompletedOnDate(habitId, dateString)
                    
                    // Обновляем базу данных
                    repository.toggleHabitCompletion(habitId, dateString)
                    
                    // Перезагружаем состояния из базы данных для корректной анимации
                    loadHabitCompletions()
                    
                    // Функциональная обработка уведомлений
                    if (!wasCompleted) {
                        handleTaskCompletion(habit, habitId)
                    }
                }
                
                // УЛЬТРА-ОПТИМИЗАЦИЯ: функциональная проверка достижения цели
                checkGoalAchievement(habitId)
            } catch (_: Exception) {
            }
        }
    }
    
    fun isHabitCompletedOnSelectedDate(habitId: Long): Boolean {
        return _habitCompletions.value[habitId] ?: false
    }
    
    fun getHabitCompletionCount(habitId: Long): Int {
        return _completionCounts.value[habitId] ?: 0
    }
    
    fun createTestNotification() {
        viewModelScope.launch {
            try {
                val testNotification = Notification(
                    title = "Тестовое уведомление 🧪",
                    message = "Это тестовое уведомление создано в ${LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))}",
                    type = "TASK_COMPLETED",
                    habitId = null,
                    habitTitle = null,
                    createdAt = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                )
                repository.insertNotification(testNotification)
            } catch (_: Exception) {
            }
        }
    }
    
    fun loadCompletedTasks() {
        // Отменяем предыдущую подписку, если есть, чтобы избежать дублирования коллектора
        notificationsJob?.cancel()
        notificationsJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                
                repository.getAllNotifications().collect { notificationList ->
                    
                    val simpleNotifications = notificationList.map { notification ->
                        SimpleNotification(
                            id = notification.id,
                            title = notification.title,
                            message = notification.message,
                            date = notification.createdAt,
                            isRead = notification.isRead
                        )
                    }
                    
                    _notifications.value = simpleNotifications
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
                // Перезагрузка не требуется: flow обновится автоматически
            } catch (_: Exception) {
            }
        }
    }
    
    fun loadNotifications() {
        loadCompletedTasks()
    }
    
    // Метод initializeNotificationsSafely удален - теперь уведомления управляются в NotificationsScreen
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadHabitCompletions() // Перезагружаем завершения для новой даты
    }
    
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Обновление имени пользователя
    fun updateUserName(name: String) {
        viewModelScope.launch {
            val current = _profile.value ?: UserProfile()
            repository.upsertUserProfile(current.copy(name = name))
        }
    }
    
    suspend fun getCompletionCount(habitId: Long): Int {
        return repository.getCompletionCount(habitId)
    }
    
    fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                repository.deactivateHabit(habitId)
            } catch (_: Exception) {
            }
        }
    }
    
}
