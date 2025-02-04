package com.example.todolist.utils

import android.app.NotificationManager
import android.graphics.Color

class Constants {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "REMINDER"
        const val NOTIFICATION_CHANNEL_NAME = "Reminder Channel"
        const val NOTIFICATION_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "This is the channel for task reminders notifications"
    }
}