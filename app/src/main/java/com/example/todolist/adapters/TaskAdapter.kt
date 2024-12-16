package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.entities.Task
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.utils.TaskDiffUtils
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.getFormattedDateTime
import com.example.todolist.utils.getFormattedTime
import com.example.todolist.utils.isBeforeToday
import com.example.todolist.utils.isToday
import com.example.todolist.utils.isTomorrow
import com.example.todolist.utils.isYesterday
import java.text.DateFormat
import java.util.Locale

class TaskAdapter(
    var items: List<Task>,
    val onItemClick: (Int) -> Unit,
    val onItemCheck: (Int) -> Unit,
    val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (checkBox.isPressed) {
                onItemCheck(holder.adapterPosition)
            }
        }
        /*holder.binding.deleteButton.setOnClickListener {
            onItemDelete(holder.adapterPosition)
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<Task>) {
        val diffUtils = TaskDiffUtils(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }



    class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private lateinit var task: Task

        fun render(task: Task) {
            this.task = task

            binding.nameTextView.text = task.title
            binding.doneCheckBox.isChecked = task.done

            binding.priorityImageView.setColorFilter(context.getColor(task.getPriorityColor()))
            when (task.priority) {
                0 -> binding.priorityImageView.visibility = View.GONE
                1, 2 -> binding.priorityImageView.visibility = View.VISIBLE
            }

            task.getCalendar()?.let { calendar ->
                var dateText = if (calendar.isToday()) {
                    context.getString(R.string.calendar_today)
                } else if (calendar.isTomorrow()) {
                    context.getString(R.string.calendar_tomorrow)
                } else if (calendar.isYesterday()) {
                    context.getString(R.string.calendar_yesterday)
                } else {
                    calendar.getFormattedDate()
                }
                if (!task.allDay) {
                    dateText += " " + calendar.getFormattedTime()
                }
                binding.dateTextView.text = dateText

                if (calendar.isBeforeToday()) {
                    binding.dateTextView.setTextColor(context.getColor(R.color.expired_date))
                } else {
                    binding.dateTextView.setTextColor(context.getColor(R.color.gray))
                }
            }

            binding.categoryView.setColorFilter(task.category.color)
            binding.categoryView.setImageResource(task.category.icon)
        }

        fun isChecked (): Boolean {
            return task.done
        }
    }
}