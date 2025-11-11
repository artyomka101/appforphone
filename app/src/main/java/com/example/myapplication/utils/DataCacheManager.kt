package com.example.myapplication.utils

import androidx.compose.runtime.*
import com.example.myapplication.data.Habit
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

/**
 * УЛЬТРА-БЫСТРОЕ КЕШИРОВАНИЕ ДАННЫХ
 * Система кеширования для мгновенного доступа к данным
 */
class DataCacheManager {
    
    // Кеш для привычек
    private val habitsCache = ConcurrentHashMap<String, List<Habit>>()
    
    // Кеш для состояний выполнения
    private val completionsCache = ConcurrentHashMap<String, Map<Long, Boolean>>()
    
    // Кеш для счетчиков выполнения
    private val countsCache = ConcurrentHashMap<String, Map<Long, Int>>()
    
    // Кеш для уведомлений
    private val notificationsCache = ConcurrentHashMap<String, List<Any>>()
    
    // Мьютекс для безопасного доступа
    private val mutex = Mutex()
    
    
    /**
     * Кеширование привычек
     */
    suspend fun cacheHabits(habits: List<Habit>) {
        mutex.withLock {
            habitsCache["all_habits"] = habits
        }
    }
    
    /**
     * Получение кешированных привычек
     */
    suspend fun getCachedHabits(): List<Habit>? {
        return mutex.withLock {
            habitsCache["all_habits"]
        }
    }
    
    /**
     * Кеширование состояний выполнения
     */
    suspend fun cacheCompletions(date: LocalDate, completions: Map<Long, Boolean>) {
        mutex.withLock {
            completionsCache[date.toString()] = completions
        }
    }
    
    /**
     * Получение кешированных состояний выполнения
     */
    suspend fun getCachedCompletions(date: LocalDate): Map<Long, Boolean>? {
        return mutex.withLock {
            completionsCache[date.toString()]
        }
    }
    
    /**
     * Кеширование счетчиков выполнения
     */
    suspend fun cacheCounts(counts: Map<Long, Int>) {
        mutex.withLock {
            countsCache["all_counts"] = counts
        }
    }
    
    /**
     * Получение кешированных счетчиков
     */
    suspend fun getCachedCounts(): Map<Long, Int>? {
        return mutex.withLock {
            countsCache["all_counts"]
        }
    }
    
    /**
     * Кеширование уведомлений
     */
    suspend fun cacheNotifications(notifications: List<Any>) {
        mutex.withLock {
            notificationsCache["all_notifications"] = notifications
        }
    }
    
    /**
     * Получение кешированных уведомлений
     */
    suspend fun getCachedNotifications(): List<Any>? {
        return mutex.withLock {
            notificationsCache["all_notifications"]
        }
    }
    
    /**
     * Очистка кеша
     */
    fun clearCache() {
        habitsCache.clear()
        completionsCache.clear()
        countsCache.clear()
        notificationsCache.clear()
    }
    
    /**
     * Очистка устаревших данных
     */
    suspend fun clearOldData() {
        mutex.withLock {
            // Очищаем данные старше 7 дней
            val cutoffDate = LocalDate.now().minusDays(7)
            completionsCache.keys.removeAll { dateString ->
                try {
                    LocalDate.parse(dateString).isBefore(cutoffDate)
                } catch (e: Exception) {
                    true
                }
            }
        }
    }
    
    /**
     * Получение размера кеша
     */
    suspend fun getCacheSize(): Int {
        return mutex.withLock {
            habitsCache.size + completionsCache.size + countsCache.size + notificationsCache.size
        }
    }
}

/**
 * Composable для управления кешем данных
 */
@Composable
fun rememberDataCacheManager(): DataCacheManager {
    return remember { DataCacheManager() }
}

/**
 * Composable для инициализации кеша данных
 */
@Composable
fun DataCacheInitializer(
    cacheManager: DataCacheManager
) {
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    
    // Очищаем устаревшие данные при создании
    LaunchedEffect(Unit) {
        cacheManager.clearOldData()
    }
    
    // Очищаем кеш при уничтожении
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) {
                cacheManager.clearCache()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
