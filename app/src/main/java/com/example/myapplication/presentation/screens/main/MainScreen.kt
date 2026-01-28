package com.example.myapplication.presentation.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.myapplication.presentation.components.HabitStats
import com.example.myapplication.presentation.components.DateSelector
import com.example.myapplication.presentation.components.ModernStatsCard
import com.example.myapplication.presentation.components.EmptyStateCard
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import kotlinx.datetime.LocalDate

data class HabitState(
    val isCompleted: Boolean,
    val completionCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
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
    
    val habitsList by remember(habits) {
        derivedStateOf { 
            habits.toList().sortedBy { it.title }
        }
    }
    
    var showCompletionNotification by remember { mutableStateOf(false) }
    var completedHabitsCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(habits, habitCompletions) {
        val completedCount = habits.count { habitCompletions[it.id] == true }
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
                    key = { habit -> habit.id },
                    contentType = { "habit" }
                ) { habit ->
                    val habitState = remember(habit.id, habitCompletions, completionCounts) {
                        HabitState(
                            isCompleted = habitCompletions[habit.id] ?: false,
                            completionCount = completionCounts[habit.id] ?: 0
                        )
                    }
                    HabitCard(
                        habit = habit,
                        isCompleted = habitState.isCompleted,
                        completionCount = habitState.completionCount,
                        onToggleCompletion = { viewModel.toggleHabitCompletion(habit.id) },
                        onEdit = { onEditHabit(habit) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }
        }
    }
    
    if (showCompletionNotification) {
        // Анимация появления с эффектом пружины
        val animatedScale by animateFloatAsState(
            targetValue = if (showCompletionNotification) 1f else 0.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "notificationScale"
        )
        
        val animatedAlpha by animateFloatAsState(
            targetValue = if (showCompletionNotification) 1f else 0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
            label = "notificationAlpha"
        )
        
        // Анимация вращения для иконки
        val infiniteTransition = rememberInfiniteTransition(label = "celebration")
        val iconRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "iconRotation"
        )
        
        // Пульсирующий эффект для фона
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        alpha = animatedAlpha
                        rotationZ = if (showCompletionNotification) 0f else -10f
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = pulseAlpha),
                                    Color(0xFF2196F3).copy(alpha = pulseAlpha),
                                    Color(0xFF9C27B0).copy(alpha = pulseAlpha)
                                ),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Анимированная иконка с вращением
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .graphicsLayer {
                                    rotationZ = iconRotation
                                }
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = 0.8f),
                                            Color(0xFFFFA500).copy(alpha = 0.6f),
                                            Color(0xFFFF6347).copy(alpha = 0.4f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = -iconRotation // Противоположное вращение для стабильности текста
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(20.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎉 Отлично!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Выполнено $completedHabitsCount ${when {
                                    completedHabitsCount % 10 == 1 && completedHabitsCount % 100 != 11 -> "задача"
                                    completedHabitsCount % 10 in 2..4 && completedHabitsCount % 100 !in 12..14 -> "задачи"
                                    else -> "задач"
                                }}!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Стильная кнопка закрытия
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.White.copy(alpha = 0.2f)
                                )
                                .clickable { 
                                    showCompletionNotification = false 
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        // Автоматическое скрытие через 4 секунды
        LaunchedEffect(showCompletionNotification) {
            if (showCompletionNotification) {
                kotlinx.coroutines.delay(4000)
                showCompletionNotification = false
            }
        }
    }
}

