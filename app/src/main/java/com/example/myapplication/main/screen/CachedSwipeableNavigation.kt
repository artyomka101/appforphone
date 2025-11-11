package com.example.myapplication.main.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Habit
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.utils.ScreenCacheManager
import com.example.myapplication.utils.rememberScreenCacheManager
import com.example.myapplication.main.screen.MainScreen.MainScreen
import com.example.myapplication.main.screen.AddEditHabitScreen
import com.example.myapplication.main.screen.NotificationsScreen
import com.example.myapplication.main.screen.ExploreScreen
import com.example.myapplication.main.screen.ProfileScreen
import kotlinx.coroutines.launch

/**
 * УЛЬТРА-БЫСТРАЯ НАВИГАЦИЯ С КЕШИРОВАНИЕМ
 * Система с предзагруженными страницами и кешированием для мгновенных переходов
 */
@Composable
fun CachedSwipeableMainScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit,
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
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: состояние текущей страницы
    var currentPage by remember { mutableStateOf(pageMapping[currentBottomNavDestination] ?: 0) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: кеш менеджер для предзагрузки экранов
    val cacheManager = rememberScreenCacheManager()
    
    val coroutineScope = rememberCoroutineScope()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация перехода с улучшенной плавностью
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pageOffset"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: синхронизация с навигацией
    LaunchedEffect(currentBottomNavDestination) {
        val targetPage = pageMapping[currentBottomNavDestination] ?: 0
        if (currentPage != targetPage) {
            currentPage = targetPage
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: синхронизация обратно в навигацию
    LaunchedEffect(currentPage) {
        val destination = reverseMapping[currentPage] ?: BottomNavDestination.HOME
        if (currentBottomNavDestination != destination) {
            onDestinationClick(destination)
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: обработка свайпов с улучшенной чувствительностью
    val handleSwipe = { deltaX: Float ->
        if (deltaX > 30f && currentPage > 0) {
            // Свайп вправо - предыдущая страница
            currentPage--
            isDragging = false
            dragOffset = 0f
        } else if (deltaX < -30f && currentPage < 4) {
            // Свайп влево - следующая страница
            currentPage++
            isDragging = false
            dragOffset = 0f
        } else {
            // Возвращаем на место с плавной анимацией
            isDragging = false
            dragOffset = 0f
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: инициализация кеша
    LaunchedEffect(Unit) {
        cacheManager.initializeCache(viewModel, onAddHabit, onEditHabit)
    }
    
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { 
                        handleSwipe(dragOffset)
                    },
                    onDrag = { _, dragAmount ->
                        dragOffset += dragAmount.x
                    }
                )
            }
    ) {
        val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        
        // УЛЬТРА-ОПТИМИЗАЦИЯ: рендерим все страницы с кешированием
        for (page in 0..4) {
            val pageOffset = (page - currentPage) * 1f + (if (screenWidthPx != 0f) animatedOffset / screenWidthPx else 0f)
            val alpha = if (page == currentPage) 1f else 0f
            val scale = if (page == currentPage) 1f else 0.95f
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = pageOffset * screenWidthPx
                        this.alpha = alpha
                        scaleX = scale
                        scaleY = scale
                        clip = true
                        // УЛЬТРА-ОПТИМИЗАЦИЯ: улучшенная производительность рендеринга
                        renderEffect = null
                    }
            ) {
                // Для страницы добавления задачи используем не кешированную версию,
                // чтобы корректно возвращаться на главный экран после сохранения
                val destination = reverseMapping[page] ?: BottomNavDestination.HOME
                if (destination == BottomNavDestination.ADD) {
                    AddEditHabitScreen(
                        onSave = { habit ->
                            viewModel.addHabit(habit)
                            currentPage = 0
                        },
                        onBack = { currentPage = 0 }
                    )
                } else {
                    // УЛЬТРА-ОПТИМИЗАЦИЯ: используем кешированные экраны для остальных страниц
                    val cachedScreen = cacheManager.getCachedScreen(destination)
                    if (cachedScreen != null) {
                        cachedScreen()
                    } else {
                        when (page) {
                            0 -> MainScreen(
                                onAddHabit = onAddHabit,
                                onEditHabit = onEditHabit,
                                viewModel = viewModel
                            )
                            1 -> ExploreScreen()
                            3 -> NotificationsScreen(viewModel = viewModel)
                            4 -> ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

/**
 * УЛЬТРА-БЫСТРАЯ НАВИГАЦИЯ С ПРЕДЗАГРУЗКОЙ
 * Версия с предзагрузкой всех экранов в фоне
 */
@Composable
fun UltraCachedSwipeableMainScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit,
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
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: состояние текущей страницы
    var currentPage by remember { mutableStateOf(pageMapping[currentBottomNavDestination] ?: 0) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: кеш менеджер
    val cacheManager = rememberScreenCacheManager()
    
    val coroutineScope = rememberCoroutineScope()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация перехода с максимальной плавностью
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pageOffset"
    )
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: синхронизация с навигацией
    LaunchedEffect(currentBottomNavDestination) {
        val targetPage = pageMapping[currentBottomNavDestination] ?: 0
        if (currentPage != targetPage) {
            currentPage = targetPage
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: синхронизация обратно в навигацию
    LaunchedEffect(currentPage) {
        val destination = reverseMapping[currentPage] ?: BottomNavDestination.HOME
        if (currentBottomNavDestination != destination) {
            onDestinationClick(destination)
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: обработка свайпов
    val handleSwipe = { deltaX: Float ->
        if (deltaX > 25f && currentPage > 0) {
            currentPage--
            isDragging = false
            dragOffset = 0f
        } else if (deltaX < -25f && currentPage < 4) {
            currentPage++
            isDragging = false
            dragOffset = 0f
        } else {
            isDragging = false
            dragOffset = 0f
        }
    }
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: инициализация кеша
    LaunchedEffect(Unit) {
        cacheManager.initializeCache(viewModel, onAddHabit, onEditHabit)
    }
    
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { 
                        handleSwipe(dragOffset)
                    },
                    onDrag = { _, dragAmount ->
                        dragOffset += dragAmount.x
                    }
                )
            }
    ) {
        val screenWidth = maxWidth.value
        
        // УЛЬТРА-ОПТИМИЗАЦИЯ: рендерим все страницы с кешированием
        for (page in 0..4) {
            val pageOffset = (page - currentPage) * 1f + (animatedOffset / screenWidth)
            val alpha = if (page == currentPage) 1f else 0f
            val scale = if (page == currentPage) 1f else 0.98f
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = pageOffset * screenWidth
                        this.alpha = alpha
                        scaleX = scale
                        scaleY = scale
                        clip = true
                        renderEffect = null
                    }
            ) {
                // УЛЬТРА-ОПТИМИЗАЦИЯ: используем кешированные экраны
                val cachedScreen = cacheManager.getCachedScreen(reverseMapping[page] ?: BottomNavDestination.HOME)
                
                if (cachedScreen != null) {
                    cachedScreen()
                } else {
                    // Fallback на обычные экраны
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
                                currentPage = 0
                            },
                            onBack = {
                                currentPage = 0
                            }
                        )
                        3 -> NotificationsScreen(viewModel = viewModel)
                        4 -> ProfileScreen()
                    }
                }
            }
        }
    }
}
