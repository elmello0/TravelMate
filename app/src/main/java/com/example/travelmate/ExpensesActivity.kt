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

class ExpensesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var db: FirebaseFirestore
    private val ADD_EXPENSE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)

        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerViewExpenses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        expenseList = arrayListOf()
        expenseAdapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = expenseAdapter

        loadExpenses()

        val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)
        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val description = data?.getStringExtra("expense_description") ?: ""
            val amount = data?.getDoubleExtra("expense_amount", 0.0) ?: 0.0
            val paidBy = data?.getStringExtra("expense_paid_by") ?: ""

            if (description.isNotEmpty() && amount > 0.0 && paidBy.isNotEmpty()) {
                val newExpense = Expense(description = description, amount = amount, paidBy = paidBy)

                db.collection("expenses").add(newExpense)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Gasto añadido", Toast.LENGTH_SHORT).show()
                        expenseList.add(newExpense)
                        expenseAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al añadir gasto: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadExpenses() {
        expenseList.clear()
        db.collection("expenses").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val expense = document.toObject(Expense::class.java)
                    expenseList.add(expense)
                }
                expenseAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar gastos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
