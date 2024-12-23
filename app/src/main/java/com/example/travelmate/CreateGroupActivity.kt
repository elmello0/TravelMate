package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var createGroupButton: Button
    private lateinit var addMembersButton: Button
    private lateinit var membersRecyclerView: RecyclerView

    private lateinit var membersAdapter: SelectedMembersAdapter
    private var selectedUsers: ArrayList<User> = arrayListOf()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Inicializar vistas
        groupNameEditText = findViewById(R.id.etGroupName)
        groupDescriptionEditText = findViewById(R.id.etGroupDescription)
        createGroupButton = findViewById(R.id.btnCreateGroup)
        addMembersButton = findViewById(R.id.btnAddMembers)
        membersRecyclerView = findViewById(R.id.recyclerViewGroupMembers)

        // Configurar RecyclerView para mostrar los miembros seleccionados
        membersAdapter = SelectedMembersAdapter(selectedUsers) { user ->
            removeMember(user)
        }
        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersRecyclerView.adapter = membersAdapter

        // Configurar botón "Añadir miembros"
        addMembersButton.setOnClickListener {
            val intent = Intent(this, AddMembersActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_MEMBERS)
        }

        // Configurar botón "Crear Grupo"
        createGroupButton.text = "Crear Grupo"
        createGroupButton.setOnClickListener {
            saveGroupToFirestore()
        }
    }

    private fun removeMember(user: User) {
        selectedUsers.remove(user)
        membersAdapter.notifyDataSetChanged() // Actualizar lista en pantalla
        Toast.makeText(this, "${user.username} eliminado del grupo", Toast.LENGTH_SHORT).show()
    }

    private fun saveGroupToFirestore() {
        val groupName = groupNameEditText.text.toString().trim()
        val groupDescription = groupDescriptionEditText.text.toString().trim()

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre para el grupo", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show()
            return
        }

        // Agregar al usuario actual automáticamente como miembro
        val currentUserData = mapOf(
            "userId" to currentUser.uid,
            "username" to (currentUser.displayName ?: "Usuario anónimo"),
            "email" to (currentUser.email ?: "")
        )

        if (selectedUsers.none { it.userId == currentUser.uid }) {
            selectedUsers.add(User(currentUser.uid, currentUser.displayName ?: "Usuario anónimo", currentUser.email ?: ""))
        }

        val groupData = hashMapOf(
            "name" to groupName,
            "description" to groupDescription,
            "createdBy" to currentUser.uid,
            "members" to selectedUsers.map { mapOf("userId" to it.userId, "username" to it.username, "email" to it.email) }
        )

        Log.d(TAG, "Creando grupo: $groupData")

        db.collection("groups")
            .add(groupData)
            .addOnSuccessListener {
                Toast.makeText(this, "Grupo guardado exitosamente", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Grupo creado exitosamente en Firestore.")
                finish() // Volver a la actividad anterior
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar grupo: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error al guardar grupo", e)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_MEMBERS && resultCode == RESULT_OK && data != null) {
            val newMembers = data.getParcelableArrayListExtra<User>("selected_users")
            if (newMembers != null) {
                selectedUsers.clear()
                selectedUsers.addAll(newMembers)
                membersAdapter.notifyDataSetChanged() // Actualizar lista en pantalla
                Log.d(TAG, "Miembros seleccionados: ${selectedUsers.map { it.username }}")
            }
        }
    }

    companion object {
        private const val REQUEST_ADD_MEMBERS = 1
        private const val TAG = "CreateGroupActivity"
    }
}