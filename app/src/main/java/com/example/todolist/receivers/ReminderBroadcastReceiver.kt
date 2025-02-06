package com.example.todolist.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.managers.NotificationManager

class ReminderBroadcastReceiver: BroadcastReceiver() {

    companion object {
        const val TASK_ID = "TASK_ID"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val id = intent.getLongExtra(TASK_ID, -1L)
            val task = TaskDAO(context).findById(id)
            task?.let {
                val notificationManager = NotificationManager(context)
                notificationManager.createNotificationChannel()
                notificationManager.createNotification(task)
            }
        }
    }
}