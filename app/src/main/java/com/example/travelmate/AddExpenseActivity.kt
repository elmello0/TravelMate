package com.example.travelmate

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etPaidBy: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etPaidBy = findViewById(R.id.etPaidBy)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            val description = etDescription.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val paidBy = etPaidBy.text.toString().trim()

            if (description.isNotEmpty() && amountText.isNotEmpty() && paidBy.isNotEmpty()) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null) {
                    val resultIntent = intent
                    resultIntent.putExtra("expense_description", description)
                    resultIntent.putExtra("expense_amount", amount)
                    resultIntent.putExtra("expense_paid_by", paidBy)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
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
}
