package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserListAdapter(
    private var userList: MutableList<User>, // Lista mutable para poder actualizar
    private val onAddClick: (User) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val userEmailTextView: TextView = itemView.findViewById(R.id.tvUserEmail)
        val addButton: Button = itemView.findViewById(R.id.btnAddUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userNameTextView.text = currentUser.username // Cambiado de name a username
        holder.userEmailTextView.text = currentUser.email
        holder.addButton.text = if (currentUser.isSelected) "Eliminar" else "Añadir"

        holder.addButton.setOnClickListener {
            currentUser.isSelected = !currentUser.isSelected
            notifyItemChanged(position) // Notificar cambio en posición
            onAddClick(currentUser) // Callback para manejar el clic
        }
    }

    override fun getItemCount() = userList.size

    /**
     * Actualiza la lista de usuarios mostrada.
     */
    fun updateList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged() // Notifica que toda la lista ha cambiado
    }
}
