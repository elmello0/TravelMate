package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AddMembersActivity : AppCompatActivity() {

    private lateinit var searchUserEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var cancelAddMembersButton: Button
    private lateinit var saveMembersButton: Button
    private lateinit var userListAdapter: UserListAdapter
    private val db = FirebaseFirestore.getInstance()

    private var userList: ArrayList<User> = arrayListOf()
    private var selectedUsers: MutableList<User> = mutableListOf() // MutableList para gestionar la selección

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members)

        // Inicializar las vistas
        searchUserEditText = findViewById(R.id.etSearchUser)
        recyclerView = findViewById(R.id.recyclerViewUsers)
        cancelAddMembersButton = findViewById(R.id.btnCancelAddMembers)
        saveMembersButton = findViewById(R.id.btnSaveMembers)

        // Configurar RecyclerView
        userListAdapter = UserListAdapter(userList) { user ->
            toggleUserSelection(user)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userListAdapter

        // Cargar usuarios desde Firestore
        loadUsers()

        // Configurar búsqueda en tiempo real
        setupSearchFunctionality()

        // Botón para cancelar
        cancelAddMembersButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // Botón para guardar miembros seleccionados
        saveMembersButton.setOnClickListener {
            if (selectedUsers.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putParcelableArrayListExtra("selected_users", ArrayList(selectedUsers))
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Selecciona al menos un usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Alterna la selección de un usuario.
     */
    private fun toggleUserSelection(user: User) {
        if (user.isSelected) {
            selectedUsers.add(user)
        } else {
            selectedUsers.remove(user)
        }
    }

    private fun loadUsers() {
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
                userListAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchFunctionality() {
        searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchUsers(query)
                } else {
                    userListAdapter.updateList(userList) // Mostrar todos los usuarios si no hay búsqueda
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchUsers(query: String) {
        val filteredList = userList.filter {
            it.username.contains(query, ignoreCase = true) || it.email.contains(query, ignoreCase = true)
        }
        userListAdapter.updateList(filteredList)
    }
}
