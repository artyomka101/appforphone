package com.example.myapplication.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import com.example.myapplication.domain.model.Habit
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // Мемоизация цвета и иконки для оптимизации
    val habitColor = remember(habit.color) { AppUtils.stringToColor(habit.color) }
    val habitIcon = remember(habit.icon) { AppUtils.stringToIcon(habit.icon) }
    
    // Мемоизация состояний для предотвращения пересчетов
    val isActive = remember(habit.isActive) { habit.isActive }
    val habitTitle = remember(habit.title) { habit.title }
    val habitDescription = remember(habit.description) { habit.description }
    
    // Оптимизированные анимации с spring для плавности
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )
    
    val cardColor by animateColorAsState(
        targetValue = when {
            !habit.isActive -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            !habit.isActive -> trackerColors.hint.copy(alpha = 0.6f)
            isCompleted -> trackerColors.hint
            else -> trackerColors.text
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "textColor"
    )
    
    Card(
        modifier = modifier
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
                accentColor = habitColor,
                onClick = { if (isActive) onToggleCompletion() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Иконка задачи
            Icon(
                imageVector = habitIcon,
                contentDescription = "Иконка задачи",
                tint = habitColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация о задаче
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habitTitle,
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
                
                if (habitDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habitDescription,
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Прогресс
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isActive) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка редактирования
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Кнопка удаления
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
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
    // Анимация для эффекта "взрыва" при выполнении
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "toggleScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isCompleted) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "toggleRotation"
    )
    
    val backgroundBrush = if (isCompleted) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF4CAF50),
                Color(0xFF2196F3),
                accentColor.copy(alpha = 0.8f)
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

    val borderColor = if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .size(36.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .clip(CircleShape)
            .background(backgroundBrush)
            .border(width = if (isCompleted) 0.dp else 1.dp, color = borderColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            // Анимированная галочка с эффектом появления
            val checkScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "checkScale"
            )
            
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выполнено",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                    }
            )
        } else {
            // Пустая окружность с легким бликом
            Box(
                modifier = Modifier
                    .size(16.dp)
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
