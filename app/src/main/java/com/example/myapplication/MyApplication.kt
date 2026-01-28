package com.example.myapplication

import android.app.Application
import com.example.myapplication.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Создаём канал уведомлений при запуске приложения
        NotificationHelper.createNotificationChannel(this)
    }
}




