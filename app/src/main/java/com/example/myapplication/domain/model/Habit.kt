package com.example.myapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val color: String = "#2196F3",
    val icon: String = "task",
    val targetDays: Int = 30,
    val isActive: Boolean = true,
    val scheduledTime: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey 
    val id: Long = 1,
    @ColumnInfo(name = "name") 
    val name: String = "Гость",
    @ColumnInfo(name = "createdAt") 
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class SimpleNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String,
    val habitId: Long? = null,
    val habitTitle: String? = null,
    val createdAt: String,
    val isRead: Boolean = false
)
