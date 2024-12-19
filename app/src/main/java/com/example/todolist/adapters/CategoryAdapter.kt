package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.entities.Category
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ItemCategoryBinding
import com.example.todolist.databinding.ItemFilterBinding
import com.example.todolist.utils.removeTime
import java.util.Calendar

class CategoryAdapter(
    var items: List<Category>,
    val onItemClick: (Int) -> Unit,
    val onItemLongClick: (Int) -> Boolean,
    val onFilterClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val FILTER_TODAY      = 0
        const val FILTER_SCHEDULED  = 1
        const val FILTER_ALL        = 2
        const val FILTER_PRIORITY   = 3
        const val FILTER_DONE       = 4
        const val FILTER_NEW        = 5
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            val holder = viewHolder as FiltersViewHolder
            holder.render()
            holder.binding.filterToday.setOnClickListener { onFilterClick(FILTER_TODAY) }
            holder.binding.filterScheduled.setOnClickListener { onFilterClick(FILTER_SCHEDULED) }
            holder.binding.filterAll.setOnClickListener { onFilterClick(FILTER_ALL) }
            holder.binding.filterPriority.setOnClickListener { onFilterClick(FILTER_PRIORITY) }
            holder.binding.filterCompleted.setOnClickListener { onFilterClick(FILTER_DONE) }
            holder.binding.filterNew.setOnClickListener { onFilterClick(FILTER_NEW) }
        } else {
            val holder = viewHolder as ViewHolder
            val category = items[position - 1]
            holder.render(category)
            holder.binding.itemView.setOnClickListener {
                onItemClick(holder.adapterPosition - 1)
            }
            holder.binding.itemView.setOnLongClickListener {
                onItemLongClick(holder.adapterPosition - 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FiltersViewHolder(binding)
        } else {
            val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        } else {
            return 1
        }
    }

    fun updateItems(items: List<Category>) {
        /*val diffUtils = CategoryDiffUtils(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        this.items = items
        diffResult.dispatchUpdatesTo(this)*/
        this.items = items
        notifyDataSetChanged()
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

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private lateinit var category: Category
        private val taskDAO = TaskDAO(context)

        fun render(category: Category) {
            this.category = category

            binding.nameTextView.text = category.name
            binding.iconImageView.setImageResource(category.icon)
            binding.colorCardView.setCardBackgroundColor(category.color)

            val numberOfTasksDone = taskDAO.countByCategoryAndDone(category, true).toFloat()
            val numberOfTasksTotal = taskDAO.countByCategory(category).toFloat()

            var completed = false
            if (numberOfTasksTotal == 0f) {
                binding.taskProgressIndicator.progress = 100
                completed = true
            } else {
                val progress = ((numberOfTasksDone / numberOfTasksTotal) * 100).toInt()
                binding.taskProgressIndicator.progress = progress
                binding.taskProgressPercentage.text = "$progress%"
                completed = progress == 100
            }

            if (completed) {
                binding.taskProgressPercentage.visibility = View.GONE
                binding.taskProgressCompleted.visibility = View.VISIBLE
            } else {
                binding.taskProgressPercentage.visibility = View.VISIBLE
                binding.taskProgressCompleted.visibility = View.GONE
            }
        }
    }
}