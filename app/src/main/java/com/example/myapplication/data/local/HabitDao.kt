package com.example.myapplication.data.local

import androidx.room.*
import com.example.myapplication.domain.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE isActive = 0 ORDER BY createdAt DESC")
    fun getArchivedHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): Habit?
    
    @Insert
    suspend fun insertHabit(habit: Habit): Long
    
    @Update
    suspend fun updateHabit(habit: Habit)
    
    @Delete
    suspend fun deleteHabit(habit: Habit)
    
    @Query("UPDATE habits SET isActive = 0 WHERE id = :habitId")
    suspend fun deactivateHabit(habitId: Long)
    
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun getHabitCompletion(habitId: Long, date: String): HabitCompletion?
    
    @Insert
    suspend fun insertHabitCompletion(completion: HabitCompletion)
    
    @Delete
    suspend fun deleteHabitCompletion(completion: HabitCompletion)
    
    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId")
    suspend fun getCompletionCount(habitId: Long): Int
    
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<SimpleNotification>>
    
    @Insert
    suspend fun insertNotification(notification: SimpleNotification)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Long)
    
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Long)
    
    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile)
}
