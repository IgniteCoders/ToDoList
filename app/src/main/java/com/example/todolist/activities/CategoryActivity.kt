package com.example.todolist.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.ColorAdapter
import com.example.todolist.adapters.IconAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.databinding.ActivityCategoryBinding
import com.example.todolist.utils.setWindowImeInsets

class CategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    private lateinit var binding: ActivityCategoryBinding

    private var isEditing: Boolean = false
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var category: Category

    private lateinit var colorAdapter: ColorAdapter
    private lateinit var iconAdapter: IconAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setWindowInsets(binding.root)
        setWindowImeInsets(binding.root)

        categoryDAO = CategoryDAO(this)

        // Si nos pasan un id es que queremos editar una tarea existente
        val id = intent.getLongExtra(EXTRA_CATEGORY_ID, -1L)
        if (id != -1L) {
            isEditing = true
            category = categoryDAO.findById(id)!!
        } else {
            isEditing = false
            category = Category(-1, "", Category.colors[0], 0, categoryDAO.countAll())
        }

        initViews()

        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_save -> {
                saveCategory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = if (isEditing) {
            getString(R.string.activity_category_title_edit)
        } else {
            getString(R.string.activity_category_title_create)
        }

        colorAdapter = ColorAdapter(Category.colors, Category.colors.indexOf(category.color)) {
            category.color = Category.colors[it]
            loadColorAndIcon()
        }
        binding.colorsRecyclerView.adapter = colorAdapter
        binding.colorsRecyclerView.layoutManager = GridLayoutManager(this, 6)

        iconAdapter = IconAdapter(Category.icons, category.iconPosition) {
            category.iconPosition = it
            loadColorAndIcon()
        }
        binding.iconsRecyclerView.adapter = iconAdapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(this, 6)

        binding.saveButton.setOnClickListener {
            saveCategory()
        }
    }

    private fun loadData() {
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
        if (category.name.length > 20) {
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

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_CATEGORY_ID, category.id)
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }
    }
}