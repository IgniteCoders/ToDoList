package com.example.todolist.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.data.entities.Task
import com.example.todolist.data.providers.TaskDAO
import com.example.todolist.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.saveButton.setOnClickListener {
            val taskName = binding.nameTextField.editText?.text.toString()
            if (taskName.isEmpty()) {
                binding.nameTextField.error = "Escribe algo"
                return@setOnClickListener
            }
            if (taskName.length > 50) {
                binding.nameTextField.error = "Te pasaste"
                return@setOnClickListener
            }
            val task = Task(-1, taskName)

            val taskDAO = TaskDAO(this)
            taskDAO.insert(task)

            finish()
        }
    }
}