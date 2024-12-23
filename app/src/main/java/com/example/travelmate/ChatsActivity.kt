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

class ChatsActivity : AppCompatActivity() {

    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList: MutableList<Group> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // Inicializar RecyclerView
        groupsRecyclerView = findViewById(R.id.recyclerViewChats)
        groupsAdapter = GroupsAdapter(groupsList) { group ->
            openChat(group)
        }
        groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        groupsRecyclerView.adapter = groupsAdapter

        // Cargar los grupos disponibles
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
            .get() // Obtenemos todos los grupos
            .addOnSuccessListener { documents ->
                groupsList.clear()
                for (document in documents) {
                    val members = document.get("members") as? List<Map<String, Any>>
                    if (members != null) {
                        // Verificamos si el userId del usuario actual está en los miembros
                        val isMember = members.any { it["userId"] == currentUser.uid }
                        if (isMember) {
                            val group = document.toObject(Group::class.java).copy(groupId = document.id)
                            groupsList.add(group)
                        }
                    }
                }
                if (groupsList.isEmpty()) {
                    Toast.makeText(this, "No tienes grupos disponibles.", Toast.LENGTH_SHORT).show()
                }
                groupsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ChatsActivity", "Error al cargar grupos: ${e.message}", e)
                Toast.makeText(this, "Error al cargar los grupos.", Toast.LENGTH_SHORT).show()
            }
    }



    private fun openChat(group: Group) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("group_id", group.groupId) // Pasar el groupId al ChatActivity
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        loadUserGroups() // Recargar los grupos disponibles
    }

}
