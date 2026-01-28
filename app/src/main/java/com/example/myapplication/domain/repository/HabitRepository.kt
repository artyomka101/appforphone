package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.*
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(): Flow<List<Habit>>
    fun getAllActiveHabits(): Flow<List<Habit>>
    fun getArchivedHabits(): Flow<List<Habit>>
    suspend fun getHabitById(habitId: Long): Habit?
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun deactivateHabit(habitId: Long)
    
    suspend fun isHabitCompletedOnDate(habitId: Long, date: String): Boolean
    suspend fun toggleHabitCompletion(habitId: Long, date: String)
    suspend fun getCompletionCount(habitId: Long): Int
    
    fun getAllNotifications(): Flow<List<SimpleNotification>>
    suspend fun insertNotification(notification: SimpleNotification)
    suspend fun markNotificationAsRead(notificationId: Long)
    suspend fun deleteNotification(notificationId: Long)
    suspend fun clearAllNotifications()

    fun getUserProfile(): Flow<UserProfile?>
    suspend fun loadUserProfileOnce(): UserProfile?
    suspend fun upsertUserProfile(profile: UserProfile)
}
