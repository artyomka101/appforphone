package com.example.myapplication.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import com.example.myapplication.utils.AppUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    habit: Habit? = null,
    onSave: (Habit) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(habit?.title ?: "") }
    var description by remember { mutableStateOf(habit?.description ?: "") }
    var targetDays by remember { mutableStateOf(habit?.targetDays?.toString() ?: "30") }
    var scheduledTime by remember { mutableStateOf(habit?.scheduledTime ?: "") }
    var showTimePicker by remember { mutableStateOf(false) }
    val (initialHour, initialMinute) = remember(scheduledTime) {
        val parts = scheduledTime.split(":")
        val hh = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 8
        val mm = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
        hh to mm
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    var selectedColor by remember { 
        mutableStateOf(
            if (habit != null) AppUtils.stringToColor(habit.color) 
            else AppUtils.availableColors.first().first
        ) 
    }
    var selectedIcon by remember { 
        mutableStateOf(
            if (habit != null) AppUtils.stringToIcon(habit.icon) 
            else AppUtils.availableIcons.first().first
        ) 
    }
    
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (habit == null) "Добавить задачу" else "Редактировать задачу",
                        style = trackerTypography.TitleText,
                        color = trackerColors.text,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = trackerColors.hint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 100.dp // Добавляем отступ снизу для нижней панели
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Заголовок в стиле скриншота
                Text(
                    text = if (habit == null) "Создать задачу" else "Редактировать задачу",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                // Секция названия
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "НАЗВАНИЕ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название задачи") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedColor,
                            focusedLabelColor = selectedColor
                        )
                    )
                }
            }
            
            item {
                // Описание
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            item {
                // Секция выбора цвета и иконки
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Выбор цвета
                    Text(
                        text = "ЦВЕТ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(AppUtils.availableColors.size) { index ->
                            val (color, _) = AppUtils.availableColors[index]
                            ColorSelectionCard(
                                color = color,
                                isSelected = selectedColor == color,
                                onClick = { selectedColor = color }
                            )
                        }
                    }
                    
                    // Выбор иконки
                    Text(
                        text = "ИКОНКА",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(AppUtils.availableIcons.size) { index ->
                            val (icon, iconString) = AppUtils.availableIcons[index]
                            val name = when (iconString) {
                                "task" -> "Задача"
                                "fitness" -> "Спорт"
                                "book" -> "Чтение"
                                "water" -> "Вода"
                                "dining" -> "Еда"
                                "bedtime" -> "Сон"
                                "school" -> "Учеба"
                                "work" -> "Работа"
                                "favorite" -> "Любимое"
                                "star" -> "Звезда"
                                else -> "Задача"
                            }
                            IconSelectionCard(
                                icon = icon,
                                name = name,
                                isSelected = selectedIcon == icon,
                                onClick = { selectedIcon = icon }
                            )
                        }
                    }
                }
            }
            
            item {
                // Время выполнения
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ВРЕМЯ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { /* readOnly - ввод через пикер */ },
                        readOnly = true,
                        label = { Text("Время (HH:MM)") },
                        placeholder = { Text("08:30") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Выбрать время")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedColor,
                            focusedLabelColor = selectedColor
                        )
                    )
                }
            }

            item {
                // Секция цели
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ЦЕЛЬ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = targetDays,
                        onValueChange = { targetDays = it },
                        label = { Text("Цель в днях") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedColor,
                            focusedLabelColor = selectedColor
                        )
                    )
                }
            }
            
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            item {
                // Spacer for layout balance
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            item {
                // Кнопка сохранения - всегда видимая и кликабельная
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val habitToSave = Habit(
                                id = habit?.id ?: 0,
                                title = title.trim(),
                                description = description.trim(),
                                targetDays = targetDays.toIntOrNull() ?: 30,
                                isActive = true,
                                color = AppUtils.colorToString(selectedColor),
                                icon = AppUtils.iconToString(selectedIcon),
                                scheduledTime = scheduledTime.trim()
                            )
                            onSave(habitToSave)
                            onBack()
                        } else {
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = true, // Всегда включена
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (title.isNotBlank()) {
                            selectedColor
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        },
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Text(
                        text = if (habit == null) "Добавить задачу" else "Сохранить изменения",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
        }
    }

    if (showTimePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .width(IntrinsicSize.Min)
                ) {
                    Text(
                        text = "Выберите время",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
                        TextButton(onClick = {
                            val hh = timePickerState.hour.toString().padStart(2, '0')
                            val mm = timePickerState.minute.toString().padStart(2, '0')
                            scheduledTime = "$hh:$mm"
                            showTimePicker = false
                        }) { Text("ОК") }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectionCard(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(60.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Button
            ),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        border = if (isSelected) {
            BorderStroke(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выбрано",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun IconSelectionCard(
    icon: ImageVector,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Button
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
