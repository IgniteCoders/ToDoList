package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.ItemIconBinding


class IconAdapter(
    private val icons: Array<Int>,
    var selected: Int,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = icons[position]
        //holder.itemView.setBackgroundColor(color)

        holder.render(icon)
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
        return icons.size
    }

    class ViewHolder(val binding: ItemIconBinding) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun render(icon: Int) {
            binding.iconImageView.setImageResource(icon)
        }

        fun setSelected(selected: Boolean) {
            if (selected) {
                binding.selectedIconCardView.setCardBackgroundColor(context.getColor(R.color.md_theme_primary))
            } else {
                binding.selectedIconCardView.setCardBackgroundColor(context.getColor(R.color.md_theme_outlineVariant))
            }
        }
    }
}