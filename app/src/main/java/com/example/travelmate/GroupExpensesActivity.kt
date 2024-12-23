package com.example.travelmate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.NumberFormat
import java.util.Locale

class GroupExpensesActivity : AppCompatActivity() {

    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var db: FirebaseFirestore
    private lateinit var totalAmountTextView: TextView
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var noExpensesMessage: TextView

    private var totalAmount: Double = 0.0
    private var expensesListener: ListenerRegistration? = null

    private var groupId: String? = null
    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expenses)

        // Recuperar el groupId y el groupName del Intent
        groupId = intent.getStringExtra("group_id")
        groupName = intent.getStringExtra("groupName")

        if (groupId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se recibió el ID del grupo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarGroupExpenses)
        setSupportActionBar(toolbar)
        supportActionBar?.title = groupName ?: "Gastos del Grupo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Inicializar Firebase y vistas
        db = FirebaseFirestore.getInstance()
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses)
        recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        expenseList = arrayListOf()

        // Inicialización del adapter con el callback para long click (eliminar)
        expenseAdapter = ExpenseAdapter(expenseList) { expense ->
            AlertDialog.Builder(this)
                .setTitle("Eliminar Gasto")
                .setMessage("¿Deseas eliminar este gasto?")
                .setPositiveButton("Eliminar") { _, _ ->
                    deleteExpense(expense)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        recyclerViewExpenses.adapter = expenseAdapter

        totalAmountTextView = findViewById(R.id.tvTotalAmount)
        fabAddExpense = findViewById(R.id.fabAddExpense)
        noExpensesMessage = findViewById(R.id.tvNoExpenses)

        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("group_id", groupId) // usar la misma clave "group_id"
            startActivity(intent)
        }

        // Cargar gastos del grupo a través de su subcolección "expenses"
        loadGroupExpenses()
    }

    private fun loadGroupExpenses() {
        expenseList.clear()
        totalAmount = 0.0

        expensesListener = db.collection("groups")
            .document(groupId!!)
            .collection("expenses")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al cargar gastos: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                expenseList.clear()
                totalAmount = 0.0

                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot) {
                        val expense = document.toObject(Expense::class.java)
                        expense.documentId = document.id // Asignar el ID del documento
                        expenseList.add(expense)
                        totalAmount += expense.amount
                    }
                }

                // Mostrar u ocultar el mensaje "No hay gastos"
                if (expenseList.isEmpty()) {
                    noExpensesMessage.visibility = View.VISIBLE
                    recyclerViewExpenses.visibility = View.GONE
                } else {
                    noExpensesMessage.visibility = View.GONE
                    recyclerViewExpenses.visibility = View.VISIBLE
                }

                expenseAdapter.updateExpenses(expenseList)

                // Formatear el total a CLP
                val formatCLP = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
                val formattedTotal = formatCLP.format(totalAmount)
                totalAmountTextView.text = "Monto total: $formattedTotal"
            }
    }

    private fun deleteExpense(expense: Expense) {
        if (expense.documentId.isNullOrEmpty()) {
            Toast.makeText(this, "No se puede eliminar: ID no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("groups")
            .document(groupId!!)
            .collection("expenses")
            .document(expense.documentId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show()
                // addSnapshotListener actualizará la lista automáticamente
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el gasto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        expensesListener?.remove()
    }
}
