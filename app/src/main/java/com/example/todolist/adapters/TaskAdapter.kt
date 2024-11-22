package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.entities.Task
import com.example.todolist.databinding.ItemTaskBinding

class TaskAdapter(
    var items: List<Task>,
    val onItemClick: (Int) -> Unit,
    val onItemCheck: (Int) -> Unit,
    val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (checkBox.isPressed) {
                onItemCheck(position)
            }
        }
        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<Task>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(task: Task) {
        binding.nameTextView.text = task.title
        binding.doneCheckBox.isChecked = task.done
    }
}