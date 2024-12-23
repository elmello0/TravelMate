package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
    private lateinit var emptyStateTextView: TextView
    private val db = FirebaseFirestore.getInstance()

    private val ADD_TASK_REQUEST_CODE = 1
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        // Recuperar el ID del grupo pasado desde la actividad anterior
        groupId = intent.getStringExtra("group_id")

        if (groupId == null) {
            Toast.makeText(this, "Error: No se encontró el ID del grupo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        emptyStateTextView = findViewById(R.id.tv_empty_state)

        taskList = arrayListOf()
        taskAdapter = TaskAdapter(
            taskList = taskList,
            onTaskChecked = { task ->
                updateTaskInDatabase(task)
            },
            onTaskDeleted = { task ->
                deleteTask(task)
            }
        )
        recyclerView.adapter = taskAdapter

        loadTaskData()

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }
    }

    private fun loadTaskData() {
        if (groupId == null) return

        db.collection("groups").document(groupId!!)
            .collection("tasks")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                taskList.clear()
                snapshots?.forEach { document ->
                    val task = document.toObject(Task::class.java).apply {
                        documentId = document.id
                    }
                    taskList.add(task)
                }
                updateEmptyState()
                taskAdapter.notifyDataSetChanged()
            }
    }

    private fun updateTaskInDatabase(task: Task) {
        if (groupId == null || task.documentId == null) return

        db.collection("groups").document(groupId!!)
            .collection("tasks")
            .document(task.documentId!!)
            .set(task)
            .addOnSuccessListener {
                Toast.makeText(this, "${task.title} actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar ${task.title}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTask(task: Task) {
        if (groupId == null || task.documentId == null) return

        db.collection("groups").document(groupId!!)
            .collection("tasks")
            .document(task.documentId!!)
            .delete()
            .addOnSuccessListener {
                taskList.remove(task)
                updateEmptyState()
                taskAdapter.notifyDataSetChanged()
                Toast.makeText(this, "${task.title} eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar ${task.title}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val taskTitle = data?.getStringExtra("task_title")
            val taskDescription = data?.getStringExtra("task_description")

            if (taskTitle != null && taskDescription != null && groupId != null) {
                val newTask = Task(taskTitle, taskDescription, isCompleted = false)
                db.collection("groups").document(groupId!!)
                    .collection("tasks")
                    .add(newTask)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Tarea añadida correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al añadir tarea", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
