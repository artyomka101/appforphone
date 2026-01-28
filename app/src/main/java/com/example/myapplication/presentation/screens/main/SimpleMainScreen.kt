package com.example.myapplication.presentation.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.Habit
import com.example.myapplication.presentation.viewmodels.HabitViewModel
import com.example.myapplication.presentation.components.HabitCard
import com.example.myapplication.presentation.components.SimpleHabitCard
import com.example.myapplication.presentation.components.HabitStats
import com.example.myapplication.presentation.components.DateSelector
import com.example.myapplication.presentation.components.ModernStatsCard
import com.example.myapplication.presentation.components.EmptyStateCard
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import kotlinx.datetime.LocalDate

// HabitState moved to MainScreen.kt to avoid redeclaration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMainScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit,
    viewModel: HabitViewModel = viewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val habitCompletions by viewModel.habitCompletions.collectAsState()
    val completionCounts by viewModel.completionCounts.collectAsState()
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    val completedToday = habits.count { habitCompletions[it.id] == true }
    val completionRate = if (habits.isNotEmpty()) {
        (completedToday * 100 / habits.size)
    } else 0
    val habitsList = habits.sortedBy { it.title }
    
    var showCompletionNotification by remember { mutableStateOf(false) }
    var completedHabitsCount by remember { mutableStateOf(0) }
    
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
                    Column {
                        Text(
                            text = "Планировщик задач",
                            style = trackerTypography.TitleText,
                            color = trackerColors.text,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedDate.dayOfMonth.toString().padStart(2, '0')}.${selectedDate.monthNumber.toString().padStart(2, '0')}.${selectedDate.year}",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleTheme() }) {
                        Text(
                            text = if (isDarkTheme) "☀️" else "🌙",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Статистика вверху
            if (habits.isNotEmpty()) {
                item {
                    ModernStatsCard(
                        completedToday = completedToday,
                        totalHabits = habits.size,
                        completionRate = completionRate,
                        trackerColors = trackerColors,
                        trackerTypography = trackerTypography
                    )
                }
            }
            
            // Селектор даты
            item {
                DateSelector(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setSelectedDate(it) }
                )
            }
            
            if (habits.isEmpty()) {
                item {
                    EmptyStateCard(
                        trackerColors = trackerColors,
                        trackerTypography = trackerTypography,
                        onAddHabit = onAddHabit
                    )
                }
            } else {
                items(
                    items = habitsList,
                    key = { habit -> habit.id }
                ) { habit ->
                    val habitState = HabitState(
                        isCompleted = habitCompletions[habit.id] ?: false,
                        completionCount = completionCounts[habit.id] ?: 0
                    )
                    
                    SimpleHabitCard(
                        habit = habit,
                        isCompleted = habitState.isCompleted,
                        completionCount = habitState.completionCount,
                        onToggleCompletion = { 
                            viewModel.toggleHabitCompletion(habit.id) 
                        },
                        onEdit = { onEditHabit(habit) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }
        }
    }
    
    if (showCompletionNotification) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎉",
                    style = trackerTypography.subTitleText,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Поздравляем!",
                        style = trackerTypography.subTitleText,
                        color = trackerColors.text,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Вы завершили $completedHabitsCount привычек!",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
                TextButton(
                    onClick = { showCompletionNotification = false }
                ) {
                    Text("✕", color = trackerColors.hint)
                }
            }
        }
    }
}

