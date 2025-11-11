package com.example.myapplication.utils

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.myapplication.data.Habit

/**
 * Объединенные утилиты для приложения
 * Содержит все необходимые функции для анимаций, производительности и UI
 */
object AppUtils {
    
    // === АНИМАЦИИ ===
    
    // Стандартные easing функции
    val FastOutSlowInEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val LinearOutSlowInEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val FastOutLinearInEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val SpringEasing = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
    
    // Оптимизированные длительности
    const val FAST_DURATION = 150
    const val STANDARD_DURATION = 300
    const val SLOW_DURATION = 500
    
    // Стандартные анимации
    @Composable
    fun rememberScaleAnimation(
        targetValue: Float,
        duration: Int = STANDARD_DURATION
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(duration, easing = FastOutSlowInEasing),
            label = "scale"
        )
    }
    
    @Composable
    fun rememberAlphaAnimation(
        targetValue: Float,
        duration: Int = STANDARD_DURATION
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(duration, easing = FastOutSlowInEasing),
            label = "alpha"
        )
    }
    
    @Composable
    fun rememberColorAnimation(
        targetValue: Color,
        duration: Int = STANDARD_DURATION
    ): State<Color> {
        return animateColorAsState(
            targetValue = targetValue,
            animationSpec = tween(duration, easing = FastOutSlowInEasing),
            label = "color"
        )
    }
    
    // === ЦВЕТА И ИКОНКИ ===
    
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
    
    // === ПРОИЗВОДИТЕЛЬНОСТЬ ===
    
    // Кэш для оптимизации
    private val performanceCache = mutableMapOf<String, Any?>()
    
    @Composable
    fun <T> rememberCached(
        key: String,
        calculation: () -> T
    ): T {
        return remember(key) {
            @Suppress("UNCHECKED_CAST")
            performanceCache.getOrPut(key) { calculation() } as T
        }
    }
    
    fun clearCache() {
        performanceCache.clear()
    }
    
    // === КОРУТИНЫ ===
    
    // Оптимизированные контексты
    private val IO_CONTEXT = Dispatchers.IO.limitedParallelism(4)
    private val CPU_CONTEXT = Dispatchers.Default.limitedParallelism(2)
    private val MAIN_CONTEXT = Dispatchers.Main.immediate
    
    // Мьютекс для критических секций
    private val criticalSectionMutex = Mutex()
    
    fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(IO_CONTEXT, block = block)
    }
    
    fun CoroutineScope.launchCPU(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(CPU_CONTEXT, block = block)
    }
    
    fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(MAIN_CONTEXT, block = block)
    }
    
    suspend fun <T> withCriticalSection(block: suspend () -> T): T {
        return criticalSectionMutex.withLock { block() }
    }
    
    // === ФУНКЦИОНАЛЬНОЕ ПРОГРАММИРОВАНИЕ ===
    
    @Composable
    fun rememberOptimizedHabitsList(
        habits: List<Habit>,
        sortBy: (Habit) -> String = { it.title }
    ): List<Habit> {
        return remember(habits) {
            derivedStateOf { habits.sortedBy(sortBy) }
        }.value
    }
    
    @Composable
    fun rememberOptimizedStats(
        habits: List<Habit>,
        completions: Map<Long, Boolean>
    ): Triple<Int, Int, Int> {
        return remember(habits, completions) {
            derivedStateOf {
                val completedToday = habits.count { completions[it.id] == true }
                val totalHabits = habits.size
                val completionRate = if (totalHabits > 0) {
                    (completedToday * 100) / totalHabits
                } else 0
                
                Triple(completedToday, totalHabits, completionRate)
            }
        }.value
    }
    
    fun <T, K, V> createOptimizedMap(
        items: List<T>,
        keySelector: (T) -> K,
        valueSelector: (T) -> V
    ): Map<K, V> {
        return items.associate { keySelector(it) to valueSelector(it) }
    }
    
    @Composable
    fun <T> rememberOptimizedFilteredList(
        items: List<T>,
        predicate: (T) -> Boolean
    ): List<T> {
        return remember(items) {
            derivedStateOf { items.filter(predicate) }
        }.value
    }
    
    // === UI ОПТИМИЗАЦИИ ===
    
    @Composable
    fun Modifier.optimizedGraphicsLayer(
        scaleX: Float = 1f,
        scaleY: Float = 1f,
        alpha: Float = 1f,
        translationX: Float = 0f,
        translationY: Float = 0f
    ): Modifier {
        return this.graphicsLayer {
            this.scaleX = scaleX
            this.scaleY = scaleY
            this.alpha = alpha
            this.translationX = translationX
            this.translationY = translationY
            
            // Оптимизации для производительности
            clip = true
            renderEffect = null
        }
    }
    
    // === БЕЗОПАСНАЯ РАБОТА С NULL ===
    
    inline fun <T, R> T?.safeLet(block: (T) -> R): R? {
        return this?.let(block)
    }
    
    // === FLOW ОПЕРАЦИИ ===
    
    fun <T1, T2, R> combineOptimized(
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        transform: (T1, T2) -> R
    ): Flow<R> {
        return combine(flow1, flow2, transform)
    }
    
    fun <T, R> mapOptimized(
        flow: Flow<T>,
        transform: (T) -> R
    ): Flow<R> {
        return flow.map(transform)
    }
    
    fun <T> filterOptimized(
        flow: Flow<T>,
        predicate: (T) -> Boolean
    ): Flow<T> {
        return flow.filter(predicate)
    }
    
    fun <T> distinctOptimized(flow: Flow<T>): Flow<T> {
        return flow.distinctUntilChanged()
    }
}




