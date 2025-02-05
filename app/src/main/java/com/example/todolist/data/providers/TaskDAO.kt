package com.example.todolist.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task
import com.example.todolist.managers.DatabaseManager
import java.util.Calendar

class TaskDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    private fun getContentValues(task: Task): ContentValues {
        return ContentValues().apply {
            put(Task.COLUMN_NAME_TITLE, task.title)
            put(Task.COLUMN_NAME_DESCRIPTION, task.description)
            put(Task.COLUMN_NAME_REMINDER, task.reminder)
            put(Task.COLUMN_NAME_ALL_DAY, task.allDay)
            put(Task.COLUMN_NAME_DATE, task.date)
            put(Task.COLUMN_NAME_TIME, task.time)
            put(Task.COLUMN_NAME_PRIORITY, task.priority)
            put(Task.COLUMN_NAME_DONE, task.done)
            put(Task.COLUMN_NAME_CATEGORY, task.category.id)
        }
    }

    private fun cursorToEntity(cursor: Cursor): Task {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DESCRIPTION))
        val reminder = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_REMINDER)) != 0
        val allDay = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ALL_DAY)) != 0
        val date = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DATE))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TIME))
        val priority = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_PRIORITY))
        val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0
        val categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_CATEGORY))

        val category = CategoryDAO(context).findById(categoryId)!!
        return Task(id, name, description, reminder, allDay, date, time, priority, done, category)
    }

    fun insert(task: Task): Long {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Task.TABLE_NAME, null, values)
            task.id = id
            return id
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
            return -1
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
            val updatedRows = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_NAME_ID} = ${task.id}", null)
            Log.i("DB", "Updated $updatedRows rows in ${Task.TABLE_NAME}")
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
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_NAME_ID} = ${task.id}", null)
            Log.i("DB", "Deleted $deletedRows rows in ${Task.TABLE_NAME}")
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
                "${Task.COLUMN_NAME_ID} = $id",  // The columns for the WHERE clause
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
        return findAllBy(null)
    }

    fun findAllByTitle(title: String) : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_TITLE} LIKE '%$title%'")
    }

    fun findAllByCategory(category: Category) : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_CATEGORY} = ${category.id}")
    }

    fun findAllByDate(date: Calendar) : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_REMINDER} = true AND ${Task.COLUMN_NAME_DATE} = ${date.timeInMillis}")
    }

    fun findAllByReminder() : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_REMINDER} = true")
    }

    fun findAllByPriority() : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_PRIORITY} > 0")
    }

    fun findAllByDone() : List<Task> {
        return findAllBy("${Task.COLUMN_NAME_DONE} = true")
    }

    fun findAllBy(where: String?) : List<Task> {
        open()

        val list: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,                    // The table to query
                Task.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                where,                       // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                "${Task.COLUMN_NAME_DONE}, ${Task.COLUMN_NAME_DATE}, ${Task.COLUMN_NAME_TIME}"                        // The sort order
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

    fun countAll(): Int {
        return countBy(null)
    }

    fun countByCategory(category: Category): Int {
        return countBy("${Task.COLUMN_NAME_CATEGORY} = ${category.id}")
    }

    fun countByCategoryAndDone(category: Category, done: Boolean): Int {
        return countBy("${Task.COLUMN_NAME_CATEGORY} = ${category.id} AND ${Task.COLUMN_NAME_DONE} = $done")
    }

    fun countByPriority(): Int {
        return countBy("${Task.COLUMN_NAME_PRIORITY} > 0")
    }

    fun countByReminder(): Int {
        return countBy("${Task.COLUMN_NAME_REMINDER} = true")
    }

    fun countByDone(): Int {
        return countBy("${Task.COLUMN_NAME_DONE} = true")
    }

    fun countByDate(date: Calendar): Int {
        return countBy("${Task.COLUMN_NAME_REMINDER} = true AND ${Task.COLUMN_NAME_DATE} = ${date.timeInMillis}")
    }

    fun countBy(where: String?): Int {
        open()

        var count = 0

        try {
            val cursor = db.query(
                Task.TABLE_NAME,                 // The table to query
                arrayOf("COUNT(*)"),     // The array of columns to return (pass null to get all)
                where,                // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )
            if (cursor.moveToNext()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }

        return count
    }

}