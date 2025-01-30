package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ItemFilterBinding
import com.example.todolist.utils.removeTime
import java.util.Calendar

class FiltersAdapter(
    val onFilterClick: (Int) -> Unit
) : RecyclerView.Adapter<FiltersAdapter.FiltersViewHolder>() {

    companion object {
        const val FILTER_TODAY      = 0
        const val FILTER_SCHEDULED  = 1
        const val FILTER_ALL        = 2
        const val FILTER_PRIORITY   = 3
        const val FILTER_DONE       = 4
        const val FILTER_NEW        = 5
    }

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        holder.render()
        holder.binding.filterToday.setOnClickListener { onFilterClick(FILTER_TODAY) }
        holder.binding.filterScheduled.setOnClickListener { onFilterClick(FILTER_SCHEDULED) }
        holder.binding.filterAll.setOnClickListener { onFilterClick(FILTER_ALL) }
        holder.binding.filterPriority.setOnClickListener { onFilterClick(FILTER_PRIORITY) }
        holder.binding.filterCompleted.setOnClickListener { onFilterClick(FILTER_DONE) }
        holder.binding.filterNew.setOnClickListener { onFilterClick(FILTER_NEW) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FiltersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class FiltersViewHolder(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private val taskDAO = TaskDAO(context)

        fun render() {
            binding.numberOfTasksTodayTextView.text = taskDAO.countByDate(Calendar.getInstance().removeTime()).toString()
            binding.numberOfTasksScheduledTextView.text = taskDAO.countByReminder().toString()
            binding.numberOfTasksAllTextView.text = taskDAO.countAll().toString()
            binding.numberOfTasksPriorityTextView.text = taskDAO.countByPriority().toString()
            binding.numberOfTasksCompletedTextView.text = taskDAO.countByDone().toString()
        }
    }
}