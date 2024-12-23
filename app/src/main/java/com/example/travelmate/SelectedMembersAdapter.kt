package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectedMembersAdapter(
    private val selectedUsers: MutableList<User>,
    private val onRemoveClick: (User) -> Unit
) : RecyclerView.Adapter<SelectedMembersAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val userEmailTextView: TextView = itemView.findViewById(R.id.tvUserEmail)
        val removeButton: Button = itemView.findViewById(R.id.btnRemoveUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_member, parent, false)
        return MemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val user = selectedUsers[position]
        holder.userNameTextView.text = user.username
        holder.userEmailTextView.text = user.email

        holder.removeButton.setOnClickListener {
            onRemoveClick(user)
        }
    }

    override fun getItemCount() = selectedUsers.size
}
