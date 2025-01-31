package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.entities.Category
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ItemCategoryBinding
import com.example.todolist.adapters.utils.CategoryDiffUtils

class CategoryAdapter(
    var items: List<Category>,
    val onItemClick: (Int) -> Unit,
    val onItemLongClick: (Int) -> Boolean
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = items[position]
        holder.render(category)
        holder.binding.itemView.setOnClickListener {
            onItemClick(holder.bindingAdapterPosition)
        }
        holder.binding.itemView.setOnLongClickListener {
            onItemLongClick(holder.bindingAdapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<Category>) {
        val diffUtils = CategoryDiffUtils(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        this.items = items
        diffResult.dispatchUpdatesTo(this)
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

            val numberOfTasksDone = category.numberOfTasksDone.toFloat()
            val numberOfTasksTotal = category.numberOfTasksTotal.toFloat()

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