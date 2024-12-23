package com.example.travelmate

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ManageGroupActivity : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var membersRecyclerView: RecyclerView

    private lateinit var membersAdapter: SelectedMembersAdapter
    private val membersList: MutableList<User> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private var groupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_group)

        // Inicializar vistas
        groupNameEditText = findViewById(R.id.etGroupName)
        groupDescriptionEditText = findViewById(R.id.etGroupDescription)
        saveChangesButton = findViewById(R.id.btnSaveGroupChanges)
        membersRecyclerView = findViewById(R.id.recyclerViewGroupMembers)

        // Obtener el ID del grupo de los extras
        groupId = intent.getStringExtra("group_id") ?: ""

        if (groupId.isEmpty()) {
            Toast.makeText(this, "No se pudo cargar el grupo.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar RecyclerView
        membersAdapter = SelectedMembersAdapter(membersList) { member ->
            removeMember(member)
        }
        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersRecyclerView.adapter = membersAdapter

        // Cargar datos del grupo
        loadGroupData()

        // Guardar cambios en el grupo
        saveChangesButton.setOnClickListener {
            saveGroupChanges()
        }
    }

    private fun loadGroupData() {
        db.collection("groups").document(groupId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    groupNameEditText.setText(document.getString("name"))
                    groupDescriptionEditText.setText(document.getString("description"))

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
                Log.e("ManageGroupActivity", "Error al cargar el grupo", e)
            }
    }

    private fun saveGroupChanges() {
        val newGroupName = groupNameEditText.text.toString().trim()
        val newGroupDescription = groupDescriptionEditText.text.toString().trim()

        if (newGroupName.isEmpty()) {
            Toast.makeText(this, "El nombre del grupo no puede estar vacío.", Toast.LENGTH_SHORT).show()
            return
        }

        val groupUpdates = hashMapOf<String, Any>(
            "name" to newGroupName,
            "description" to newGroupDescription,
            "members" to membersList.map {
                mapOf(
                    "userId" to it.userId,
                    "username" to it.username,
                    "email" to it.email
                )
            }
        )

        db.collection("groups").document(groupId).update(groupUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Cambios guardados exitosamente.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Notificar a la actividad anterior que hubo cambios
                finish() // Cerrar esta actividad y volver
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ManageGroupActivity", "Error al guardar cambios", e)
            }
    }

    private fun removeMember(member: User) {
        membersList.remove(member)
        membersAdapter.notifyDataSetChanged()
        Toast.makeText(this, "${member.username} eliminado del grupo.", Toast.LENGTH_SHORT).show()
    }
}
