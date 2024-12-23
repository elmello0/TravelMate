package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseGroupAdapter(
    private val groupList: List<Group>,
    private val onGroupClick: (Group) -> Unit
) : RecyclerView.Adapter<ExpenseGroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.tvGroupName)
        val groupDescription: TextView = itemView.findViewById(R.id.tvGroupDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        holder.groupName.text = group.name
        holder.groupDescription.text = group.description ?: "Sin descripción"

        // Configurar el click en el grupo
        holder.itemView.setOnClickListener { onGroupClick(group) }
    }

    override fun getItemCount(): Int = groupList.size
}
