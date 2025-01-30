package com.example.todolist.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.todolist.data.entities.Task

class TaskDiffUtils(private val oldList: List<Task>,
                    private val newList: List<Task>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].title != newList[newItemPosition].title -> false
            oldList[oldItemPosition].description != newList[newItemPosition].description -> false
            oldList[oldItemPosition].priority != newList[newItemPosition].priority -> false
            oldList[oldItemPosition].reminder != newList[newItemPosition].reminder -> false
            oldList[oldItemPosition].date != newList[newItemPosition].date -> false
            oldList[oldItemPosition].time != newList[newItemPosition].time -> false
            oldList[oldItemPosition].done != newList[newItemPosition].done -> false
            oldList[oldItemPosition].category.id != newList[newItemPosition].category.id -> false
            else -> true
        }
    }
}