package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: List<Task>, private val onTaskChecked: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxTaskCompleted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.taskName.text = currentItem.title

        // Desactivar el listener temporalmente para evitar bucles
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isCompleted

        // Volver a establecer el listener
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isCompleted = isChecked
            onTaskChecked(currentItem) // Acción cuando se marca o desmarca una tarea
        }
    }

    override fun getItemCount() = taskList.size
}
