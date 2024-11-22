package com.example.todolist.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.todolist.data.entities.Task
import com.example.todolist.utils.DatabaseManager

class TaskDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun getContentValues(task: Task): ContentValues {
        return ContentValues().apply {
            put(Task.COLUMN_NAME_TITLE, task.title)
            put(Task.COLUMN_NAME_DESCRIPTION, task.description)
            put(Task.COLUMN_NAME_REMINDER, task.reminder)
            put(Task.COLUMN_NAME_ALL_DAY, task.allDay)
            put(Task.COLUMN_NAME_DATE, task.date)
            put(Task.COLUMN_NAME_TIME, task.time)
            put(Task.COLUMN_NAME_DONE, task.done)
        }
    }

    fun cursorToEntity(cursor: Cursor): Task {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DESCRIPTION))
        val reminder = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_REMINDER)) != 0
        val allDay = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ALL_DAY)) != 0
        val date = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DATE))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TIME))
        val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0

        return Task(id, name, description, reminder, allDay, date, time, done)
    }

    fun insert(task: Task) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Task.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun update(task: Task) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Update the existing rows, returning the number of affected rows
            val updatedRows = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_ID} = ${task.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun delete(task: Task) {
        open()

        try {
            // Delete the existing row, returning the number of affected rows
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_ID} = ${task.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun findById(id: Long) : Task? {
        open()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,                    // The table to query
                Task.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                "${Task.COLUMN_ID} = $id",  // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                null                         // The sort order
            )

            if (cursor.moveToNext()) {
                return cursorToEntity(cursor)
            }
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return null
    }

    fun findAll() : List<Task> {
        open()

        var list: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,                    // The table to query
                Task.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                null,                       // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                null                         // The sort order
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                list.add(task)
            }
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return list
    }

}