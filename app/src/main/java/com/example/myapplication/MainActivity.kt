package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.main.screen.BottomNavigationBar
import com.example.myapplication.main.screen.SplashScreen
import com.example.myapplication.main.screen.BottomNavDestination
import com.example.myapplication.main.screen.CachedSwipeableMainScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Разрешение предоставлено
        } else {
            // Разрешение отклонено
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Запрашиваем разрешение на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        setContent {
            val viewModel: HabitViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            
            MyApplicationTheme(darkTheme = isDarkTheme) {
                HabitTrackerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = "Ошибка",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Произошла ошибка",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun HabitTrackerApp(viewModel: HabitViewModel) {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    var currentBottomNavDestination by remember { mutableStateOf(BottomNavDestination.HOME) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ для Redmi Note 13: мгновенные переходы
    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(animationSpec = tween(25, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(25, easing = FastOutSlowInEasing))
    ) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    }
    
    AnimatedVisibility(
        visible = !showSplash,
        enter = fadeIn(animationSpec = tween(25, easing = FastOutSlowInEasing)) + slideInHorizontally(
            initialOffsetX = { 0 },
            animationSpec = tween(25, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(25, easing = FastOutSlowInEasing))
    ) {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            currentDestination = currentBottomNavDestination,
                            onDestinationClick = { destination ->
                                currentBottomNavDestination = destination
                            },
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
        ) { paddingValues ->
                // НАВИГАЦИЯ С КЕШИРОВАНИЕМ ДЛЯ ПЛАВНОСТИ
                CachedSwipeableMainScreen(
                    viewModel = viewModel,
                    onAddHabit = {
                        currentBottomNavDestination = BottomNavDestination.ADD
                    },
                    onEditHabit = { habit ->
                        // Для редактирования можно добавить отдельный экран или модальное окно
                        // Пока что просто показываем информацию
                    },
                    currentBottomNavDestination = currentBottomNavDestination,
                    onDestinationClick = { destination ->
                        currentBottomNavDestination = destination
                    }
                )
        }
    }
}

// Заглушки для экранов перенесены в SwipeableNavigation.kt
