package com.example.travelmate

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter(
    private val groupList: List<Group>,
    private val viewType: String = "default",
    private val onGroupClick: (Group) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.tvGroupName)
        val groupMembersTextView: TextView = itemView.findViewById(R.id.tvGroupMembers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]

        // Nombre del grupo, o mensaje si no existe
        holder.groupNameTextView.text = if (group.name.isNotEmpty()) group.name else "Grupo sin nombre"

        // Mostrar la cantidad de miembros
        val memberCount = group.members.size
        holder.groupMembersTextView.text = "Miembros: $memberCount"

        holder.itemView.setOnClickListener {
            if (group.groupId.isEmpty() || group.name.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Datos del grupo incompletos", Toast.LENGTH_SHORT).show()
            } else {
                // Abrir actividad basada en el tipo de vista
                when (viewType) {
                    "expenses" -> openActivity(holder.itemView.context, group, GroupExpensesActivity::class.java)
                    "documents" -> openActivity(holder.itemView.context, group, DocumentsActivity::class.java)
                    "manage_groups" -> openActivity(holder.itemView.context, group, ManageGroupActivity::class.java)
                    else -> onGroupClick(group) // Callback definido en la instancia del adaptador
                }
            }
        }
    }

    private fun openActivity(context: Context, group: Group, activityClass: Class<*>) {
        val intent = Intent(context, activityClass).apply {
            putExtra("group_id", group.groupId)
            putExtra("group_name", group.name)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = groupList.size
}
