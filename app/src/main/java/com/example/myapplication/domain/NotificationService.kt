package com.example.myapplication.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.util.concurrent.atomic.AtomicInteger

class NotificationService(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID_TASKS = "task_notifications"
        const val CHANNEL_ID_GOALS = "goal_notifications"
        const val GROUP_KEY_TASKS = "task_group"
        const val GROUP_KEY_GOALS = "goal_group"
    }
    
    private val notificationIdGenerator = AtomicInteger(1000)
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Канал для уведомлений о выполнении задач
            val taskChannel = NotificationChannel(
                CHANNEL_ID_TASKS,
                "Уведомления о задачах",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о выполненных задачах"
                enableVibration(true)
                enableLights(true)
            }
            
            // Канал для уведомлений о достижении целей
            val goalChannel = NotificationChannel(
                CHANNEL_ID_GOALS,
                "Уведомления о целях",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о достигнутых целях"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(goalChannel)
        }
    }
    
    fun showTaskCompletedNotification(taskName: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("open_notifications", true)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationIdGenerator.getAndIncrement(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASKS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Задача выполнена! 🎉")
                .setContentText("Поздравляем! Вы выполнили задачу: $taskName")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Отличная работа! Вы успешно выполнили задачу \"$taskName\". Продолжайте в том же духе!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setGroup(GROUP_KEY_TASKS)
                .setGroupSummary(false)
                .build()
            
            with(NotificationManagerCompat.from(context)) {
                if (areNotificationsEnabled()) {
                    notify(notificationIdGenerator.getAndIncrement(), notification)
                }
            }
        } catch (_: Exception) {
        }
    }
    
    fun showGoalAchievedNotification(taskName: String, targetDays: Int) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("open_notifications", true)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationIdGenerator.getAndIncrement(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_GOALS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Цель достигнута! ⭐")
                .setContentText("Поздравляем! Вы достигли цели по задаче: $taskName")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Невероятно! Вы выполнили задачу \"$taskName\" $targetDays дней подряд и достигли поставленной цели! Это настоящий успех!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setGroup(GROUP_KEY_GOALS)
                .setGroupSummary(false)
                .build()
            
            with(NotificationManagerCompat.from(context)) {
                if (areNotificationsEnabled()) {
                    notify(notificationIdGenerator.getAndIncrement(), notification)
                }
            }
        } catch (_: Exception) {
        }
    }
    
    private fun areNotificationsEnabled(): Boolean {
        return try {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (_: Exception) {
            false
        }
    }
}
