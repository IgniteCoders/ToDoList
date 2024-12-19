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
            Color.parseColor("#fe453a"),
            Color.parseColor("#ff9e0b"),
            Color.parseColor("#ffd50b"),
            Color.parseColor("#2fd05b"),
            Color.parseColor("#79c3fe"),
            Color.parseColor("#0b84fd"),
            Color.parseColor("#5d5be5"),
            Color.parseColor("#fe4f79"),
            Color.parseColor("#d57ff4"),
            Color.parseColor("#c8a576"),
            Color.parseColor("#757e86"),
            Color.parseColor("#ebb5ae")
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