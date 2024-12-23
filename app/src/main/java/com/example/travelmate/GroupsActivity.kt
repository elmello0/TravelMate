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

class GroupsActivity : AppCompatActivity() {

    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList: MutableList<Group> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        // Inicializar RecyclerView
        groupsRecyclerView = findViewById(R.id.recyclerViewGroups)
        groupsAdapter = GroupsAdapter(groupsList) { group ->
            // Mantener el flujo actual: abrir ManageGroupActivity
            openManageGroup(group)
        }
        groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        groupsRecyclerView.adapter = groupsAdapter

        // Cargar los grupos creados por el usuario actual
        loadUserGroups()
    }

    private fun loadUserGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Por favor, inicia sesión.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("groups")
            .whereEqualTo("createdBy", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                groupsList.clear()
                for (document in documents) {
                    val group = document.toObject(Group::class.java).copy(groupId = document.id)
                    groupsList.add(group)
                }
                groupsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("GroupsActivity", "Error al cargar grupos: ${e.message}", e)
                Toast.makeText(this, "Error al cargar los grupos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openManageGroup(group: Group) {
        val intent = Intent(this, ManageGroupActivity::class.java)
        intent.putExtra("group_id", group.groupId)
        startActivity(intent)
    }

    // Nuevo método que no altera el flujo actual.
    // Puedes utilizarlo en el futuro si quieres abrir GroupDetailsActivity para ver información del grupo y luego el chat.
    private fun openGroupDetails(group: Group) {
        val intent = Intent(this, GroupDetailsActivity::class.java)
        intent.putExtra("group_id", group.groupId)
        startActivity(intent)
    }
}
