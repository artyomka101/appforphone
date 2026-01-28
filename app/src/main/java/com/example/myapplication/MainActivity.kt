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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.myapplication.presentation.viewmodels.HabitViewModel
import com.example.myapplication.presentation.navigation.BottomNavigationBar
import com.example.myapplication.presentation.navigation.BottomNavDestination
import com.example.myapplication.presentation.screens.splash.SplashScreen
import com.example.myapplication.presentation.screens.main.MainScreen
import com.example.myapplication.presentation.screens.explore.ExploreScreen
import com.example.myapplication.presentation.screens.add_edit.AddEditHabitScreen
import com.example.myapplication.presentation.screens.archive.ArchiveScreen
import com.example.myapplication.presentation.screens.profile.ProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
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
            val viewModel: HabitViewModel = hiltViewModel()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitTrackerApp(viewModel: HabitViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 }
    )
    val scope = rememberCoroutineScope()
    
    // Состояние для редактирования привычки
    var editingHabit by remember { mutableStateOf<com.example.myapplication.domain.model.Habit?>(null) }
    
    val destinations = remember {
        listOf(
            BottomNavDestination.HOME,
            BottomNavDestination.EXPLORE,
            BottomNavDestination.ADD,
            BottomNavDestination.ARCHIVE,
            BottomNavDestination.PROFILE
        )
    }
    
    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
    ) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    }
    
    AnimatedVisibility(
        visible = !showSplash,
        enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
    ) {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp) // Дополнительный отступ от края экрана
                ) {
                    BottomNavigationBar(
                        currentDestination = destinations[pagerState.currentPage],
                        onDestinationClick = { destination ->
                            scope.launch {
                                // Прямой переход без анимации для быстроты
                                pagerState.scrollToPage(destinations.indexOf(destination))
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(paddingValues),
                key = { destinations[it].route },
                pageSpacing = 0.dp,
                userScrollEnabled = true
            ) { page ->
                // Оптимизированная загрузка экранов
                when (destinations[page]) {
                    BottomNavDestination.HOME -> {
                        MainScreen(
                            viewModel = viewModel,
                            onAddHabit = { 
                                editingHabit = null // Сбрасываем редактируемую привычку
                                scope.launch {
                                    pagerState.scrollToPage(2)
                                }
                            },
                            onEditHabit = { habit ->
                                editingHabit = habit // Устанавливаем привычку для редактирования
                                scope.launch {
                                    pagerState.scrollToPage(2)
                                }
                            }
                        )
                    }
                    BottomNavDestination.EXPLORE -> {
                        ExploreScreen(viewModel = viewModel)
                    }
                    BottomNavDestination.ADD -> {
                        AddEditHabitScreen(
                            habit = editingHabit, // Передаем привычку для редактирования
                            onSave = { habit ->
                                if (editingHabit != null) {
                                    // Обновляем существующую привычку
                                    viewModel.updateHabit(habit.copy(id = editingHabit!!.id))
                                } else {
                                    // Добавляем новую привычку
                                    viewModel.addHabit(habit)
                                }
                                editingHabit = null // Сбрасываем состояние
                                scope.launch {
                                    pagerState.scrollToPage(0)
                                }
                            },
                            onBack = { 
                                editingHabit = null // Сбрасываем состояние
                                scope.launch {
                                    pagerState.scrollToPage(0)
                                }
                            }
                        )
                    }
                    BottomNavDestination.ARCHIVE -> {
                        ArchiveScreen(viewModel = viewModel)
                    }
                    BottomNavDestination.PROFILE -> {
                        ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

// Заглушки для экранов перенесены в SwipeableNavigation.kt
