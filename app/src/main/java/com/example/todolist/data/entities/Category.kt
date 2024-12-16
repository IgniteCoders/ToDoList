package com.example.todolist.data.entities

import android.graphics.Color
import com.example.todolist.R

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

        val colors = arrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLACK,
            Color.WHITE
        )

        val icons = arrayOf(
            R.drawable.ic_clock,
            R.drawable.ic_calendar,
            R.drawable.ic_close,
            R.drawable.ic_box_checked,
            R.drawable.ic_box_unchecked,
            R.drawable.ic_save,
        )
    }

    override fun equals(other: Any?): Boolean{
        if(other is Category){
            return id == other.id
        }
        return false;
    }
}