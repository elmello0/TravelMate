package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleEditText = findViewById(R.id.etTaskTitle)
        descriptionEditText = findViewById(R.id.etDescription)
        saveButton = findViewById(R.id.btnSave)
        cancelButton = findViewById(R.id.btnCancel)

        // Guardar la tarea
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Crear un Intent para devolver la tarea
                val resultIntent = Intent()
                resultIntent.putExtra("task_title", title)
                resultIntent.putExtra("task_description", description)
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Cerrar AddTaskActivity y volver a la lista de tareas
            }
        }

        // Cancelar la operación
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() // Cerrar AddTaskActivity
        }
    }
}
