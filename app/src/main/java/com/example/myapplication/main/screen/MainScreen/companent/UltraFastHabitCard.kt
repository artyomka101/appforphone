package com.example.myapplication.main.screen.MainScreen.companent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography

/**
 * УЛЬТРА-БЫСТРАЯ КАРТОЧКА ПРИВЫЧКИ: Предзагруженные компоненты для мгновенного отклика
 * Все анимации и состояния кэшируются для максимальной производительности
 */
@Composable
fun UltraFastHabitCard(
    habit: Habit,
    isCompleted: Boolean,
    completionCount: Int = 0,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизируем все цвета и стили
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    val colorScheme = MaterialTheme.colorScheme
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: предзагружаем все состояния
    val cardColor by remember(habit.id, isCompleted) {
        derivedStateOf {
            if (isCompleted) {
                colorScheme.primary.copy(alpha = 0.1f)
            } else {
                colorScheme.surface
            }
        }
    }
    
    val textColor by remember(habit.id, isCompleted) {
        derivedStateOf {
            if (isCompleted) {
                colorScheme.primary
            } else {
                trackerColors.text
            }
        }
    }
    
    val checkIconColor by remember(habit.id, isCompleted) {
        derivedStateOf {
            if (isCompleted) {
                colorScheme.primary
            } else {
                trackerColors.hint
            }
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мгновенная анимация для максимальной производительности
    val checkIconAlpha by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = tween(15, easing = FastOutSlowInEasing), // УЛЬТРА-быстрая анимация
        label = "checkAlpha"
    )
    
    val checkIconScale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0.8f,
        animationSpec = tween(15, easing = FastOutSlowInEasing), // УЛЬТРА-быстрая анимация
        label = "checkScale"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: предзагружаем все компоненты
    val preloadedComponents = remember(habit.id, isCompleted, completionCount) {
        mutableMapOf<String, @Composable () -> Unit>()
    }
    
    // Предзагружаем компоненты
    LaunchedEffect(habit.id, isCompleted, completionCount) {
        preloadedComponents["title"] = {
            Text(
                text = habit.title,
                style = trackerTypography.TitleText.copy(
                    fontSize = 16.sp,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor,
                maxLines = 2
            )
        }
        
        preloadedComponents["description"] = {
            if (habit.description.isNotEmpty()) {
                Text(
                    text = habit.description,
                    style = trackerTypography.oftenText.copy(fontSize = 12.sp),
                    color = trackerColors.hint,
                    maxLines = 2
                )
            }
        }
        
        preloadedComponents["completionCount"] = {
            if (completionCount > 0) {
                Text(
                    text = "$completionCount/${habit.targetDays}",
                    style = trackerTypography.oftenText.copy(fontSize = 11.sp),
                    color = trackerColors.hint
                )
            }
        }
        
        preloadedComponents["checkIcon"] = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выполнено",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        alpha = checkIconAlpha
                        scaleX = checkIconScale
                        scaleY = checkIconScale
                    },
                tint = checkIconColor
            )
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                // УЛЬТРА-ОПТИМИЗАЦИЯ: плавная анимация для Redmi Note 13
                clip = true
                renderEffect = null
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
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопка выполнения
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        } else {
                            trackerColors.hint.copy(alpha = 0.1f)
                        }
                    )
                    .clickable { onToggleCompletion() },
                contentAlignment = Alignment.Center
            ) {
                preloadedComponents["checkIcon"]?.invoke() ?: Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выполнено",
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            alpha = checkIconAlpha
                            scaleX = checkIconScale
                            scaleY = checkIconScale
                        },
                    tint = checkIconColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: контент карточки
            Column(
                modifier = Modifier.weight(1f)
            ) {
                preloadedComponents["title"]?.invoke() ?: Text(
                    text = habit.title,
                    style = trackerTypography.TitleText.copy(
                        fontSize = 16.sp,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = textColor,
                    maxLines = 2
                )
                
                if (habit.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    preloadedComponents["description"]?.invoke() ?: Text(
                        text = habit.description,
                        style = trackerTypography.oftenText.copy(fontSize = 12.sp),
                        color = trackerColors.hint,
                        maxLines = 2
                    )
                }
                
                if (completionCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    preloadedComponents["completionCount"]?.invoke() ?: Text(
                        text = "$completionCount/${habit.targetDays}",
                        style = trackerTypography.oftenText.copy(fontSize = 11.sp),
                        color = trackerColors.hint
                    )
                }
            }
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопки действий
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(16.dp),
                        tint = trackerColors.hint
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
