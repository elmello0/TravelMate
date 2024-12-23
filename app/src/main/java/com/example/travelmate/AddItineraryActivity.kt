package com.example.travelmate

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddItineraryActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_itinerary)

        titleEditText = findViewById(R.id.etActivityTitle)
        descriptionEditText = findViewById(R.id.etDescription)
        dateButton = findViewById(R.id.btnDate)
        timeButton = findViewById(R.id.btnTime)
        saveButton = findViewById(R.id.btnSave)
        cancelButton = findViewById(R.id.btnCancel)

        // Selector de fecha
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateButton.text = selectedDate
            }, year, month, day)

            datePickerDialog.show()
        }

        // Selector de hora
        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                selectedTime = "$selectedHour:$selectedMinute"
                timeButton.text = selectedTime
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // Guardar el itinerario
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (title.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                // Crear el nuevo itinerario con isCompleted por defecto en false
                val newItinerary = Itinerary(
                    title = title,
                    date = selectedDate,
                    time = selectedTime,
                    description = description,
                    group = "", // Si ya no necesitas un grupo específico, deja este campo vacío
                    isCompleted = false // Por defecto, los itinerarios no están completados
                )

                // Devolver el resultado a ItineraryActivity
                val resultIntent = Intent()
                resultIntent.putExtra("new_itinerary", newItinerary)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                // Mostrar un mensaje si algún campo está vacío
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar la operación
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
