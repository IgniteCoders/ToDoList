package com.example.todolist.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.R
import com.example.todolist.activities.TasksActivity.Companion
import com.example.todolist.adapters.CategoryAdapter
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTaskBinding
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.getFormattedTime
import com.example.todolist.utils.setWindowInsets
import java.text.DateFormat
import java.util.Calendar


class TaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    lateinit var binding: ActivityTaskBinding

    var isEditing: Boolean = false
    lateinit var taskDAO: TaskDAO
    lateinit var task: Task
    lateinit var calendar: Calendar

    lateinit var categoryDAO: CategoryDAO
    //var categoryList: MutableList<Category> = mutableListOf()
    lateinit var category: Category

    //lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setWindowInsets(binding.root)

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        //categoryList = categoryDAO.findAll().toMutableList()

        // Si nos pasan un id es que queremos editar una tarea existente
        val id = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, -1)
        if (categoryId != -1L) {
            category = categoryDAO.findById(categoryId)!!
        } else {
            category = categoryDAO.findAll().first()
        }
        if (id != -1L) {
            isEditing = true
            task = taskDAO.findById(id)!!
            category = task.category
        } else {
            isEditing = false
            category = category
            task = Task(-1, "", category = category)
        }


        initViews()

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

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = if (isEditing) {
            getString(R.string.activity_task_title_edit)
        } else {
            getString(R.string.activity_task_title_create)
        }

        //binding.titleTextField.requestFocus()

        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.allDaySwitch.isEnabled = isChecked
            binding.dateTextField.isEnabled = isChecked
            binding.timeTextField.isEnabled = isChecked && !binding.allDaySwitch.isChecked
        }

        binding.allDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.timeTextField.isEnabled = !isChecked
        }

        binding.dateTextField.editText?.setOnClickListener {
            datePickerDialog()
        }

        binding.timeTextField.editText?.setOnClickListener {
            timePickerDialog()
        }

        binding.dateTextField.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                datePickerDialog()
            }
        }

        binding.timeTextField.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                timePickerDialog()
            }
        }

        binding.priorityAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            task.priority = position
        }

        binding.saveButton.setOnClickListener {
            saveTask()
        }

        /*val categoryNew = Category(-1, getString(R.string.new_category), R.color.md_theme_secondary, R.drawable.ic_add)
        categoryList.add(categoryNew)
        categoryAdapter = CategoryAdapter(categoryList, { position ->
            if (position == categoryList.lastIndex) {
                //addCategory()
            } else {
                //selectCategory()
            }
        }, { position ->
            if (position == categoryList.lastIndex) {
                false
            } else {
                //editCategory()
                true
            }
        })
        binding.categoryRecyclerView.adapter = categoryAdapter*/
    }

    private fun loadData() {
        binding.titleTextField.editText?.setText(task.title)
        binding.descriptionTextField.editText?.setText(task.description)
        binding.reminderSwitch.isChecked = task.reminder
        binding.allDaySwitch.isChecked = task.allDay
        setPriority()

        if (task.reminder) {
            calendar = task.getCalendar()!!
        } else {
            calendar = Calendar.getInstance()
        }
        setDate(calendar)
        setTime(calendar)

        binding.categoryChip.text = category.name
        binding.categoryChip.setChipIconResource(category.icon)
        binding.categoryChip.chipBackgroundColor = ColorStateList.valueOf(category.color)
    }

    private fun setPriority() {
        val item = binding.priorityAutoCompleteTextView.adapter.getItem(task.priority) as (String)
        binding.priorityAutoCompleteTextView.setText(item, false)
    }

    private fun setDate(calendar: Calendar) {
        binding.dateTextField.editText?.setText(calendar.getFormattedDate())
    }

    private fun setTime(calendar: Calendar) {
        binding.timeTextField.editText?.setText(calendar.getFormattedTime())
    }

    private fun datePickerDialog() {
        val dateListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDate(calendar)
        }
        DatePickerDialog(
            this,
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun timePickerDialog() {
        val dateListener = OnTimeSetListener { view, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            setTime(calendar)
        }
        TimePickerDialog(
            this,
            dateListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun validateTask(): Boolean {
        // Comprobamos el texto introducido para mostrar posibles errores
        if (task.title.trim().isEmpty()) {
            binding.titleTextField.error = getString(R.string.field_error_task_title_empty)
            return false
        } else {
            binding.titleTextField.error = null
        }
        if (task.title.length > 50) {
            binding.titleTextField.error = getString(R.string.field_error_task_title_too_long)
            return false
        } else {
            binding.titleTextField.error = null
        }

        if (task.reminder) {
            if (binding.dateTextField.editText?.text.isNullOrEmpty()) {
                binding.dateTextField.error = getString(R.string.field_error_task_date_empty)
                return false
            } else {
                binding.dateTextField.error = null
            }
            if (!task.allDay && binding.timeTextField.editText?.text.isNullOrEmpty()) {
                binding.timeTextField.error = getString(R.string.field_error_task_time_empty)
                return false
            } else {
                binding.timeTextField.error = null
            }
        }
        return true
    }

    private fun saveTask() {
        task.title = binding.titleTextField.editText?.text.toString()
        task.description = binding.descriptionTextField.editText?.text.toString()
        task.reminder = binding.reminderSwitch.isChecked
        task.allDay = task.reminder && binding.allDaySwitch.isChecked
        //task.priority = binding.priorityAutoCompleteTextView.listSelection
        task.setCalendar(calendar)

        if (validateTask()) {
            // Si la tarea existe la actualizamos si no la insertamos
            if (task.id != -1L) {
                taskDAO.update(task)
            } else {
                taskDAO.insert(task)
            }

            finish()
        }
    }
}