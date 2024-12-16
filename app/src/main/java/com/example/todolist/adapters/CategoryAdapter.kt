package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.entities.Category
import com.example.todolist.databinding.ItemCategoryBinding

class CategoryAdapter(
    var items: List<Category>,
    val onItemClick: (Int) -> Unit,
    val onItemLongClick: (Int) -> Boolean
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = items[position]
        holder.render(category)
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(holder.adapterPosition)
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
        /*val diffUtils = CategoryDiffUtils(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        this.items = items
        diffResult.dispatchUpdatesTo(this)*/
        this.items = items
        notifyDataSetChanged()
    }



    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private lateinit var category: Category

        fun render(category: Category) {
            this.category = category

            binding.nameTextView.text = category.name
            binding.iconImageView.setImageResource(category.icon)
            binding.colorCardView.setCardBackgroundColor(category.color)
        }
    }
}