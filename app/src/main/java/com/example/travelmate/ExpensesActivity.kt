package com.example.travelmate

import android.app.Activity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpensesActivity : AppCompatActivity() {

    private lateinit var recyclerViewGroups: RecyclerView
    private lateinit var expenseGroupAdapter: ExpenseGroupAdapter
    private lateinit var groupList: MutableList<Group>
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()

    // Nuevo: TextView para mostrar mensaje de "No hay grupos"
    private lateinit var noGroupsMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarExpenses)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Configuración de Firestore
        db = FirebaseFirestore.getInstance()

        // Configuración del RecyclerView para grupos
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups)
        recyclerViewGroups.layoutManager = LinearLayoutManager(this) // Lista vertical
        groupList = mutableListOf()

        // Adapter para los grupos
        expenseGroupAdapter = ExpenseGroupAdapter(groupList) { group ->
            openGroupExpenses(group)
        }
        recyclerViewGroups.adapter = expenseGroupAdapter

        // TextView para mostrar "No hay grupos"
        noGroupsMessage = findViewById(R.id.tvNoGroups)

        val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)
        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivityForResult(intent, 1)
        }

        // Cargar grupos y escuchar cambios en tiempo real
        loadGroups()
    }

    private fun loadGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Por favor, inicia sesión.", Toast.LENGTH_SHORT).show()
            return
        }

        // Uso de addSnapshotListener en lugar de get() para obtener actualizaciones en tiempo real
        db.collection("groups")
            .whereEqualTo("createdBy", currentUser.uid)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar grupos: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                // Limpiar la lista antes de llenarla
                groupList.clear()

                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val group = document.toObject(Group::class.java)
                        group.groupId = document.id
                        groupList.add(group)
                    }
                }

                // Si la lista está vacía, mostrar el mensaje de "No hay grupos"
                if (groupList.isEmpty()) {
                    noGroupsMessage.visibility = View.VISIBLE
                    recyclerViewGroups.visibility = View.GONE
                } else {
                    noGroupsMessage.visibility = View.GONE
                    recyclerViewGroups.visibility = View.VISIBLE
                }

                expenseGroupAdapter.notifyDataSetChanged()
            }
    }

    private fun openGroupExpenses(group: Group) {
        val intent = Intent(this, GroupExpensesActivity::class.java)
        intent.putExtra("groupId", group.groupId)
        intent.putExtra("groupName", group.name)
        startActivity(intent)
    }
}
