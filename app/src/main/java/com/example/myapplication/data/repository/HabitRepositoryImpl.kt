package com.example.myapplication.data.repository

import com.example.myapplication.data.local.HabitDao
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class HabitRepositoryImpl(
    private val habitDao: HabitDao
) : HabitRepository {
    
    override fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    
    override fun getAllActiveHabits(): Flow<List<Habit>> = habitDao.getAllActiveHabits()
    
    override fun getArchivedHabits(): Flow<List<Habit>> = habitDao.getArchivedHabits()
    
    override suspend fun getHabitById(habitId: Long): Habit? = habitDao.getHabitById(habitId)
    
    override suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    
    override suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    
    override suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    
    override suspend fun deactivateHabit(habitId: Long) = habitDao.deactivateHabit(habitId)
    
    override suspend fun isHabitCompletedOnDate(habitId: Long, date: String): Boolean {
        val completion = habitDao.getHabitCompletion(habitId, date)
        return completion != null
    }
    
    override suspend fun toggleHabitCompletion(habitId: Long, date: String) {
        val existingCompletion = habitDao.getHabitCompletion(habitId, date)
        
        if (existingCompletion != null) {
            habitDao.deleteHabitCompletion(existingCompletion)
        } else {
            val completion = HabitCompletion(
                habitId = habitId,
                date = date,
                completedAt = System.currentTimeMillis()
            )
            habitDao.insertHabitCompletion(completion)
        }
    }
    
    override suspend fun getCompletionCount(habitId: Long): Int {
        return habitDao.getCompletionCount(habitId)
    }
    
    override fun getAllNotifications(): Flow<List<SimpleNotification>> = habitDao.getAllNotifications()
    
    override suspend fun insertNotification(notification: SimpleNotification) = habitDao.insertNotification(notification)
    
    override suspend fun markNotificationAsRead(notificationId: Long) = habitDao.markNotificationAsRead(notificationId)
    
    override suspend fun deleteNotification(notificationId: Long) = habitDao.deleteNotification(notificationId)
    
    override suspend fun clearAllNotifications() = habitDao.clearAllNotifications()

    override fun getUserProfile(): Flow<UserProfile?> = habitDao.getUserProfileFlow()

    override suspend fun loadUserProfileOnce(): UserProfile? = habitDao.getUserProfile()

    override suspend fun upsertUserProfile(profile: UserProfile) = habitDao.upsertUserProfile(profile)
}
