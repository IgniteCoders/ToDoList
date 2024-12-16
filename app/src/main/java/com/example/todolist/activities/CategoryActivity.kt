package com.example.todolist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.ColorAdapter
import com.example.todolist.adapters.IconAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    lateinit var binding: ActivityCategoryBinding

    var isEditing: Boolean = false
    lateinit var categoryDAO: CategoryDAO
    lateinit var category: Category

    lateinit var colorAdapter: ColorAdapter
    lateinit var iconAdapter: IconAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)

        // Si nos pasan un id es que queremos editar una tarea existente
        val id = intent.getLongExtra(EXTRA_CATEGORY_ID, -1L)
        if (id != -1L) {
            isEditing = true
            category = categoryDAO.findById(id)!!
        } else {
            isEditing = false
            category = Category(-1, "", Category.colors[0], Category.icons[0])
        }


        initViews()

        loadData()

    }

    private fun initViews() {
        binding.closeButton.setOnClickListener { finish() }

        colorAdapter = ColorAdapter(Category.colors, Category.colors.indexOf(category.color)) {
            category.color = Category.colors[it]
            loadColorAndIcon()
        }
        binding.colorsRecyclerView.adapter = colorAdapter
        binding.colorsRecyclerView.layoutManager = GridLayoutManager(this, 6)

        iconAdapter = IconAdapter(Category.icons, Category.icons.indexOf(category.icon)) {
            category.icon = Category.icons[it]
            loadColorAndIcon()
        }
        binding.iconsRecyclerView.adapter = iconAdapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(this, 6)

        binding.saveButton.setOnClickListener {
            saveCategory()
        }
    }

    private fun loadData() {
        binding.titleTextView.text = if (isEditing) {
            getString(R.string.activity_category_title_edit)
        } else {
            getString(R.string.activity_category_title_create)
        }

        binding.titleTextField.editText?.setText(category.name)

        loadColorAndIcon()
    }

    private fun loadColorAndIcon() {
        binding.colorCardView.setCardBackgroundColor(category.color)
        binding.iconImageView.setImageResource(category.icon)
    }

    private fun validateCategory(): Boolean {
        // Comprobamos el texto introducido para mostrar posibles errores
        if (category.name.trim().isEmpty()) {
            binding.titleTextField.error = getString(R.string.field_error_category_title_empty)
            return false
        } else {
            binding.titleTextField.error = null
        }
        if (category.name.length > 50) {
            binding.titleTextField.error = getString(R.string.field_error_category_title_too_long)
            return false
        } else {
            binding.titleTextField.error = null
        }
        return true
    }

    private fun saveCategory() {
        category.name = binding.titleTextField.editText?.text.toString()

        if (validateCategory()) {
            // Si la tarea existe la actualizamos si no la insertamos
            if (category.id != -1L) {
                categoryDAO.update(category)
            } else {
                categoryDAO.insert(category)
            }

            finish()
        }
    }
}