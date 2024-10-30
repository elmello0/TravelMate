package com.example.travelmate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddMembersActivity : AppCompatActivity() {

    private lateinit var searchUserEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var cancelAddMembersButton: Button
    private lateinit var saveMembersButton: Button

    // Adaptador del RecyclerView para la lista de usuarios
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userList: ArrayList<User> // Lista de usuarios disponibles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members)

        // Inicializar las vistas
        searchUserEditText = findViewById(R.id.etSearchUser)
        recyclerView = findViewById(R.id.recyclerViewUsers)
        cancelAddMembersButton = findViewById(R.id.btnCancelAddMembers)
        saveMembersButton = findViewById(R.id.btnSaveMembers)

        // Crear una lista simulada de usuarios
        userList = arrayListOf(
            User("Usuario1"),
            User("Usuario2"),
            User("Usuario3")
        )

        // Inicializar el adaptador con la lista de usuarios
        userListAdapter = UserListAdapter(userList) { user ->
            // Lógica para manejar cuando un usuario sea añadido o eliminado
            user.isSelected = !user.isSelected // Cambia el estado del usuario al hacer clic
            userListAdapter.notifyDataSetChanged() // Notifica al adaptador sobre el cambio
        }

        // Configurar el RecyclerView con el adaptador y el layout manager
        recyclerView.layoutManager = LinearLayoutManager(this) // Layout en forma de lista vertical
        recyclerView.adapter = userListAdapter // Conectar el adaptador con el RecyclerView

        cancelAddMembersButton.setOnClickListener {
            finish() // Cerrar la actividad si se presiona cancelar
        }

        saveMembersButton.setOnClickListener {
            // Lógica para guardar los miembros seleccionados en el grupo
            val selectedUsers = userList.filter { it.isSelected }
            // Puedes almacenar los miembros seleccionados en una lista o base de datos
            finish() // Finalizar la actividad después de guardar
        }
    }
}
