package com.example.travelmate


import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddItineraryActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var groupSpinner: Spinner

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedGroup: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_itinerary)

        titleEditText = findViewById(R.id.etActivityTitle)
        descriptionEditText = findViewById(R.id.etDescription)
        dateButton = findViewById(R.id.btnDate)
        timeButton = findViewById(R.id.btnTime)
        saveButton = findViewById(R.id.btnSave)
        cancelButton = findViewById(R.id.btnCancel)
        groupSpinner = findViewById(R.id.spinnerGroup)

        // Lista de grupos
        val groupList = arrayListOf("Grupo 1", "Grupo 2", "Grupo 3")

        // Configurar el Spinner con la lista de grupos
        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupList)
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSpinner.adapter = groupAdapter

        // Selector de fecha
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Guardar la fecha seleccionada
                dateButton.text = selectedDate // Mostrar la fecha seleccionada en el botón
            }, year, month, day)

            datePickerDialog.show()
        }

        // Selector de hora
        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                selectedTime = "$selectedHour:$selectedMinute" // Guardar la hora seleccionada
                timeButton.text = selectedTime // Mostrar la hora seleccionada en el botón
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // Guardar el itinerario
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            selectedGroup = groupSpinner.selectedItem.toString()

            if (title.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                // Crear el nuevo itinerario
                val newItinerary = Itinerary(
                    title = title,
                    date = selectedDate,
                    time = selectedTime,
                    description = description,
                    group = selectedGroup
                )

                // Devolver el resultado a ItineraryActivity
                val resultIntent = Intent()
                resultIntent.putExtra("new_itinerary", newItinerary)
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Cerrar AddItineraryActivity y volver a ItineraryActivity
            }
        }

        // Cancelar la operación
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() // Cerrar AddItineraryActivity
        }
    }
}
