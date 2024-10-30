package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserListAdapter(private val userList: List<User>, private val onAddClick: (User) -> Unit) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    // Clase interna que define el ViewHolder para el RecyclerView
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val addButton: Button = itemView.findViewById(R.id.btnAddUser)
    }

    // Método que infla el layout para cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    // Método que asigna los valores de cada elemento de la lista al ViewHolder
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userNameTextView.text = currentUser.name

        // Actualiza el texto del botón en función de si el usuario está seleccionado o no
        holder.addButton.text = if (currentUser.isSelected) "Eliminar" else "Añadir"

        // Acción cuando se hace clic en el botón de "Añadir" o "Eliminar"
        holder.addButton.setOnClickListener {
            onAddClick(currentUser)
        }
    }

    // Método que devuelve el número de elementos en la lista
    override fun getItemCount() = userList.size
}
