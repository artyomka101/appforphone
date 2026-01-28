package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography

@Composable
fun HabitStats(
    completedToday: Int,
    totalHabits: Int,
    completionRate: Int
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Статистика",
                style = trackerTypography.subTitleText,
                color = trackerColors.text,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Выполнено",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
                
                // Всего привычек
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = totalHabits.toString(),
                        style = trackerTypography.TitleText,
                        color = trackerColors.text,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Всего",
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
                        color = if (completionRate >= 80) 
                            Color(0xFF10B981) // Green
                        else if (completionRate >= 50) 
                            Color(0xFFF59E0B) // Yellow
                        else 
                            Color(0xFFEF4444), // Red
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Успех",
                        style = trackerTypography.oftenText,
                        color = trackerColors.hint
                    )
                }
            }
        }
    }
}
