package com.example.todolist.data

data class Task(
    val id: Long,
    var name: String,
    var done: Boolean
) {
    companion object {
        const val TABLE_NAME = "Task"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DONE = "done"
    }

}
