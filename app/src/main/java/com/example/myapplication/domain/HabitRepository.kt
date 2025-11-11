package com.example.myapplication.domain

import com.example.myapplication.data.Habit
import com.example.myapplication.data.Notification
import com.example.myapplication.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllActiveHabits(): Flow<List<Habit>>
    suspend fun getHabitById(habitId: Long): Habit?
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun deactivateHabit(habitId: Long)
    
    suspend fun isHabitCompletedOnDate(habitId: Long, date: String): Boolean
    suspend fun toggleHabitCompletion(habitId: Long, date: String)
    suspend fun getCompletionCount(habitId: Long): Int
    
    // Методы для уведомлений
    fun getAllNotifications(): Flow<List<Notification>>
    suspend fun insertNotification(notification: Notification)
    suspend fun markNotificationAsRead(notificationId: Long)
    suspend fun deleteNotification(notificationId: Long)
    suspend fun clearAllNotifications()

    // Профиль
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun loadUserProfileOnce(): UserProfile?
    suspend fun upsertUserProfile(profile: UserProfile)
}
