package com.example.todolist.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.search.SearchBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.utils.getFormattedDate
import java.text.DateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: CategoryAdapter

    lateinit var categoryDAO: CategoryDAO
    var categoryList: MutableList<Category> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        categoryDAO = CategoryDAO(this)
        adapter = CategoryAdapter(categoryList, {
            showCategory(it)
        }, {
            editCategory(it)
            true
        }, {
            onFilterClick(it)
        })

        binding.recyclerView.adapter = adapter
        //binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        initViews()
    }

    override fun onResume() {
        super.onResume()

        categoryList = categoryDAO.findAll().toMutableList()
        adapter.updateItems(categoryList)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        initSearchView(menu?.findItem(R.id.menu_search))
        return false
    }

    private fun initSearchView(searchItem: MenuItem?) {
        if (searchItem != null) {
            var searchView = searchItem.actionView as SearchBar

            searchView.textView.addTextChangedListener {
                println(searchView.textView.text.toString())
            }
        }
    }*/

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = Calendar.getInstance().getFormattedDate(DateFormat.LONG)
    }

    private fun showCategory(position: Int) {
        val category = categoryList[position]
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)
    }

    private fun editCategory(position: Int) {
        val category = categoryList[position]
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)
    }

    private fun onFilterClick(filter: Int) {
        if (filter == CategoryAdapter.FILTER_NEW) {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, TasksActivity::class.java)
            intent.putExtra(TasksActivity.EXTRA_FILTER, filter)
            startActivity(intent)
        }
    }
}