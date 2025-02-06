package com.example.todolist.managers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todolist.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionManager(private val activity: Activity) {

    companion object {
        // Constantes para códigos de solicitud
        const val REQUEST_PERMISSION_POST_NOTIFICATIONS = 101
        const val REQUEST_PERMISSION_USE_EXACT_ALARM = 102
        const val REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM = 103
    }

    fun checkNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 API 33
            val permissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_POST_NOTIFICATIONS)
            }
        }
    }

    fun checkUseExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 API 33
            val permissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.USE_EXACT_ALARM)
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.USE_EXACT_ALARM)
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_USE_EXACT_ALARM)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 API 31
            val permissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.SCHEDULE_EXACT_ALARM)
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM)
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso fue concedido, puedes proceder a mostrar las notificaciones
                } else {
                    // El permiso fue denegado, puedes mostrar un mensaje o redirigir a la configuración
                    goToSettings(REQUEST_PERMISSION_POST_NOTIFICATIONS)
                }
            }
            REQUEST_PERMISSION_USE_EXACT_ALARM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso para alarmas exactas concedido, procede con AlarmManager
                } else {
                    // El permiso fue denegado, muestra un mensaje o configura una alternativa
                    goToSettings(REQUEST_PERMISSION_USE_EXACT_ALARM)
                }
            }
            REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso para alarmas exactas concedido, procede con AlarmManager
                } else {
                    // El permiso fue denegado, muestra un mensaje o configura una alternativa
                    goToSettings(REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM)
                }
            }
            // Otros permisos
        }
    }

    private fun goToSettings(requestCode: Int) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.alert_dialog_permissions_title)
            .setMessage(R.string.alert_dialog_permissions_message)
            .setPositiveButton(R.string.action_settings) { dialog, _ ->
                when (requestCode) {
                    REQUEST_PERMISSION_POST_NOTIFICATIONS -> {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                        activity.startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.setData(Uri.parse("package:${activity.packageName}"))
                        activity.startActivity(intent)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_settings)
            .show()
    }
}