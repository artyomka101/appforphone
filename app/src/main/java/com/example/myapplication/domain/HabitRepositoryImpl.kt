package com.example.myapplication.domain

import com.example.myapplication.data.Habit
import com.example.myapplication.data.HabitCompletion
import com.example.myapplication.data.HabitDao
import com.example.myapplication.data.Notification
import com.example.myapplication.data.UserProfile
import kotlinx.coroutines.flow.Flow

class HabitRepositoryImpl(
    private val habitDao: HabitDao
) : HabitRepository {
    
    override fun getAllActiveHabits(): Flow<List<Habit>> = habitDao.getAllActiveHabits()
    
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
            // Если привычка уже выполнена в этот день, удаляем запись
            habitDao.deleteHabitCompletion(existingCompletion)
        } else {
            // Если привычка не выполнена, добавляем запись
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
    
    // Реализация методов для уведомлений
    override fun getAllNotifications(): Flow<List<Notification>> = habitDao.getAllNotifications()
    
    override suspend fun insertNotification(notification: Notification) = habitDao.insertNotification(notification)
    
    override suspend fun markNotificationAsRead(notificationId: Long) = habitDao.markNotificationAsRead(notificationId)
    
    override suspend fun deleteNotification(notificationId: Long) = habitDao.deleteNotification(notificationId)
    
    override suspend fun clearAllNotifications() = habitDao.clearAllNotifications()

    // Профиль
    override fun getUserProfile(): Flow<UserProfile?> = habitDao.getUserProfileFlow()

    override suspend fun loadUserProfileOnce(): UserProfile? = habitDao.getUserProfile()

    override suspend fun upsertUserProfile(profile: UserProfile) = habitDao.upsertUserProfile(profile)
}
