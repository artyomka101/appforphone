package com.example.myapplication.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.*

/**
 * УЛЬТРА-БЫСТРОЕ КЕШИРОВАНИЕ АНИМАЦИЙ
 * Система предзагрузки и кеширования анимаций для мгновенных переходов
 */
class AnimationCacheManager {
    
    // Кеш для анимаций
    private val animationCache = mutableMapOf<String, AnimationSpec<Float>>()
    
    // Кеш для spring анимаций
    private val springCache = mutableMapOf<String, SpringSpec<Float>>()
    
    // Кеш для tween анимаций
    private val tweenCache = mutableMapOf<String, TweenSpec<Float>>()
    
    
    init {
        initializeAnimationCache()
    }
    
    /**
     * Инициализация кеша анимаций
     */
    private fun initializeAnimationCache() {
        // УЛЬТРА-БЫСТРЫЕ анимации для мгновенных переходов
        animationCache["ultra_fast_fade"] = tween(50, easing = FastOutSlowInEasing)
        animationCache["ultra_fast_slide"] = tween(75, easing = FastOutSlowInEasing)
        animationCache["ultra_fast_scale"] = tween(100, easing = FastOutSlowInEasing)
        
        // БЫСТРЫЕ анимации для плавных переходов
        animationCache["fast_fade"] = tween(150, easing = FastOutSlowInEasing)
        animationCache["fast_slide"] = tween(200, easing = FastOutSlowInEasing)
        animationCache["fast_scale"] = tween(250, easing = FastOutSlowInEasing)
        
        // ПЛАВНЫЕ анимации для комфортных переходов
        animationCache["smooth_fade"] = tween(300, easing = FastOutSlowInEasing)
        animationCache["smooth_slide"] = tween(400, easing = FastOutSlowInEasing)
        animationCache["smooth_scale"] = tween(500, easing = FastOutSlowInEasing)
        
        // SPRING анимации для естественных переходов
        springCache["bouncy_spring"] = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
        
        springCache["smooth_spring"] = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
        
        springCache["ultra_smooth_spring"] = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
        
        // TWEEN анимации для точного контроля
        tweenCache["ultra_fast_tween"] = tween(25, easing = LinearEasing)
        tweenCache["fast_tween"] = tween(100, easing = FastOutSlowInEasing)
        tweenCache["smooth_tween"] = tween(300, easing = FastOutSlowInEasing)
    }
    
    /**
     * Получение кешированной анимации
     */
    fun getCachedAnimation(key: String): AnimationSpec<Float>? {
        return animationCache[key]
    }
    
    /**
     * Получение кешированной spring анимации
     */
    fun getCachedSpring(key: String): SpringSpec<Float>? {
        return springCache[key]
    }
    
    /**
     * Получение кешированной tween анимации
     */
    fun getCachedTween(key: String): TweenSpec<Float>? {
        return tweenCache[key]
    }
    
    /**
     * Создание оптимизированной анимации
     */
    fun createOptimizedAnimation(
        duration: Int = 150,
        easing: Easing = FastOutSlowInEasing
    ): AnimationSpec<Float> {
        return tween(duration, easing = easing)
    }
    
    /**
     * Создание оптимизированной spring анимации
     */
    fun createOptimizedSpring(
        dampingRatio: Float = Spring.DampingRatioNoBouncy,
        stiffness: Float = Spring.StiffnessHigh
    ): AnimationSpec<Float> {
        return spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        )
    }
    
    /**
     * Очистка кеша
     */
    fun clearCache() {
        animationCache.clear()
        springCache.clear()
        tweenCache.clear()
    }
}

/**
 * Composable для управления кешем анимаций
 */
@Composable
fun rememberAnimationCacheManager(): AnimationCacheManager {
    return remember { AnimationCacheManager() }
}

/**
 * УЛЬТРА-БЫСТРЫЕ анимации для мгновенных переходов
 */
object UltraFastAnimations {
    val fadeIn: TweenSpec<Float> = tween(50, easing = FastOutSlowInEasing)
    val fadeOut: TweenSpec<Float> = tween(50, easing = FastOutSlowInEasing)
    val slideIn: TweenSpec<Float> = tween(75, easing = FastOutSlowInEasing)
    val slideOut: TweenSpec<Float> = tween(75, easing = FastOutSlowInEasing)
    val scaleIn: TweenSpec<Float> = tween(100, easing = FastOutSlowInEasing)
    val scaleOut: TweenSpec<Float> = tween(100, easing = FastOutSlowInEasing)
}

/**
 * БЫСТРЫЕ анимации для плавных переходов
 */
object FastAnimations {
    val fadeIn: TweenSpec<Float> = tween(150, easing = FastOutSlowInEasing)
    val fadeOut: TweenSpec<Float> = tween(150, easing = FastOutSlowInEasing)
    val slideIn: TweenSpec<Float> = tween(200, easing = FastOutSlowInEasing)
    val slideOut: TweenSpec<Float> = tween(200, easing = FastOutSlowInEasing)
    val scaleIn: TweenSpec<Float> = tween(250, easing = FastOutSlowInEasing)
    val scaleOut: TweenSpec<Float> = tween(250, easing = FastOutSlowInEasing)
}

/**
 * ПЛАВНЫЕ анимации для комфортных переходов
 */
object SmoothAnimations {
    val fadeIn: TweenSpec<Float> = tween(300, easing = FastOutSlowInEasing)
    val fadeOut: TweenSpec<Float> = tween(300, easing = FastOutSlowInEasing)
    val slideIn: TweenSpec<Float> = tween(400, easing = FastOutSlowInEasing)
    val slideOut: TweenSpec<Float> = tween(400, easing = FastOutSlowInEasing)
    val scaleIn: TweenSpec<Float> = tween(500, easing = FastOutSlowInEasing)
    val scaleOut: TweenSpec<Float> = tween(500, easing = FastOutSlowInEasing)
}

/**
 * SPRING анимации для естественных переходов
 */
object SpringAnimations {
    val bouncy: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val smooth: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
    
    val ultraSmooth: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
}