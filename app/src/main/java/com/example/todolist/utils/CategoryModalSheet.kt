package com.example.todolist.utils

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todolist.data.entities.Category
import com.example.todolist.databinding.DialogCategorySheetBinding
import com.example.todolist.databinding.ItemCategoryChipBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryModalSheet(val categoryList: List<Category>, val onItemClick: (Int) -> Unit) : BottomSheetDialogFragment() {

    lateinit var binding: DialogCategorySheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCategorySheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for ((position, category) in categoryList.withIndex()) {
            val chipBinding = ItemCategoryChipBinding.inflate(layoutInflater, binding.categoryChipGroup, false)
            chipBinding.categoryChip.text = category.name
            chipBinding.categoryChip.setChipIconResource(category.icon)
            chipBinding.categoryChip.chipBackgroundColor = ColorStateList.valueOf(category.color)
            chipBinding.categoryChip.setOnClickListener {
                onItemClick(position)
                this.dismiss()
            }
            binding.categoryChipGroup.addView(chipBinding.root)
        }
    }

    companion object {
        const val TAG = "CategoryModalSheet"
    }
}