package com.example.myapplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography

@Composable
fun ModernStatsCard(
    completedToday: Int,
    totalHabits: Int,
    completionRate: Int,
    trackerColors: com.example.myapplication.ui.theme.TrackerColors,
    trackerTypography: com.example.myapplication.ui.theme.TrackerTextStyle
) {
    // Используем цвета напрямую для лучшей производительности
    val primaryColor = MaterialTheme.colorScheme.primary
    val progressColor = when {
        completionRate >= 80 -> Color(0xFF10B981) // Green
        completionRate >= 50 -> Color(0xFFF59E0B) // Yellow
        else -> Color(0xFFEF4444) // Red
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.1f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Заголовок
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊",
                        style = trackerTypography.subTitleText,
                        color = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Сегодня",
                        style = trackerTypography.subTitleText,
                        color = trackerColors.text,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Основная статистика
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Выполнено сегодня
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = completedToday.toString(),
                            style = trackerTypography.TitleText,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "из $totalHabits",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                    
                    // Процент выполнения
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$completionRate%",
                            style = trackerTypography.TitleText,
                            color = progressColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "выполнено",
                            style = trackerTypography.oftenText,
                            color = trackerColors.hint
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Прогресс-бар
                LinearProgressIndicator(
                    progress = { completionRate / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = primaryColor,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }
    }
}
