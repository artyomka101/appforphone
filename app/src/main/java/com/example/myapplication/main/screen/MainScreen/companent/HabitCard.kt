package com.example.myapplication.main.screen.MainScreen.companent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import com.example.myapplication.utils.AppUtils

@Composable
fun HabitCard(
    habit: Habit,
    isCompleted: Boolean,
    completionCount: Int = 0,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // Стандартные анимации Compose
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.95f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "cardScale"
    )
    
    val cardColor by animateColorAsState(
        targetValue = when {
            !habit.isActive -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "cardColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            !habit.isActive -> trackerColors.hint.copy(alpha = 0.6f)
            isCompleted -> trackerColors.hint
            else -> trackerColors.text
        },
        animationSpec = tween(300),
        label = "textColor"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка отметки с обновленным дизайном
            CompleteToggle(
                isCompleted = isCompleted,
                accentColor = AppUtils.stringToColor(habit.color),
                onClick = { if (habit.isActive) onToggleCompletion() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Иконка задачи
            Icon(
                imageVector = AppUtils.stringToIcon(habit.icon),
                contentDescription = "Иконка задачи",
                tint = AppUtils.stringToColor(habit.color),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация о задаче
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = trackerTypography.subTitleText,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                if (habit.scheduledTime.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Время: ${habit.scheduledTime}",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
                
                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habit.description,
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Прогресс
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!habit.isActive) {
                        Text(
                            text = "✅ Цель достигнута!",
                            style = trackerTypography.oftenText,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Прогресс: $completionCount/${habit.targetDays} дней",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                }
            }
            
            // Кнопки действий
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = trackerColors.hint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompleteToggle(
    isCompleted: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val backgroundBrush = if (isCompleted) {
        Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.9f),
                accentColor.copy(alpha = 0.6f)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        )
    }

    val borderColor = if (isCompleted) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundBrush)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выполнено",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        } else {
            // Пустая окружность с легким бликом
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.0f)
                            )
                        )
                    )
            )
        }
    }
}