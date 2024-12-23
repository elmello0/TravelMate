package com.example.travelmate

import android.app.AlertDialog
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription) // Nuevo campo para la descripción
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxTaskCompleted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.taskName.text = currentItem.title
        holder.taskDescription.text = currentItem.description // Configurar la descripción

        // Desactivar el listener temporalmente para evitar bucles
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isCompleted

        // Aplicar estilo si la tarea está completada
        if (currentItem.isCompleted) {
            holder.taskName.paintFlags = holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.taskName.paintFlags = holder.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Listener para el CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isCompleted = isChecked
            onTaskChecked(currentItem)
        }

        // Listener para eliminar la tarea con long click
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                .setPositiveButton("Eliminar") { _, _ ->
                    onTaskDeleted(currentItem)
                    taskList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }
    }

    override fun getItemCount() = taskList.size
}
