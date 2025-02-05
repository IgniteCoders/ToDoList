package com.example.todolist.activities

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.adapters.FiltersAdapter
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.DateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: ConcatAdapter
    private lateinit var filtersAdapter: FiltersAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var searchAdapter: TaskAdapter

    private lateinit var categoryDAO: CategoryDAO
    private var categoryList: MutableList<Category> = mutableListOf()

    private lateinit var taskDAO: TaskDAO
    private var taskList: MutableList<Task> = mutableListOf()

    private var intentLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val categoryId = data.getLongExtra(CategoryActivity.EXTRA_CATEGORY_ID, -1)
                val intent = Intent(this, TasksActivity::class.java)
                intent.putExtra(TasksActivity.EXTRA_CATEGORY_ID, categoryId)
                startActivity(intent)
            }
        }
    }

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

        loadData()

        // Refresh search result when back to activity in case we changed something
        if (binding.searchView.isShowing) {
            loadSearchData()
        }

        binding.addTaskButton.isEnabled = categoryList.isNotEmpty()
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = Calendar.getInstance().getFormattedDate(DateFormat.LONG)

        filtersAdapter = FiltersAdapter() {
            onFilterClick(it)
        }

        categoryAdapter = CategoryAdapter(categoryList, {
            showCategory(it)
        }, {
            //editCategory(it)
            false
        })

        adapter = ConcatAdapter(filtersAdapter, categoryAdapter)

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

        binding.searchBar.inflateMenu(R.menu.menu_activity_main)
        binding.searchBar.setOnMenuItemClickListener { menuItem -> onMenuItemClick(menuItem) }

        // Crear tarea
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        configureGestures()
    }

    private fun loadData() {
        categoryList = categoryDAO.findAll().toMutableList()
        categoryAdapter.updateItems(categoryList)
    }

    private fun loadSearchData() {
        taskList = taskDAO.findAllByTitle(binding.searchView.text.toString()).toMutableList()
        searchAdapter.updateItems(taskList)
    }

    private fun configureGestures() {
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if (viewHolder.absoluteAdapterPosition == 0) return false
                    if (target.absoluteAdapterPosition == 0) return false

                    swapCategoryPositions(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

                    categoryAdapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                    //loadData()
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (viewHolder.absoluteAdapterPosition == 0) return

                    if (direction == ItemTouchHelper.LEFT) {
                        deleteCategory(viewHolder.bindingAdapterPosition)
                    } else {
                        editCategory(viewHolder.bindingAdapterPosition)
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    if (viewHolder.absoluteAdapterPosition == 0) return

                    val whiteColor = getColor(R.color.white)

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        // Swipe left action
                        .addSwipeLeftLabel(getString(R.string.action_delete).uppercase(Locale.ROOT))
                        .setSwipeLeftLabelColor(whiteColor)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(whiteColor)
                        .addSwipeLeftBackgroundColor(getColor(R.color.delete))

                        // Swipe right action
                        .addSwipeRightLabel(getString(R.string.action_edit).uppercase(Locale.ROOT))
                        .setSwipeRightLabelColor(whiteColor)
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .setSwipeRightActionIconTint(whiteColor)
                        .addSwipeRightBackgroundColor(getColor(R.color.md_theme_secondary))

                        // Build
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })
        gestures.attachToRecyclerView(binding.recyclerView)
    }

    private fun showCategory(position: Int) {
        val category = categoryList[position]
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)
    }

    private fun editCategory(position: Int) {
        categoryAdapter.notifyItemChanged(position)
        val category = categoryList[position]
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)
    }

    private fun swapCategoryPositions(position1: Int, position2: Int) {
        println("Swap from $position1 to $position2")
        val category1 = categoryList[position1]
        val category2 = categoryList[position2]
        category1.position = position2
        category2.position = position1
        categoryDAO.update(category1)
        categoryDAO.update(category2)
        Collections.swap(categoryList, position1, position2)
    }

    private fun deleteCategory(position: Int) {
        val category = categoryList[position]
        // Mostramos un dialogo para asegurarnos de que el usuario quiere borrar la categoria
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.alert_dialog_delete_category_title)
            .setMessage(R.string.alert_dialog_delete_category_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                // Borramos la tarea en caso de pulsar el boton OK
                categoryDAO.delete(category)
                // Actualizamos las posiciones de las categorias que hay por debajo
                for (i in position..<categoryList.count()) {
                    categoryList[i].position = i - 1
                    categoryDAO.update(categoryList[i])
                    println("Actualizo posicion de $i a ${categoryList[i].position}")
                }
                loadData()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                categoryAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setOnCancelListener {
                categoryAdapter.notifyItemChanged(position)
            }
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun onFilterClick(filter: Int) {
        if (filter == FiltersAdapter.FILTER_NEW) {
            val intent = Intent(this, CategoryActivity::class.java)
            //startActivity(intent)
            intentLauncher.launch(intent)
        } else {
            val intent = Intent(this, TasksActivity::class.java)
            intent.putExtra(TasksActivity.EXTRA_FILTER, filter)
            startActivity(intent)
        }
    }

    // Funcion para cuando marcamos una tarea (finalizada/pendiente)
    private fun checkTask(position: Int) {
        val task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)
        searchAdapter.notifyItemChanged(position)
        loadSearchData()
    }

    // Funciona para mostrar un dialogo para borrar la tarea
    private fun deleteTask(position: Int) {
        val task = taskList[position]
        // Mostramos un dialogo para asegurarnos de que el usuario quiere borrar la tarea
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.alert_dialog_delete_task_title)
            .setMessage(R.string.alert_dialog_delete_task_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                // Borramos la tarea en caso de pulsar el boton OK
                taskDAO.delete(task)
                loadSearchData()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                searchAdapter.notifyItemChanged(position)
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