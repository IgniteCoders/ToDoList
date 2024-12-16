package com.example.todolist.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.entities.Category
import com.example.todolist.databinding.ItemColorBinding


class ColorAdapter(
    private val colors: Array<Int>,
    var selected: Int,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colors[position]
        //holder.itemView.setBackgroundColor(color)

        holder.render(color)
        holder.setSelected(position == selected)

        holder.itemView.setOnClickListener {
            val oldSelected = selected
            selected = position
            notifyItemChanged(oldSelected)
            notifyItemChanged(selected)
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    class ViewHolder(val binding: ItemColorBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun render(color: Int) {
            binding.colorCardView.setCardBackgroundColor(color)
        }

        fun setSelected(selected: Boolean) {
            if (selected) {
                binding.selectedColorCardView.visibility = View.VISIBLE
            } else {
                binding.selectedColorCardView.visibility = View.GONE
            }
        }
    }
}