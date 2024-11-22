package com.example.todolist.data.entities

import java.util.Calendar

data class Task(
    val id: Long,
    var title: String,
    var description: String = "",
    var reminder: Boolean = false,
    var allDay: Boolean = false,
    var date: Long = 0,
    var time: Long = 0,
    var done: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "Task"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_REMINDER = "reminder"
        const val COLUMN_NAME_ALL_DAY = "allDay"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_DONE = "done"
        val COLUMN_NAMES = arrayOf(
            COLUMN_ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_REMINDER,
            COLUMN_NAME_ALL_DAY,
            COLUMN_NAME_DATE,
            COLUMN_NAME_TIME,
            COLUMN_NAME_DONE
        )
    }

    fun getCalendar(): Calendar? {
        if (reminder) {
            val calendar = Calendar.getInstance()
            if (allDay) {
                calendar.timeInMillis = date
            } else {
                calendar.timeInMillis = date + time
            }
            return calendar
        } else {
            return null
        }
    }

    fun setCalendar(calendar: Calendar) {
        // Obtener los milisegundos de la fecha (sin la hora)
        val calendarDate = Calendar.getInstance()
        calendarDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendarDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        calendarDate.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        // Establecer la hora a las 00:00:00
        calendarDate.set(Calendar.HOUR_OF_DAY, 0)
        calendarDate.set(Calendar.MINUTE, 0)
        calendarDate.set(Calendar.SECOND, 0)
        calendarDate.set(Calendar.MILLISECOND, 0)

        // Obtener los milisegundos de la fecha (sin hora)
        val dateMillis: Long = calendarDate.timeInMillis

        // Obtener los milisegundos de la hora (solo la hora del día)
        val timeMillis: Long = calendar.timeInMillis - dateMillis

        // Guardamos los resultados
        date = dateMillis
        time = timeMillis

        // Mostrar los resultados
        println("Milisegundos de la fecha: $dateMillis")
        println("Milisegundos de la hora: $timeMillis")
    }
}
