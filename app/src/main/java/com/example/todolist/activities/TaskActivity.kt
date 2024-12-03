package com.example.todolist.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTaskBinding
import com.example.todolist.utils.getFormattedDate
import com.example.todolist.utils.getFormattedTime
import java.util.Calendar


class TaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    lateinit var binding: ActivityTaskBinding

    var isEditing: Boolean = false
    lateinit var taskDAO: TaskDAO
    lateinit var task: Task
    lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDAO = TaskDAO(this)

        // Si nos pasan un id es que queremos editar una tarea existente
        val id = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        task = if (id != -1L) {
            isEditing = true
            taskDAO.findById(id)!!
        } else {
            isEditing = false
            Task(-1, "")
        }

        initViews()

        loadData()
    }

    private fun initViews() {
        //binding.titleTextField.requestFocus()

        binding.closeButton.setOnClickListener { finish() }

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
    }

    private fun loadData() {
        binding.titleTextView.text = if (isEditing) {
            "Editar tarea"
        } else {
            "Nueva tarea"
        }

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
            binding.titleTextField.error = "Escribe algo"
            return false
        } else {
            binding.titleTextField.error = null
        }
        if (task.title.length > 50) {
            binding.titleTextField.error = "Te pasaste"
            return false
        } else {
            binding.titleTextField.error = null
        }

        if (task.reminder) {
            if (binding.dateTextField.editText?.text.isNullOrEmpty()) {
                binding.dateTextField.error = "Selecciona una fecha"
                return false
            } else {
                binding.dateTextField.error = null
            }
            if (!task.allDay && binding.timeTextField.editText?.text.isNullOrEmpty()) {
                binding.timeTextField.error = "Selecciona una hora"
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