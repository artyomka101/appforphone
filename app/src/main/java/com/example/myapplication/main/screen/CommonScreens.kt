package com.example.myapplication.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.data.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import com.example.myapplication.utils.AppUtils
import kotlinx.coroutines.launch

/**
 * Общие экраны для приложения
 * Избегаем дублирования кода
 */

@Composable
fun ExploreScreen() {
    val viewModel: HabitViewModel = viewModel()
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }

    val categories = listOf("Все", "Здоровье", "Продуктивность", "Обучение", "Привычки дня")
    val templates = remember { defaultTemplates() }
    val filtered = remember(query, selectedCategory, templates) {
        templates.filter { t ->
            (selectedCategory == "Все" || t.category == selectedCategory) &&
            (query.isBlank() || t.title.contains(query, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Исследовать") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Баннер приветствия
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Откройте новые идеи для задач", style = trackerTypography.subTitleText, color = trackerColors.text)
                    Text("Выбирайте из готовых шаблонов и добавляйте в один клик", style = trackerTypography.oftenText, color = trackerColors.hint)
                }
            }

            // Поиск
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск шаблонов") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Категории
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { 
                            Text(
                                text = cat,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) 
                        },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            // Список шаблонов
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { t ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = AppUtils.stringToIcon(t.icon),
                                    contentDescription = null,
                                    tint = AppUtils.stringToColor(t.color),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(t.title, style = trackerTypography.subTitleText, color = trackerColors.text)
                                    Text(t.description, style = trackerTypography.oftenText, color = trackerColors.hint)
                                }
                                AssistChip(
                                    onClick = {
                                        val habit = Habit(
                                            title = t.title,
                                            description = t.description,
                                            color = t.color,
                                            icon = t.icon,
                                            targetDays = t.targetDays,
                                            scheduledTime = t.scheduledTime
                                        )
                                        viewModel.addHabit(habit)
                                        scope.launch { snackbarHostState.showSnackbar("Добавлено: ${t.title}") }
                                    },
                                    label = { Text("Добавить") }
                                )
                            }
                            if (t.scheduledTime.isNotBlank()) {
                                Text("Рекомендуемое время: ${t.scheduledTime}", style = trackerTypography.oftenText, color = trackerColors.hint)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class HabitTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val icon: String,
    val color: String,
    val targetDays: Int,
    val scheduledTime: String
)

private fun defaultTemplates(): List<HabitTemplate> {
    return listOf(
        HabitTemplate(
            id = "water",
            title = "Пить воду",
            description = "Выпивать 8 стаканов воды",
            category = "Здоровье",
            icon = "water",
            color = "#42A5F5",
            targetDays = 30,
            scheduledTime = "08:00"
        ),
        HabitTemplate(
            id = "reading",
            title = "Читать книгу",
            description = "15 минут чтения в день",
            category = "Обучение",
            icon = "book",
            color = "#7E57C2",
            targetDays = 21,
            scheduledTime = "21:00"
        ),
        HabitTemplate(
            id = "fitness",
            title = "Тренировка",
            description = "Легкая зарядка/прогулка",
            category = "Здоровье",
            icon = "fitness",
            color = "#66BB6A",
            targetDays = 21,
            scheduledTime = "07:30"
        ),
        HabitTemplate(
            id = "plan",
            title = "План на день",
            description = "3 ключевые задачи",
            category = "Продуктивность",
            icon = "task",
            color = "#FFA726",
            targetDays = 30,
            scheduledTime = "09:00"
        ),
        HabitTemplate(
            id = "sleep",
            title = "Ложиться вовремя",
            description = "Сон не позже 23:00",
            category = "Привычки дня",
            icon = "bedtime",
            color = "#26C6DA",
            targetDays = 14,
            scheduledTime = "22:30"
        )
    )
}

@Composable
fun ProfileScreen() {
    val viewModel: HabitViewModel = viewModel()
    val profile by viewModel.profile.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val habitCompletions by viewModel.habitCompletions.collectAsState()
    val completionCounts by viewModel.completionCounts.collectAsState()

    val totalHabits by remember(habits) { derivedStateOf { habits.size } }
    val completedToday by remember(habits, habitCompletions) {
        derivedStateOf { habits.count { habitCompletions[it.id] == true } }
    }
    val activeHabits by remember(habits) { derivedStateOf { habits.count { it.isActive } } }
    val totalCompletions by remember(completionCounts) { derivedStateOf { completionCounts.values.sum() } }
    var name by remember(profile) { mutableStateOf(profile?.name ?: "Гость") }
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Хедер с аватаром и градиентом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(text = "Привет,", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!isEditing) {
                            Text(text = name, style = MaterialTheme.typography.headlineSmall)
                        } else {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it.take(24) },
                                singleLine = true,
                                label = { Text("Имя") }
                            )
                        }
                    }
                }
            }

            // Блок быстрых действий
            Card(
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Настройки", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(onClick = {
                            if (isEditing) viewModel.updateUserName(name)
                            isEditing = !isEditing
                        }, label = { Text(if (isEditing) "Сохранить имя" else "Изменить имя") })
                        AssistChip(onClick = {
                            name = "Гость"
                            viewModel.updateUserName(name)
                        }, label = { Text("Сбросить") })
                    }
                }
            }

            // Инфо/стата (заглушки)
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Статистика", style = MaterialTheme.typography.titleMedium)
                    Text("Всего задач: $totalHabits")
                    Text("Активных задач: $activeHabits")
                    Text("Выполнено сегодня: $completedToday")
                    Text("Всего выполнений: $totalCompletions")
                }
            }
        }
    }
}


