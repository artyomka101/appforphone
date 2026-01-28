package com.example.myapplication.presentation.navigation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset

enum class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
) {
    HOME("home", "Главная", Icons.Default.Home, Icons.Default.Home),
    EXPLORE("explore", "Исследовать", Icons.Default.Search, Icons.Default.Search),
    ADD("add", "Добавить", Icons.Default.Add, Icons.Default.Add),
    ARCHIVE("archive", "Архив", Icons.Default.Archive, Icons.Default.Archive),
    PROFILE("profile", "Профиль", Icons.Default.Person, Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    currentDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 24.dp) // Увеличиваем отступ снизу
    ) {
        // Основная панель навигации
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceColor.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    
                    BottomNavItem(
                        destination = destination,
                        isSelected = isSelected,
                        onClick = { onDestinationClick(destination) },
                        primaryColor = primaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: androidx.compose.ui.graphics.Color
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            primaryColor.copy(alpha = 0.15f)
                        } else {
                            Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.title,
                    modifier = Modifier.size(22.dp),
                    tint = if (isSelected) {
                        primaryColor
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }
        }
        
        // Текст под иконкой только для выбранного элемента
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Text(
                text = destination.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = primaryColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
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
                        scaleX = fabScale
                        scaleY = fabScale
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