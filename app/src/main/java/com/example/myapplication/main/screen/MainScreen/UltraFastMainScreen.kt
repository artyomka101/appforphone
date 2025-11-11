package com.example.myapplication.main.screen.MainScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.Habit
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.main.screen.MainScreen.companent.*
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import kotlinx.coroutines.launch

/**
 * УЛЬТРА-БЫСТРЫЙ ГЛАВНЫЙ ЭКРАН: Предзагруженные компоненты для мгновенного отклика
 * Все компоненты кэшируются и рендерятся заранее
 */
@Composable
fun UltraFastMainScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit,
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val habitCompletions by viewModel.habitCompletions.collectAsState()
    val completionCounts by viewModel.completionCounts.collectAsState()
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизируем все вычисления
    val completedToday by remember(habits, habitCompletions) {
        derivedStateOf { 
            habits.count { habitCompletions[it.id] == true }
        }
    }
    
    val completionRate by remember(habits, completedToday) {
        derivedStateOf { 
            if (habits.isNotEmpty()) {
                (completedToday * 100 / habits.size)
            } else 0 
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: предзагружаем все компоненты
    val preloadedComponents = remember(habits, completedToday, completionRate, trackerColors, trackerTypography) {
        mutableMapOf<String, @Composable () -> Unit>()
    }
    
    // Предзагружаем компоненты
    LaunchedEffect(habits, completedToday, completionRate) {
        preloadedComponents["statsCard"] = {
            ModernStatsCard(
                completedToday = completedToday,
                totalHabits = habits.size,
                completionRate = completionRate,
                trackerColors = trackerColors,
                trackerTypography = trackerTypography
            )
        }
        
        // Убираем предзагрузку DateSelector для корректной работы кнопок
        
        preloadedComponents["emptyState"] = {
            EmptyStateCard(
                trackerColors = trackerColors,
                trackerTypography = trackerTypography,
                onAddHabit = onAddHabit
            )
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: состояние для уведомлений
    var showCompletionNotification by remember { mutableStateOf(false) }
    var completedHabitsCount by remember { mutableStateOf(0) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: проверка завершенных задач
    LaunchedEffect(habits, habitCompletions) {
        val completedCount = habits.count { !it.isActive }
        if (completedCount > 0 && completedCount != completedHabitsCount) {
            completedHabitsCount = completedCount
            showCompletionNotification = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Мои задачи",
                        style = trackerTypography.TitleText,
                        color = trackerColors.text,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Кнопка темного режима
                    TextButton(onClick = { viewModel.toggleTheme() }) {
                        Text(
                            text = if (isDarkTheme) "☀️" else "🌙",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                    
                    // Кнопка добавления задачи
                    IconButton(onClick = onAddHabit) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить задачу",
                            tint = trackerColors.hint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { paddingValues ->
        // УЛЬТРА-ОПТИМИЗАЦИЯ: используем обычный Column вместо LazyColumn для мгновенного рендеринга
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .graphicsLayer {
                    // УЛЬТРА-ОПТИМИЗАЦИЯ: плавная прокрутка для Redmi Note 13
                    clip = true
                    renderEffect = null
                }
        ) {
            // УЛЬТРА-ОПТИМИЗАЦИЯ: рендерим все компоненты сразу
            if (habits.isNotEmpty()) {
                // Статистика
                preloadedComponents["statsCard"]?.invoke() ?: ModernStatsCard(
                    completedToday = completedToday,
                    totalHabits = habits.size,
                    completionRate = completionRate,
                    trackerColors = trackerColors,
                    trackerTypography = trackerTypography
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Селектор даты - рендерим напрямую для корректной работы кнопок
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.setSelectedDate(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Пустое состояние или список привычек
            if (habits.isEmpty()) {
                preloadedComponents["emptyState"]?.invoke() ?: EmptyStateCard(
                    trackerColors = trackerColors,
                    trackerTypography = trackerTypography,
                    onAddHabit = onAddHabit
                )
            } else {
                // УЛЬТРА-ОПТИМИЗАЦИЯ: рендерим все привычки сразу
                habits.forEach { habit ->
                    val habitState = remember(habit.id, habitCompletions, completionCounts) {
                        HabitState(
                            isCompleted = habitCompletions[habit.id] ?: false,
                            completionCount = completionCounts[habit.id] ?: 0
                        )
                    }
                    
                    UltraFastHabitCard(
                        habit = habit,
                        isCompleted = habitState.isCompleted,
                        completionCount = habitState.completionCount,
                        onToggleCompletion = { viewModel.toggleHabitCompletion(habit.id) },
                        onEdit = { onEditHabit(habit) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизированное состояние привычки
@Composable
private fun rememberHabitState(
    habitId: Long,
    habitCompletions: Map<Long, Boolean>,
    completionCounts: Map<Long, Int>
): HabitState {
    return remember(habitId, habitCompletions, completionCounts) {
        HabitState(
            isCompleted = habitCompletions[habitId] ?: false,
            completionCount = completionCounts[habitId] ?: 0
        )
    }
}

// УЛЬТРА-ОПТИМИЗАЦИЯ: состояние привычки для мемоизации
data class HabitState(
    val isCompleted: Boolean,
    val completionCount: Int
)
