package com.example.todolist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    lateinit var binding: ActivityTaskBinding

    lateinit var taskDAO: TaskDAO
    lateinit var task: Task

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
        if (id != -1L) {
            task = taskDAO.findById(id)!!
            binding.nameTextField.editText?.setText(task.name)
        } else {
            task = Task(-1, "")
        }

        binding.saveButton.setOnClickListener {
            // Comprobamos el texto introducido para mostrar posibles errores
            val taskName = binding.nameTextField.editText?.text.toString()
            if (taskName.isEmpty()) {
                binding.nameTextField.error = "Escribe algo"
                return@setOnClickListener
            }
            if (taskName.length > 50) {
                binding.nameTextField.error = "Te pasaste"
                return@setOnClickListener
            }

            task.name = taskName

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