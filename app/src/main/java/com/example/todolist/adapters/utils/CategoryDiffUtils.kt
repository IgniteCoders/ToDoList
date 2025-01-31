package com.example.todolist.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.todolist.data.entities.Category

class CategoryDiffUtils(private val oldList: List<Category>,
                        private val newList: List<Category>) : DiffUtil.Callback() {
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
            oldList[oldItemPosition].name != newList[newItemPosition].name -> false
            oldList[oldItemPosition].color != newList[newItemPosition].color -> false
            oldList[oldItemPosition].icon != newList[newItemPosition].icon -> false
            oldList[oldItemPosition].numberOfTasksDone != newList[newItemPosition].numberOfTasksDone -> false
            oldList[oldItemPosition].numberOfTasksTotal != newList[newItemPosition].numberOfTasksTotal -> false
            else -> true
        }
    }
}