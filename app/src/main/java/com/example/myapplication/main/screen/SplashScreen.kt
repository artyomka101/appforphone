package com.example.myapplication.main.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // Анимации
    var logoScale by remember { mutableFloatStateOf(0f) }
    var logoAlpha by remember { mutableFloatStateOf(0f) }
    var textAlpha by remember { mutableFloatStateOf(0f) }
    var textOffset by remember { mutableFloatStateOf(50f) }
    var progress by remember { mutableFloatStateOf(0f) }
    var showProgress by remember { mutableStateOf(false) }
    
    // Запускаем мгновенные анимации без лагов!
    LaunchedEffect(Unit) {
        // Анимация логотипа - мгновенная
        logoScale = 1f
        logoAlpha = 1f
        
        delay(0) // Мгновенная задержка
        
        // Анимация текста - мгновенная
        textAlpha = 1f
        textOffset = 0f
        
        delay(0) // Мгновенная задержка
        
        // Показываем прогресс
        showProgress = true
        
        // Анимация прогресса - мгновенная
        progress = 1f
        
        delay(0) // Мгновенная задержка
        
        // Завершаем splash screen
        onSplashFinished()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип с оптимизированной анимацией
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎯",
                    fontSize = 60.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Заголовок с анимацией
            Text(
                text = "Планировщик задач",
                style = trackerTypography.TitleText,
                color = trackerColors.text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Подзаголовок
            Text(
                text = "Планируйте и выполняйте задачи",
                style = trackerTypography.oftenText,
                color = trackerColors.hint,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Прогресс бар
            if (showProgress) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .width(200.dp)
                            .height(4.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Загрузка...",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Декоративные элементы
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 100.dp, y = 100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
