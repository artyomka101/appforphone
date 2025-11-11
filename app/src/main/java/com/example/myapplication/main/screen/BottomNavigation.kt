package com.example.myapplication.main.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import com.example.myapplication.ui.theme.LocalTrackerColors
import com.example.myapplication.ui.theme.LocalTrackerTypography

enum class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
) {
    HOME("home", "Главная", Icons.Default.Home, Icons.Default.Home),
    EXPLORE("explore", "Исследовать", Icons.Default.Search, Icons.Default.Search),
    ADD("add", "Добавить", Icons.Default.Add, Icons.Default.Add),
    NOTIFICATIONS("notifications", "Уведомления", Icons.Default.Notifications, Icons.Default.Notifications),
    PROFILE("profile", "Профиль", Icons.Default.Person, Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    currentDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackerColors = LocalTrackerColors.current
    val trackerTypography = LocalTrackerTypography.current
    
    // Мемоизируем цвета для избежания пересчетов
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 3.dp) // Устанавливаем вертикальные отступы 3dp
    ) {
        // Овальная панель с градиентом и тенью
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50.dp))
                .shadow(
                    elevation = 16.dp, // Увеличиваем тень
                    shape = RoundedCornerShape(50.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            surfaceColor,
                            surfaceColor.copy(alpha = 0.98f)
                        )
                    )
                )
                // Добавляем тонкую обводку для лучшей видимости
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            outlineColor.copy(alpha = 0.1f),
                            outlineColor.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
        ) {
            // Равномерно распределенные кнопки
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        // Кнопка с иконкой
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            primaryColor.copy(alpha = 0.2f)
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                    .shadow(
                                        elevation = if (isSelected) 4.dp else 0.dp,
                                        shape = CircleShape,
                                        ambientColor = if (isSelected) primaryColor.copy(alpha = 0.3f) else Color.Transparent,
                                        spotColor = if (isSelected) primaryColor.copy(alpha = 0.3f) else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { onDestinationClick(destination) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (isSelected) {
                                                primaryColor
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                            }
                                        )
                                    
                                    // Красная точка для уведомлений
                                    if (destination == BottomNavDestination.NOTIFICATIONS && !isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .offset(x = 8.dp, y = (-8).dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.error)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Текст под иконкой
                        Text(
                            text = destination.title,
                            style = trackerTypography.oftenText.copy(
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) {
                                primaryColor
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            },
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarWithFAB(
    currentDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
    onFABClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимации в draw phase для максимальной плавности
    val isAddScreen = currentDestination == BottomNavDestination.ADD
    val panelHeight by animateDpAsState(
        targetValue = if (isAddScreen) 60.dp else 120.dp,
        animationSpec = tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "panelHeight"
    )

    val panelAlpha by animateFloatAsState(
        targetValue = if (isAddScreen) 0.5f else 1f,
        animationSpec = tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "panelAlpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(panelHeight)
            .graphicsLayer { alpha = panelAlpha }
    ) {
        BottomNavigationBar(
            currentDestination = currentDestination,
            onDestinationClick = onDestinationClick,
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = !isAddScreen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(150))
        ) {
            val fabScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(80, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "fabScale"
            )
            
                FloatingActionButton(
                    onClick = onFABClick,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 8.dp)
                        .graphicsLayer {
                            // УЛЬТРА-ОПТИМИЗАЦИЯ: анимации в draw phase
                            scaleX = fabScale
                            scaleY = fabScale
                            // Добавляем плавность для Redmi Note 13
                            alpha = if (isAddScreen) 0f else 1f
                        },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}