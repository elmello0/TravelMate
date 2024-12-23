package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var tvGroupName: TextView
    private lateinit var tvGroupDescription: TextView
    private lateinit var recyclerViewGroupMembers: RecyclerView
    private lateinit var btnOpenChat: Button

    private val db = FirebaseFirestore.getInstance()
    private var groupId: String = ""
    private val membersList: MutableList<User> = mutableListOf()
    private lateinit var membersAdapter: SelectedMembersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        tvGroupName = findViewById(R.id.tvGroupName)
        tvGroupDescription = findViewById(R.id.tvGroupDescription)
        recyclerViewGroupMembers = findViewById(R.id.recyclerViewGroupMembers)
        btnOpenChat = findViewById(R.id.btnOpenChat)

        groupId = intent.getStringExtra("group_id") ?: ""
        if (groupId.isEmpty()) {
            Toast.makeText(this, "No se pudo cargar el grupo.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar RecyclerView de miembros
        membersAdapter = SelectedMembersAdapter(membersList) { /* No removemos aquí */ }
        recyclerViewGroupMembers.layoutManager = LinearLayoutManager(this)
        recyclerViewGroupMembers.adapter = membersAdapter

        loadGroupData()

        btnOpenChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("group_id", groupId)
            startActivity(intent)
        }
    }

    private fun loadGroupData() {
        db.collection("groups").document(groupId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvGroupName.text = document.getString("name") ?: "Sin nombre"
                    tvGroupDescription.text = document.getString("description") ?: "Sin descripción"

                    val members = document.get("members") as? List<Map<String, String>>
                    if (members != null) {
                        membersList.clear()
                        for (memberData in members) {
                            val user = User(
                                userId = memberData["userId"] ?: "",
                                username = memberData["username"] ?: "",
                                email = memberData["email"] ?: ""
                            )
                            membersList.add(user)
                        }
                        membersAdapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar el grupo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
