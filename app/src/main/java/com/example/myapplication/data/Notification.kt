package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "TASK_COMPLETED" или "GOAL_ACHIEVED"
    val habitId: Long? = null,
    val habitTitle: String? = null,
    val createdAt: String, // Используем String вместо LocalDateTime для простоты
    val isRead: Boolean = false
)
