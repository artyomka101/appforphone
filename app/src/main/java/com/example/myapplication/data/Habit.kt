package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val color: String = "#2196F3", // Сохраняем как строку
    val icon: String = "task", // Сохраняем как строку
    val targetDays: Int = 30, // цель в днях
    val isActive: Boolean = true,
    val scheduledTime: String = "", // время в формате HH:mm
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // дата в формате "yyyy-MM-dd"
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    @ColumnInfo(name = "name") val name: String = "Гость",
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
