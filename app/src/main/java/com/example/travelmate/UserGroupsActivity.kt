package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserGroupsActivity : AppCompatActivity() {

    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupList: MutableList<Group> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var viewType: String // Almacena el tipo de vista (expenses, documents, manage_groups, tasks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_groups)

        // Obtener el viewType desde el intent
        viewType = intent.getStringExtra("viewType") ?: "expenses"

        // Configurar RecyclerView
        groupsRecyclerView = findViewById(R.id.recyclerViewGroups)
        groupsAdapter = GroupsAdapter(groupList, viewType) { group ->
            openGroupActivity(group, viewType)
        }

        groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        groupsRecyclerView.adapter = groupsAdapter

        // Cargar grupos del usuario actual
        loadUserGroups()
    }

    private fun loadUserGroups() {
        val currentUser = auth.currentUser?.uid

        if (currentUser == null) {
            Toast.makeText(this, "Por favor, inicia sesión.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("groups")
            .get()
            .addOnSuccessListener { documents ->
                groupList.clear()
                for (document in documents) {
                    val group = document.toObject(Group::class.java)
                    group.groupId = document.id // Asignar el ID del documento

                    // Filtrar grupos donde el usuario actual está en la lista de miembros
                    val isMember = group.members.any { member ->
                        member["userId"] == currentUser
                    }

                    if (isMember) {
                        groupList.add(group)
                    }
                }

                if (groupList.isEmpty()) {
                    Toast.makeText(this, "No tienes grupos disponibles.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("UserGroupsActivity", "Grupos cargados: ${groupList.size}")
                }

                groupsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los grupos: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("UserGroupsActivity", "Error al cargar grupos", e)
            }
    }

    private fun openGroupActivity(group: Group, viewType: String) {
        val targetActivity = when (viewType) {
            "itineraries" -> ItineraryActivity::class.java
            "tasks" -> TasksActivity::class.java
            "expenses" -> GroupExpensesActivity::class.java
            "documents" -> DocumentsActivity::class.java
            "manage_groups" -> ManageGroupActivity::class.java
            else -> null
        }

        targetActivity?.let { activity ->
            val intent = Intent(this, activity)
            intent.putExtra("group_id", group.groupId)
            intent.putExtra("group_name", group.name)
            startActivity(intent)
        } ?: Toast.makeText(this, "Error: Tipo de vista no reconocido.", Toast.LENGTH_SHORT).show()
    }
}