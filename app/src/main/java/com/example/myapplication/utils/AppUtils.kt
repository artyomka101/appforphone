package com.example.myapplication.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

object AppUtils {
    
    val availableColors = listOf(
        Color(0xFF2196F3) to "#2196F3", // Синий
        Color(0xFF4CAF50) to "#4CAF50", // Зеленый
        Color(0xFFFF9800) to "#FF9800", // Оранжевый
        Color(0xFFE91E63) to "#E91E63", // Розовый
        Color(0xFF9C27B0) to "#9C27B0", // Фиолетовый
        Color(0xFF00BCD4) to "#00BCD4", // Голубой
        Color(0xFFFF5722) to "#FF5722", // Красный
        Color(0xFF795548) to "#795548", // Коричневый
        Color(0xFF607D8B) to "#607D8B", // Серый
        Color(0xFFFFC107) to "#FFC107"  // Желтый
    )
    
    val availableIcons = listOf(
        Icons.Default.Task to "task",
        Icons.Default.FitnessCenter to "fitness",
        Icons.Default.Book to "book",
        Icons.Default.Water to "water",
        Icons.Default.LocalDining to "dining",
        Icons.Default.Bedtime to "bedtime",
        Icons.Default.School to "school",
        Icons.Default.Work to "work",
        Icons.Default.Favorite to "favorite",
        Icons.Default.Star to "star"
    )
    
    fun colorToString(color: Color): String {
        return availableColors.find { it.first == color }?.second ?: "#2196F3"
    }
    
    fun stringToColor(colorString: String): Color {
        return availableColors.find { it.second == colorString }?.first ?: Color(0xFF2196F3)
    }
    
    fun iconToString(icon: ImageVector): String {
        return availableIcons.find { it.first == icon }?.second ?: "task"
    }
    
    fun stringToIcon(iconString: String): ImageVector {
        return availableIcons.find { it.second == iconString }?.first ?: Icons.Default.Task
    }
}