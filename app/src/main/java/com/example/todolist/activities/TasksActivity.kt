package com.example.todolist.activities

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTasksBinding
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.removeTime
import com.example.todolist.utils.setWindowInsets
import com.example.todolist.utils.toPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.DateFormat
import java.util.Calendar


class TasksActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
        const val EXTRA_FILTER = "FILTER"
    }

    lateinit var binding: ActivityTasksBinding

    lateinit var taskDAO: TaskDAO
    var taskList: MutableList<Task> = mutableListOf()

    lateinit var adapter: TaskAdapter

    lateinit var categoryDAO: CategoryDAO
    var category: Category? = null
    var filter: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTasksBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setWindowInsets(binding.root)
        binding.addTaskButton.setWindowInsets(Insets.of(0, 0, 16.toPx(), 16.toPx()))

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        val categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, -1)
        if (categoryId != -1L) {
            category = categoryDAO.findById(categoryId)
        }

        filter = intent.getIntExtra(EXTRA_FILTER, -1)

        initViews()

        //loadData()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.subtitle = Calendar.getInstance().getFormattedDate(DateFormat.LONG)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = TaskAdapter(taskList,
            { showTask(it) },
            { checkTask(it) },
            { deleteTask(it) }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Crear tarea
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            intent.putExtra(TaskActivity.EXTRA_CATEGORY_ID, category?.id)
            startActivity(intent)
        }

        //binding.dateTextView.text = Calendar.getInstance().getFormattedDate(DateFormat.LONG)

        configureGestures()
    }

    private fun loadData() {
        if (filter != -1) {
            var title = getString(R.string.activity_tasks_title)
            var color = getColor(R.color.md_theme_primary)
            var icon = R.drawable.ic_inbox
            when (filter) {
                CategoryAdapter.FILTER_TODAY        -> {
                    taskList = taskDAO.findAllByDate(Calendar.getInstance().removeTime()).toMutableList()
                    title = getString(R.string.filter_today)
                    icon = R.drawable.ic_calendar
                }
                CategoryAdapter.FILTER_SCHEDULED    -> {
                    taskList = taskDAO.findAllByReminder().toMutableList()
                    title = getString(R.string.filter_scheduled)
                    icon = R.drawable.ic_clock
                }
                CategoryAdapter.FILTER_ALL          -> {
                    taskList = taskDAO.findAll().toMutableList()
                    title = getString(R.string.filter_all)
                    icon = R.drawable.ic_inbox
                }
                CategoryAdapter.FILTER_PRIORITY     -> {
                    taskList = taskDAO.findAllByPriority().toMutableList()
                    title = getString(R.string.filter_priority)
                    icon = R.drawable.ic_priority
                }
                CategoryAdapter.FILTER_DONE         -> {
                    taskList = taskDAO.findAllByDone().toMutableList()
                    title = getString(R.string.filter_completed)
                    icon = R.drawable.ic_completed
                }
            }
            supportActionBar?.title = title
            binding.colorCardView.setCardBackgroundColor(color)
            binding.iconImageView.setImageResource(icon)
        } else {
            if (category != null) {
                taskList = taskDAO.findAllByCategory(category!!).toMutableList()
                supportActionBar?.title = category!!.name
                binding.colorCardView.setCardBackgroundColor(category!!.color)
                binding.iconImageView.setImageResource(category!!.icon)
            } else {
                taskList = taskDAO.findAll().toMutableList()
            }
        }
        adapter.updateItems(taskList)
    }

    override fun onResume() {
        super.onResume()

        // Cargamos la lista por si se hubiera añadido una tarea nueva
        loadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureGestures() {
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        deleteTask(viewHolder.adapterPosition)
                    } else {
                        checkTask(viewHolder.adapterPosition)
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    val isChecked = (viewHolder as TaskAdapter.ViewHolder).isChecked()
                    val rightIconRes = if (isChecked) R.drawable.ic_box_unchecked else R.drawable.ic_box_checked
                    val rightTextRes = if (isChecked) R.string.action_uncheck else R.string.action_check
                    val whiteColor = getColor(R.color.white)

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        // Swipe left action
                        .addSwipeLeftLabel(getString(R.string.action_delete))
                        .setSwipeLeftLabelColor(whiteColor)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(whiteColor)
                        .addSwipeLeftBackgroundColor(getColor(R.color.delete))

                        // Swipe right action
                        .addSwipeRightLabel(getString(rightTextRes))
                        .setSwipeRightLabelColor(whiteColor)
                        .addSwipeRightActionIcon(rightIconRes)
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

    // Funcion para cuando marcamos una tarea (finalizada/pendiente)
    private fun checkTask(position: Int) {
        val task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)
        adapter.notifyItemChanged(position)
        loadData()
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
                loadData()
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