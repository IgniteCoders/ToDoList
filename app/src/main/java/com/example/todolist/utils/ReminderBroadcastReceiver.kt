package com.example.todolist.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.activities.MainActivity
import com.example.todolist.activities.TaskActivity
import com.example.todolist.activities.TasksActivity
import com.example.todolist.data.providers.TaskDAO

class ReminderBroadcastReceiver: BroadcastReceiver() {

    companion object {
        const val TASK_ID = "TASK_ID"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val id = intent.getLongExtra(TASK_ID, -1L)
            createNotification(context, id)
        }
    }

    private fun createNotification(context: Context, id: Long) {
        val task = TaskDAO(context).findById(id)

        task?.let {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Creating a notification channel
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                Constants.NOTIFICATION_CHANNEL_IMPORTANCE
            )
            channel.description = Constants.NOTIFICATION_CHANNEL_DESCRIPTION
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(500, 500, 100, 300, 100, 300)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM) // Esto es importante para que se reproduzca como sonido de alarma
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // El tipo de contenido
                .build()
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)

            notificationManager.createNotificationChannel(channel)


            val intent = Intent(context, TaskActivity::class.java).apply {
                //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(TaskActivity.EXTRA_TASK_ID, id)
            }
            val intentList = Intent(context, TasksActivity::class.java).apply {
                //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(TasksActivity.EXTRA_CATEGORY_ID, task.category.id)
            }
            val intentHome = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Creating the navigation stack
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.addNextIntent(intentHome)
            stackBuilder.addNextIntent(intentList)
            stackBuilder.addNextIntent(intent)
            val pendingIntent = stackBuilder.getPendingIntent(id.toInt(), PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            // Creating the notification
            //val pendingIntent: PendingIntent = PendingIntent.getActivity(context, id.toInt(), intent, PendingIntent.FLAG_MUTABLE)

            val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_task_notification)
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(task.description))
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(id.toInt(), notification)
        }
    }
}