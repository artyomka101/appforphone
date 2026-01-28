package com.example.myapplication.presentation.screens.explore

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
import com.example.myapplication.presentation.viewmodels.HabitViewModel
import com.example.myapplication.domain.model.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import com.example.myapplication.utils.AppUtils
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(viewModel: HabitViewModel) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }

    val categories = listOf("Все", "Здоровье", "Продуктивность", "Обучение", "Привычки дня", "Творчество", "Финансы")
    val templates = remember { defaultTemplates() }
    val filtered = remember(query, selectedCategory, templates) {
        templates.filter { t ->
            (selectedCategory == "Все" || t.category == selectedCategory) &&
            (query.isBlank() || t.title.contains(query, ignoreCase = true) || t.description.contains(query, ignoreCase = true))
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

            // Категории - исправляем для предотвращения случайных свайпов
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { 
                            Text(
                                text = cat,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            ) 
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .wrapContentWidth()
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
                                    Text(
                                        text = t.title, 
                                        style = trackerTypography.subTitleText, 
                                        color = trackerColors.text,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = t.description, 
                                        style = trackerTypography.oftenText, 
                                        color = trackerColors.hint,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
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
                                Text(
                                    text = "Рекомендуемое время: ${t.scheduledTime}", 
                                    style = trackerTypography.oftenText, 
                                    color = trackerColors.hint,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
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
        // Здоровье
        HabitTemplate(
            id = "water",
            title = "Пить воду",
            description = "Выпивать 8 стаканов воды в день",
            category = "Здоровье",
            icon = "water",
            color = "#42A5F5",
            targetDays = 30,
            scheduledTime = "08:00"
        ),
        HabitTemplate(
            id = "fitness",
            title = "Утренняя зарядка",
            description = "15 минут физических упражнений",
            category = "Здоровье",
            icon = "fitness",
            color = "#66BB6A",
            targetDays = 21,
            scheduledTime = "07:30"
        ),
        HabitTemplate(
            id = "walk",
            title = "Прогулка",
            description = "30 минут ходьбы на свежем воздухе",
            category = "Здоровье",
            icon = "fitness",
            color = "#4CAF50",
            targetDays = 30,
            scheduledTime = "18:00"
        ),
        HabitTemplate(
            id = "vitamins",
            title = "Принимать витамины",
            description = "Ежедневный прием витаминов",
            category = "Здоровье",
            icon = "favorite",
            color = "#E91E63",
            targetDays = 30,
            scheduledTime = "09:00"
        ),
        HabitTemplate(
            id = "stretch",
            title = "Растяжка",
            description = "10 минут растяжки для гибкости",
            category = "Здоровье",
            icon = "fitness",
            color = "#9C27B0",
            targetDays = 21,
            scheduledTime = "20:00"
        ),
        
        // Обучение
        HabitTemplate(
            id = "reading",
            title = "Читать книгу",
            description = "20 минут чтения художественной литературы",
            category = "Обучение",
            icon = "book",
            color = "#7E57C2",
            targetDays = 21,
            scheduledTime = "21:00"
        ),
        HabitTemplate(
            id = "language",
            title = "Изучать язык",
            description = "15 минут изучения иностранного языка",
            category = "Обучение",
            icon = "school",
            color = "#3F51B5",
            targetDays = 30,
            scheduledTime = "19:00"
        ),
        HabitTemplate(
            id = "podcast",
            title = "Слушать подкасты",
            description = "Образовательные подкасты по дороге",
            category = "Обучение",
            icon = "book",
            color = "#FF9800",
            targetDays = 21,
            scheduledTime = "08:30"
        ),
        HabitTemplate(
            id = "course",
            title = "Онлайн курс",
            description = "30 минут обучения новым навыкам",
            category = "Обучение",
            icon = "school",
            color = "#795548",
            targetDays = 30,
            scheduledTime = "20:30"
        ),
        
        // Продуктивность
        HabitTemplate(
            id = "plan",
            title = "Планировать день",
            description = "Составить список из 3 главных задач",
            category = "Продуктивность",
            icon = "task",
            color = "#FFA726",
            targetDays = 30,
            scheduledTime = "09:00"
        ),
        HabitTemplate(
            id = "email",
            title = "Проверить почту",
            description = "Обработать входящие сообщения",
            category = "Продуктивность",
            icon = "work",
            color = "#607D8B",
            targetDays = 21,
            scheduledTime = "10:00"
        ),
        HabitTemplate(
            id = "review",
            title = "Анализ дня",
            description = "5 минут рефлексии о прошедшем дне",
            category = "Продуктивность",
            icon = "task",
            color = "#FF5722",
            targetDays = 30,
            scheduledTime = "22:00"
        ),
        HabitTemplate(
            id = "declutter",
            title = "Убрать рабочее место",
            description = "Поддерживать порядок на столе",
            category = "Продуктивность",
            icon = "work",
            color = "#00BCD4",
            targetDays = 21,
            scheduledTime = "17:00"
        ),
        
        // Привычки дня
        HabitTemplate(
            id = "sleep",
            title = "Ложиться вовремя",
            description = "Сон не позже 23:00 для восстановления",
            category = "Привычки дня",
            icon = "bedtime",
            color = "#26C6DA",
            targetDays = 14,
            scheduledTime = "22:30"
        ),
        HabitTemplate(
            id = "morning",
            title = "Утренний ритуал",
            description = "Стакан воды и 5 минут медитации",
            category = "Привычки дня",
            icon = "star",
            color = "#FFC107",
            targetDays = 21,
            scheduledTime = "07:00"
        ),
        HabitTemplate(
            id = "phone",
            title = "Без телефона за едой",
            description = "Осознанное питание без гаджетов",
            category = "Привычки дня",
            icon = "dining",
            color = "#8BC34A",
            targetDays = 14,
            scheduledTime = "12:00"
        ),
        HabitTemplate(
            id = "gratitude",
            title = "Благодарность",
            description = "Записать 3 вещи, за которые благодарен",
            category = "Привычки дня",
            icon = "favorite",
            color = "#E91E63",
            targetDays = 21,
            scheduledTime = "21:30"
        ),
        
        // Творчество
        HabitTemplate(
            id = "draw",
            title = "Рисовать",
            description = "15 минут творческого рисования",
            category = "Творчество",
            icon = "star",
            color = "#9C27B0",
            targetDays = 21,
            scheduledTime = "19:30"
        ),
        HabitTemplate(
            id = "music",
            title = "Играть на инструменте",
            description = "20 минут музыкальной практики",
            category = "Творчество",
            icon = "star",
            color = "#673AB7",
            targetDays = 30,
            scheduledTime = "18:30"
        ),
        HabitTemplate(
            id = "write",
            title = "Писать дневник",
            description = "10 минут свободного письма",
            category = "Творчество",
            icon = "book",
            color = "#FF9800",
            targetDays = 21,
            scheduledTime = "22:30"
        ),
        
        // Финансы
        HabitTemplate(
            id = "budget",
            title = "Отслеживать расходы",
            description = "Записывать все траты за день",
            category = "Финансы",
            icon = "work",
            color = "#4CAF50",
            targetDays = 30,
            scheduledTime = "21:00"
        ),
        HabitTemplate(
            id = "save",
            title = "Откладывать деньги",
            description = "Перевести 100₽ в копилку",
            category = "Финансы",
            icon = "star",
            color = "#FF9800",
            targetDays = 30,
            scheduledTime = "20:00"
        ),
        HabitTemplate(
            id = "invest",
            title = "Изучать инвестиции",
            description = "15 минут чтения о финансах",
            category = "Финансы",
            icon = "book",
            color = "#2196F3",
            targetDays = 21,
            scheduledTime = "19:00"
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


