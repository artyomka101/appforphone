package com.example.myapplication.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.myapplication.data.Habit
import com.example.myapplication.domain.HabitViewModel
import com.example.myapplication.main.screen.BottomNavDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * УЛЬТРА-БЫСТРОЕ КЕШИРОВАНИЕ ЭКРАНОВ
 * Система предзагрузки и кеширования всех экранов для мгновенной навигации
 */
class ScreenCacheManager {
    
    // Кеш для экранов - хранит готовые к отображению экраны
    private val screenCache = mutableMapOf<BottomNavDestination, @Composable () -> Unit>()
    
    // Кеш для данных - хранит загруженные данные
    private val dataCache = mutableMapOf<String, Any>()
    
    // Флаг инициализации кеша
    private var isCacheInitialized = false
    
    // Корутина для фоновой работы
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Инициализация кеша экранов
     */
    fun initializeCache(
        viewModel: HabitViewModel,
        onAddHabit: () -> Unit,
        onEditHabit: (Habit) -> Unit
    ) {
        if (isCacheInitialized) return
        
        cacheScope.launch {
            try {
                // Предзагружаем все экраны в фоне
                preloadAllScreens(viewModel, onAddHabit, onEditHabit)
                isCacheInitialized = true
            } catch (_: Exception) {
            }
        }
    }
    
    /**
     * Предзагрузка всех экранов
     */
    private suspend fun preloadAllScreens(
        viewModel: HabitViewModel,
        onAddHabit: () -> Unit,
        onEditHabit: (Habit) -> Unit
    ) {
        withContext(Dispatchers.Main) {
            // Кешируем экраны как Composable функции
            screenCache[BottomNavDestination.HOME] = {
                // Главный экран уже оптимизирован
                com.example.myapplication.main.screen.MainScreen.MainScreen(
                    onAddHabit = onAddHabit,
                    onEditHabit = onEditHabit,
                    viewModel = viewModel
                )
            }
            
            screenCache[BottomNavDestination.EXPLORE] = {
                // Экран исследований
                com.example.myapplication.main.screen.ExploreScreen()
            }
            
            screenCache[BottomNavDestination.ADD] = {
                // Экран добавления привычки
                com.example.myapplication.main.screen.AddEditHabitScreen(
                    onSave = { habit ->
                        viewModel.addHabit(habit)
                    },
                    onBack = {
                        // Обработка возврата
                    }
                )
            }
            
            screenCache[BottomNavDestination.NOTIFICATIONS] = {
                // Экран уведомлений
                com.example.myapplication.main.screen.NotificationsScreen(
                    viewModel = viewModel
                )
            }
            
            screenCache[BottomNavDestination.PROFILE] = {
                // Экран профиля
                com.example.myapplication.main.screen.ProfileScreen()
            }
        }
    }
    
    /**
     * Получение кешированного экрана
     */
    fun getCachedScreen(destination: BottomNavDestination): @Composable (() -> Unit)? {
        return screenCache[destination]
    }
    
    /**
     * Кеширование данных
     */
    fun cacheData(key: String, data: Any) {
        dataCache[key] = data
    }
    
    /**
     * Получение кешированных данных
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCachedData(key: String): T? {
        return dataCache[key] as? T
    }
    
    /**
     * Очистка кеша
     */
    fun clearCache() {
        screenCache.clear()
        dataCache.clear()
        isCacheInitialized = false
        // Отменяем фоновые задачи кеша, чтобы избежать утечек
        cacheScope.cancel()
    }
    
    /**
     * Проверка инициализации кеша
     */
    fun isInitialized(): Boolean = isCacheInitialized
}

/**
 * Composable для управления кешем экранов
 */
@Composable
fun rememberScreenCacheManager(): ScreenCacheManager {
    return remember { ScreenCacheManager() }
}

/**
 * Composable для инициализации кеша
 */
@Composable
fun ScreenCacheInitializer(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit
) {
    val cacheManager = rememberScreenCacheManager()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Инициализируем кеш при создании
    LaunchedEffect(Unit) {
        cacheManager.initializeCache(viewModel, onAddHabit, onEditHabit)
    }
    
    // Очищаем кеш при уничтожении
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                cacheManager.clearCache()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

