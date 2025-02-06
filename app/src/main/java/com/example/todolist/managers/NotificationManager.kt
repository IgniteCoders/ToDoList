package com.example.todolist.managers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.activities.MainActivity
import com.example.todolist.activities.TaskActivity
import com.example.todolist.activities.TasksActivity
import com.example.todolist.data.entities.Task
import com.example.todolist.receivers.ReminderBroadcastReceiver
import com.example.todolist.utils.Constants
import java.util.Calendar

class NotificationManager(val context: Context) {

    fun scheduleNotification(task: Task) {
        val intent = Intent(context.applicationContext, ReminderBroadcastReceiver::class.java)
        intent.putExtra(ReminderBroadcastReceiver.TASK_ID, task.id)

        val pendingIntentFlags = if (task.reminder) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            task.id.toInt(),
            intent,
            pendingIntentFlags
        )

        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        if (task.reminder) {
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getCalendar().timeInMillis, pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 10000, pendingIntent)
        } else if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun createNotificationChannel() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
    }

    fun createNotification(task: Task) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, TaskActivity::class.java).apply {
            //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(TaskActivity.EXTRA_TASK_ID, task.id)
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
        val pendingIntent = stackBuilder.getPendingIntent(task.id.toInt(), PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Creating the notification
        //val pendingIntent: PendingIntent = PendingIntent.getActivity(context, id.toInt(), intent, PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task_notification)
            .setContentTitle(task.title)
            .setContentText(task.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(task.description))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(task.id.toInt(), notification)
    }
}