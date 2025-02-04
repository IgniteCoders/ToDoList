package com.example.todolist.managers

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    companion object {
        const val SHOW_COMPLETED_TASKS = "SHOW_COMPLETED_TASKS"
    }

    private val sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences("to_do_list_session", Context.MODE_PRIVATE)
    }

    fun setShowCompletedTasks(show: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(SHOW_COMPLETED_TASKS, show)
        editor.apply()
    }

    fun getShowCompletedTasks() : Boolean {
        return sharedPref.getBoolean(SHOW_COMPLETED_TASKS, false)
    }
}