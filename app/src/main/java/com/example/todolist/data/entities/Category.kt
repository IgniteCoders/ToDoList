package com.example.todolist.data.entities

data class Category (
    var id: Long,
    var name: String,
    var color: Int,
    var icon: Int
) {

    companion object {
        const val TABLE_NAME = "Category"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "name"
        const val COLUMN_NAME_COLOR = "color"
        const val COLUMN_NAME_ICON = "icon"
        val COLUMN_NAMES = arrayOf(
            COLUMN_NAME_ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_COLOR,
            COLUMN_NAME_ICON
        )
    }

    override fun equals(other: Any?): Boolean{
        if(other is Category){
            return id == other.id
        }
        return false;
    }
}