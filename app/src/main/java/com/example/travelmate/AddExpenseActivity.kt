package com.example.travelmate

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etPaidBy: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var groupId: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Obtener el groupId desde el Intent
        groupId = intent.getStringExtra("group_id")
        if (groupId.isNullOrEmpty()) {
            Toast.makeText(this, "No se recibió el ID del grupo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etPaidBy = findViewById(R.id.etPaidBy)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            val description = etDescription.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val paidBy = etPaidBy.text.toString().trim()

            resetFieldBorders()

            if (validateFields(description, amountText, paidBy)) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null) {
                    saveExpense(description, amount, paidBy)
                } else {
                    showErrorBorder(etAmount)
                    Toast.makeText(this, "Por favor, ingresa un monto válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Datos incompletos. Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun validateFields(description: String, amount: String, paidBy: String): Boolean {
        var isValid = true
        if (description.isEmpty()) {
            showErrorBorder(etDescription)
            isValid = false
        }
        if (amount.isEmpty()) {
            showErrorBorder(etAmount)
            isValid = false
        }
        if (paidBy.isEmpty()) {
            showErrorBorder(etPaidBy)
            isValid = false
        }
        return isValid
    }

    private fun showErrorBorder(editText: EditText) {
        val drawable = GradientDrawable()
        drawable.setStroke(4, Color.RED)
        drawable.cornerRadius = 8f
        editText.background = drawable
    }

    private fun resetFieldBorders() {
        etDescription.setBackgroundResource(R.drawable.input_field)
        etAmount.setBackgroundResource(R.drawable.input_field)
        etPaidBy.setBackgroundResource(R.drawable.input_field)
    }

    private fun saveExpense(description: String, amount: Double, paidBy: String) {
        val expense = Expense(
            description = description,
            amount = amount,
            paidBy = paidBy
        )

        // Guardar el gasto en Firestore dentro del grupo
        db.collection("groups")
            .document(groupId!!)
            .collection("expenses")
            .add(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Gasto agregado con éxito", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al agregar el gasto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
