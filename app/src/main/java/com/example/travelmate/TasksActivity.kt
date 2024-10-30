package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class TasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: ArrayList<Task>
    private val db = FirebaseFirestore.getInstance()

    private val ADD_TASK_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        loadTaskData()

        taskAdapter = TaskAdapter(taskList) { task ->
            // Actualiza la base de datos cada vez que el estado de la tarea cambie
            updateTaskInDatabase(task)
        }
        recyclerView.adapter = taskAdapter

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }
    }

    private fun loadTaskData() {
        taskList = arrayListOf()
        db.collection("tasks").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val task = document.toObject(Task::class.java)
                    taskList.add(task)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTaskInDatabase(task: Task) {
        db.collection("tasks").document(task.title) // Usa un identificador único para cada tarea
            .set(task)
            .addOnSuccessListener {
                Toast.makeText(this, "${task.title} actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar ${task.title}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val taskTitle = data?.getStringExtra("task_title")
            val taskDescription = data?.getStringExtra("task_description")

            if (taskTitle != null && taskDescription != null) {
                val newTask = Task(taskTitle, taskDescription, isCompleted = false)
                taskList.add(newTask)
                taskAdapter.notifyDataSetChanged()
                // Guarda la nueva tarea en la base de datos
                db.collection("tasks").document(taskTitle).set(newTask)
            }
        }
    }
}
