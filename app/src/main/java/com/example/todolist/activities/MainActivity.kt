package com.example.todolist.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.setWindowInsets
import com.example.todolist.utils.toPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: CategoryAdapter
    lateinit var searchAdapter: TaskAdapter

    lateinit var categoryDAO: CategoryDAO
    var categoryList: MutableList<Category> = mutableListOf()

    lateinit var taskDAO: TaskDAO
    var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setWindowInsets(binding.root)
        binding.addTaskButton.setWindowInsets(Insets.of(0, 0, 16.toPx(), 16.toPx()))

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        initViews()
    }

    override fun onResume() {
        super.onResume()

        categoryList = categoryDAO.findAll().toMutableList()
        adapter.updateItems(categoryList)

        // Refresh search result when back to activity in case we changed something
        if (binding.searchView.isShowing) {
            loadSearchData()
        }

        binding.addTaskButton.isEnabled = categoryList.isNotEmpty()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = Calendar.getInstance().getFormattedDate(DateFormat.LONG)

        adapter = CategoryAdapter(categoryList, {
            showCategory(it)
        }, {
            editCategory(it)
            true
        }, {
            onFilterClick(it)
        })

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchAdapter = TaskAdapter(taskList,
            { showTask(it) },
            { checkTask(it) },
            { deleteTask(it) }
        )
        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.searchView.editText.addTextChangedListener {
            loadSearchData()
        }

        // Crear tarea
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }
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

    private fun loadSearchData() {
        taskList = taskDAO.findAllByTitle(binding.searchView.text.toString()).toMutableList()
        searchAdapter.updateItems(taskList)
    }

    // Funcion para cuando marcamos una tarea (finalizada/pendiente)
    private fun checkTask(position: Int) {
        val task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)
        adapter.notifyItemChanged(position)
        loadSearchData()
    }

    // Funciona para mostrar un dialogo para borrar la tarea
    private fun deleteTask(position: Int) {
        val task = taskList[position]
        // Mostramos un dialogo para asegurarnos de que el usuario quiere borrar la tarea
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.alert_dialog_delete_title)
            .setMessage(R.string.alert_dialog_delete_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                // Borramos la tarea en caso de pulsar el boton OK
                taskDAO.delete(task)
                loadSearchData()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    // Mostramos la tarea para editarla
    private fun showTask(position: Int) {
        val task = taskList[position]
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.id)
        startActivity(intent)
    }
}