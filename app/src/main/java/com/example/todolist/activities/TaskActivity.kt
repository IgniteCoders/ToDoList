package com.example.todolist.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.data.entities.Category
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.CategoryDAO
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTaskBinding
import com.example.todolist.databinding.ItemCategoryChipBinding
import com.example.todolist.utils.CategoryModalSheet
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.getFormattedTime
import com.example.todolist.utils.removeTime
import com.example.todolist.utils.setWindowImeInsets
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar


class TaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    private lateinit var binding: ActivityTaskBinding

    private var isEditing: Boolean = false
    private lateinit var taskDAO: TaskDAO
    private lateinit var task: Task
    private lateinit var calendar: Calendar

    private lateinit var categoryDAO: CategoryDAO
    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var category: Category

    //lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setWindowInsets(binding.root)
        setWindowImeInsets(binding.root)

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        categoryList = categoryDAO.findAll().toMutableList()

        // Si nos pasan un id es que queremos editar una tarea existente
        val id = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, -1)
        if (categoryId != -1L) {
            category = categoryDAO.findById(categoryId)!!
        } else {
            category = categoryList.first()
        }
        if (id != -1L) {
            isEditing = true
            task = taskDAO.findById(id)!!
            category = task.category
            calendar = task.getCalendar()
        } else {
            isEditing = false
            category = category
            task = Task(-1, "", category = category)
            calendar = Calendar.getInstance()
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
                saveTask()
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

        binding.categoryChip.setOnClickListener {
            val modalBottomSheet = CategoryModalSheet(categoryList) { position ->
                category = categoryList[position]
                setCategory()
            }
            modalBottomSheet.show(supportFragmentManager, CategoryModalSheet.TAG)
        }
    }

    private fun loadData() {
        binding.titleTextField.editText?.setText(task.title)
        binding.descriptionTextField.editText?.setText(task.description)

        binding.reminderSwitch.isChecked = task.reminder
        binding.allDaySwitch.isChecked = task.allDay

        setDate(calendar)
        setTime(calendar)

        setPriority()

        setCategory()
    }

    private fun setCategory() {
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
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(calendar.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            calendar.timeInMillis = selection
            // Remove an extra hour added when user choose the day
            calendar.removeTime()
            // Setting the time that previously the user selected
            calendar.timeInMillis += task.time
            setDate(calendar)
        }

        datePicker.show(supportFragmentManager, datePicker.toString())
    }

    private fun timePickerDialog() {
        val isSystem24Hour = DateFormat.is24HourFormat(this)

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            setTime(calendar)
        }

        timePicker.show(supportFragmentManager, timePicker.toString())
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
        task.category = category

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