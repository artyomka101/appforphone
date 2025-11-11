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
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.main.screen.MainScreen.MainScreen
import com.example.myapplication.main.screen.MainScreen.UltraFastMainScreen
import com.example.myapplication.main.screen.AddEditHabitScreen
import com.example.myapplication.main.screen.NotificationsScreen
import com.example.myapplication.main.screen.ExploreScreen
import com.example.myapplication.main.screen.ProfileScreen
import kotlinx.coroutines.launch

/**
 * УЛЬТРА-БЫСТРАЯ НАВИГАЦИЯ: Система с предзагруженными страницами для мгновенных свайпов
 * Все страницы загружаются сразу и кэшируются для максимальной производительности
 */
@Composable
fun UltraFastSwipeableMainScreen(
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
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: состояние текущей страницы
    var currentPage by remember { mutableStateOf(pageMapping[currentBottomNavDestination] ?: 0) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ: анимация перехода
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else 0f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
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
        if (deltaX > 50f && currentPage > 0) {
            // Свайп вправо - предыдущая страница
            currentPage--
            isDragging = false
            dragOffset = 0f
        } else if (deltaX < -50f && currentPage < 4) {
            // Свайп влево - следующая страница
            currentPage++
            isDragging = false
            dragOffset = 0f
        } else {
            // Возвращаем на место
            isDragging = false
            dragOffset = 0f
        }
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
        // Используем FloatPixels корректно: берем raw px через LocalDensity
        val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        
        // УЛЬТРА-ОПТИМИЗАЦИЯ: рендерим все страницы сразу с анимацией
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
                    }
            ) {
                when (page) {
                    0 -> UltraFastMainScreen(
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

