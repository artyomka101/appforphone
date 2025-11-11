package com.example.myapplication.main.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.main.screen.MainScreen.MainScreen
import com.example.myapplication.main.screen.MainScreen.SimpleMainScreen
import com.example.myapplication.main.screen.AddEditHabitScreen
import com.example.myapplication.main.screen.NotificationsScreen
import com.example.myapplication.main.screen.ExploreScreen
import com.example.myapplication.main.screen.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableMainScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (com.example.myapplication.data.Habit) -> Unit,
    currentBottomNavDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit
) {
    // УЛЬТРА-ОПТИМИЗАЦИЯ: мемоизируем маппинг страниц
    val pageMapping = remember {
        mapOf(
            BottomNavDestination.HOME to 0,
            BottomNavDestination.EXPLORE to 1,
            BottomNavDestination.ADD to 2,
            BottomNavDestination.NOTIFICATIONS to 3,
            BottomNavDestination.PROFILE to 4
        )
    }
    
    val reverseMapping = remember {
        mapOf(
            0 to BottomNavDestination.HOME,
            1 to BottomNavDestination.EXPLORE,
            2 to BottomNavDestination.ADD,
            3 to BottomNavDestination.NOTIFICATIONS,
            4 to BottomNavDestination.PROFILE
        )
    }
    
    val pagerState = rememberPagerState(
        initialPage = pageMapping[currentBottomNavDestination] ?: 0,
        pageCount = { 5 }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: используем derivedStateOf для избежания лишних пересчетов
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: предзагружаем все страницы для мгновенных свайпов
    val preloadedPages = remember {
        mutableMapOf<Int, @Composable () -> Unit>()
    }
    
    // Предзагружаем все страницы при инициализации
    LaunchedEffect(Unit) {
        preloadedPages[0] = {
            SimpleMainScreen(
                onAddHabit = onAddHabit,
                onEditHabit = onEditHabit,
                viewModel = viewModel
            )
        }
        preloadedPages[1] = { ExploreScreen() }
        preloadedPages[2] = {
            AddEditHabitScreen(
                onSave = { habit ->
                    viewModel.addHabit(habit)
                    coroutineScope.launch {
                        pagerState.scrollToPage(0)
                    }
                },
                onBack = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(0)
                    }
                }
            )
        }
        preloadedPages[3] = { NotificationsScreen(viewModel = viewModel) }
        preloadedPages[4] = { ProfileScreen() }
    }
    
    // Оптимизированная синхронизация с минимальными пересчетами
    LaunchedEffect(currentBottomNavDestination) {
        val targetPage = pageMapping[currentBottomNavDestination] ?: 0
        if (currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: синхронизация только при реальном изменении
    LaunchedEffect(currentPage) {
        val destination = reverseMapping[currentPage] ?: BottomNavDestination.HOME
        if (currentBottomNavDestination != destination) {
            onDestinationClick(destination)
        }
    }
    
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // УЛЬТРА-ОПТИМИЗАЦИЯ: плавные свайпы для Redmi Note 13
                clip = true
                renderEffect = null // Отключаем эффекты для производительности
            },
        contentPadding = PaddingValues(0.dp),
        pageSpacing = 0.dp,
        userScrollEnabled = true
    ) { page ->
        // УЛЬТРА-ОПТИМИЗАЦИЯ: используем предзагруженные страницы для мгновенного отклика
        key(page) {
            preloadedPages[page]?.invoke() ?: run {
                when (page) {
                    0 -> MainScreen(
                        onAddHabit = onAddHabit,
                        onEditHabit = onEditHabit,
                        viewModel = viewModel
                    )
                    1 -> ExploreScreen()
                    2 -> AddEditHabitScreen(
                        onSave = { habit ->
                            viewModel.addHabit(habit)
                            coroutineScope.launch {
                                pagerState.scrollToPage(0)
                            }
                        },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(0)
                            }
                        }
                    )
                    3 -> NotificationsScreen(viewModel = viewModel)
                    4 -> ProfileScreen()
                }
            }
        }
    }
}

