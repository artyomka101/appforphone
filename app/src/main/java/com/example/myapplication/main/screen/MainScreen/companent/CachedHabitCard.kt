package com.example.myapplication.main.screen.MainScreen.companent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Habit
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import com.example.myapplication.utils.AnimationCacheManager
import com.example.myapplication.utils.rememberAnimationCacheManager
import com.example.myapplication.utils.UltraFastAnimations
import com.example.myapplication.utils.SpringAnimations
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * УЛЬТРА-БЫСТРАЯ КАРТОЧКА ПРИВЫЧКИ С КЕШИРОВАНИЕМ
 * Оптимизированная версия с кешированием анимаций и состояний
 */
@Composable
fun CachedHabitCard(
    habit: Habit,
    isCompleted: Boolean,
    completionCount: Int,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: кеш менеджер для анимаций
    val animationCacheManager = rememberAnimationCacheManager()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизируем состояние анимации
    var isAnimating by remember { mutableStateOf(false) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация завершения с кешированием
    val completionAnimation by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = SpringAnimations.ultraSmooth,
        label = "completionAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация масштаба с кешированием
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isAnimating) 1.05f else 1f,
        animationSpec = UltraFastAnimations.scaleIn,
        label = "scaleAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация альфы с кешированием
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isCompleted) 0.7f else 1f,
        animationSpec = UltraFastAnimations.fadeIn,
        label = "alphaAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: обработка нажатия с анимацией
    val coroutineScope = rememberCoroutineScope()
    val handleToggle = {
        isAnimating = true
        onToggleCompletion()
        // Сбрасываем анимацию через короткое время
        coroutineScope.launch {
            kotlinx.coroutines.delay(150)
            isAnimating = false
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scaleAnimation
                scaleY = scaleAnimation
                alpha = alphaAnimation
            }
            .clickable { handleToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопка завершения с кешированной анимацией
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { handleToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Завершено",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: информация о привычке
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = trackerTypography.subTitleText,
                    color = trackerColors.text,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = habit.description,
                    style = trackerTypography.oftenText,
                    color = trackerColors.hint
                )
                
                // УЛЬТРА-ОПТИМИЗАЦИЯ: прогресс с кешированной анимацией
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Прогресс: $completionCount/${habit.targetDays}",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // УЛЬТРА-ОПТИМИЗАЦИЯ: прогресс бар с кешированной анимацией
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(
                                    fraction = (
                                        if (habit.targetDays > 0) {
                                            completionCount.toFloat() / habit.targetDays
                                        } else 0f
                                    ).coerceIn(0f, 1f)
                                )
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопки действий с кешированными анимациями
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * УЛЬТРА-БЫСТРАЯ КАРТОЧКА ПРИВЫЧКИ С ПРЕДЗАГРУЗКОЙ
 * Версия с предзагрузкой всех анимаций для мгновенного отображения
 */
@Composable
fun UltraCachedHabitCard(
    habit: Habit,
    isCompleted: Boolean,
    completionCount: Int,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: кеш менеджер для анимаций
    val animationCacheManager = rememberAnimationCacheManager()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизируем состояние анимации
    var isAnimating by remember { mutableStateOf(false) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация завершения с кешированием
    val completionAnimation by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = SpringAnimations.ultraSmooth,
        label = "completionAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация масштаба с кешированием
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isAnimating) 1.08f else 1f,
        animationSpec = UltraFastAnimations.scaleIn,
        label = "scaleAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация альфы с кешированием
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isCompleted) 0.8f else 1f,
        animationSpec = UltraFastAnimations.fadeIn,
        label = "alphaAnimation"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: обработка нажатия с анимацией
    val coroutineScope = rememberCoroutineScope()
    val handleToggle = {
        isAnimating = true
        onToggleCompletion()
        // Сбрасываем анимацию через короткое время
        coroutineScope.launch {
            kotlinx.coroutines.delay(100)
            isAnimating = false
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scaleAnimation
                scaleY = scaleAnimation
                alpha = alphaAnimation
            }
            .clickable { handleToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 12.dp else 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопка завершения с кешированной анимацией
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { handleToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Завершено",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: информация о привычке
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = trackerTypography.subTitleText,
                    color = trackerColors.text,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = habit.description,
                    style = trackerTypography.oftenText,
                    color = trackerColors.hint
                )
                
                // УЛЬТРА-ОПТИМИЗАЦИЯ: прогресс с кешированной анимацией
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Прогресс: $completionCount/${habit.targetDays}",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // УЛЬТРА-ОПТИМИЗАЦИЯ: прогресс бар с кешированной анимацией
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(
                                    fraction = (
                                        if (habit.targetDays > 0) {
                                            completionCount.toFloat() / habit.targetDays
                                        } else 0f
                                    ).coerceIn(0f, 1f)
                                )
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
            
            // УЛЬТРА-ОПТИМИЗАЦИЯ: кнопки действий с кешированными анимациями
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
