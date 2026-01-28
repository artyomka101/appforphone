package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Immutable
data class TrackerColors(
    val block: Color,
    val text: Color,
    val hint: Color
)

@Immutable
data class TrackerTextStyle(
    val TitleText: TextStyle,
    val subTitleText: TextStyle,
    val oftenText: TextStyle
)

val LocalTrackerTypography = staticCompositionLocalOf{
    TrackerTextStyle(
        TitleText = TextStyle.Default,
        oftenText = TextStyle.Default,
        subTitleText = TextStyle.Default
    )
}

val LocalTrackerColors = staticCompositionLocalOf {
    TrackerColors(
        block = Color.Unspecified,
        text = Color.Unspecified,
        hint = Color.Unspecified
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF6366F1), // Indigo 500
            secondary = Color(0xFF8B5CF6), // Violet 500
            tertiary = Color(0xFF06B6D4), // Cyan 500
            surface = Color(0xFF1E1B4B), // Indigo 900
            surfaceVariant = Color(0xFF312E81), // Indigo 800
            background = Color(0xFF0F0F23), // Very dark blue
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onSurface = Color(0xFFE0E7FF), // Indigo 100
            onSurfaceVariant = Color(0xFFC7D2FE), // Indigo 200
            onBackground = Color(0xFFE0E7FF),
            outline = Color(0xFF4F46E5), // Indigo 600
            outlineVariant = Color(0xFF312E81)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6366F1), // Indigo 500
            secondary = Color(0xFF8B5CF6), // Violet 500
            tertiary = Color(0xFF06B6D4), // Cyan 500
            surface = Color(0xFFFFFBFE), // Pure white
            surfaceVariant = Color(0xFFF3F4F6), // Gray 100
            background = Color(0xFFF8FAFC), // Slate 50
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onSurface = Color(0xFF1E1B4B), // Indigo 900
            onSurfaceVariant = Color(0xFF374151), // Gray 700
            onBackground = Color(0xFF1E1B4B),
            outline = Color(0xFF6366F1), // Indigo 500
            outlineVariant = Color(0xFFE5E7EB) // Gray 200
        )
    }
    
    val trackerColors = if (darkTheme) {
        TrackerColors(
            block = Color(0xFF6366F1), // Indigo 500
            text = Color(0xFFE0E7FF), // Indigo 100
            hint = Color(0xFF94A3B8) // Slate 400
        )
    } else {
        TrackerColors(
            block = Color(0xFF6366F1), // Indigo 500
            text = Color(0xFF1E1B4B), // Indigo 900
            hint = Color(0xFF64748B) // Slate 500
        )
    }
    
    val trackerTypography = TrackerTextStyle(
        TitleText = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        subTitleText = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        oftenText = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    )
    
    CompositionLocalProvider(
        LocalTrackerColors provides trackerColors,
        LocalTrackerTypography provides trackerTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
