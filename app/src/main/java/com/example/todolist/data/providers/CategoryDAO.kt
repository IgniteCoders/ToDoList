package com.example.todolist.data.providers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.todolist.data.entities.Category
import com.example.todolist.managers.DatabaseManager

class CategoryDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun getContentValues(category: Category): ContentValues {
        return ContentValues().apply {
            put(Category.COLUMN_NAME_TITLE, category.name)
            put(Category.COLUMN_NAME_COLOR, category.color)
            put(Category.COLUMN_NAME_ICON, category.icon)
            put(Category.COLUMN_NAME_POSITION, category.position)
        }
    }

    fun cursorToEntity(cursor: Cursor): Category {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_TITLE))
        val color = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_COLOR))
        val icon = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_ICON))
        val position = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_POSITION))

        val category = Category(id, name, color, icon, position)

        val taskDAO = TaskDAO(context)
        category.numberOfTasksDone = taskDAO.countByCategoryAndDone(category, true)
        category.numberOfTasksTotal = taskDAO.countByCategory(category)

        return category
    }

    fun insert(category: Category) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(category)

        try {
            // Insert the new row, returning the primary key value of the new row
            val id = db.insert(Category.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun update(category: Category) {
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(category)

        try {
            // Update the existing rows, returning the number of affected rows
            val updatedRows = db.update(Category.TABLE_NAME, values, "${Category.COLUMN_NAME_ID} = ${category.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun delete(category: Category) {
        open()

        try {
            // Delete the existing row, returning the number of affected rows
            val deletedRows = db.delete(Category.TABLE_NAME, "${Category.COLUMN_NAME_ID} = ${category.id}", null)
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
    }

    fun findById(id: Long) : Category? {
        open()

        try {
            val cursor = db.query(
                Category.TABLE_NAME,                    // The table to query
                Category.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                "${Category.COLUMN_NAME_ID} = $id",  // The columns for the WHERE clause
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

    fun findAll() : List<Category> {
        open()

        var list: MutableList<Category> = mutableListOf()

        try {
            val cursor = db.query(
                Category.TABLE_NAME,                    // The table to query
                Category.COLUMN_NAMES,                  // The array of columns to return (pass null to get all)
                null,                       // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                         // don't filter by row groups
                "${Category.COLUMN_NAME_POSITION}"                        // The sort order
            )

            while (cursor.moveToNext()) {
                val category = cursorToEntity(cursor)
                list.add(category)
            }
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return list
    }

    fun countAll(): Int {
        open()

        var count = 0

        try {
            val cursor = db.query(
                Category.TABLE_NAME,                 // The table to query
                arrayOf("COUNT(*)"),     // The array of columns to return (pass null to get all)
                null,                // The columns for the WHERE clause
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