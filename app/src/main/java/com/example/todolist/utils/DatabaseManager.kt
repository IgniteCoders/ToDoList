package com.example.todolist.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "ToDoListDatabase.db"


        private const val SQL_CREATE_TABLE_TASK =
            "CREATE TABLE ${Task.TABLE_NAME} (" +
                    "${Task.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Task.COLUMN_NAME_TITLE} TEXT," +
                    "${Task.COLUMN_NAME_DESCRIPTION} TEXT," +
                    "${Task.COLUMN_NAME_REMINDER} BOOLEAN," +
                    "${Task.COLUMN_NAME_ALL_DAY} BOOLEAN," +
                    "${Task.COLUMN_NAME_DATE} INTEGER," +
                    "${Task.COLUMN_NAME_TIME} INTEGER," +
                    "${Task.COLUMN_NAME_PRIORITY} INTEGER," +
                    "${Task.COLUMN_NAME_DONE} BOOLEAN," +
                    "${Task.COLUMN_NAME_CATEGORY} INTEGER," +
                    "CONSTRAINT fk_category " +
                    "FOREIGN KEY(${Task.COLUMN_NAME_CATEGORY}) " +
                    "REFERENCES ${Category.TABLE_NAME}(${Category.COLUMN_NAME_ID}) ON DELETE CASCADE)"

        private const val SQL_DELETE_TABLE_TASK = "DROP TABLE IF EXISTS ${Task.TABLE_NAME}"

        private const val SQL_CREATE_TABLE_CATEGORY =
            "CREATE TABLE ${Category.TABLE_NAME} (" +
                    "${Category.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Category.COLUMN_NAME_TITLE} TEXT," +
                    "${Category.COLUMN_NAME_COLOR} INTEGER," +
                    "${Category.COLUMN_NAME_ICON} INTEGER)"

        private const val SQL_DELETE_TABLE_CATEGORY = "DROP TABLE IF EXISTS ${Category.TABLE_NAME}"
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;");
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_CATEGORY)
        db.execSQL(SQL_CREATE_TABLE_TASK)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //onDestroy(db)
        //onCreate(db)
        //db.execSQL("ALTER TABLE ${Task.TABLE_NAME} ADD COLUMN ${Task.COLUMN_NAME_CATEGORY} INTEGER")
    }

    private fun onDestroy(db: SQLiteDatabase) {
        db.execSQL(SQL_DELETE_TABLE_TASK)
        db.execSQL(SQL_DELETE_TABLE_CATEGORY)
    }
}