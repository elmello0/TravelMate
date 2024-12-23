package com.example.travelmate

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ItineraryAdapter(
    private val itineraryList: MutableList<Itinerary>,
    private val onItineraryChecked: (Itinerary) -> Unit,
    private val onItineraryDeleted: (Itinerary) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_itinerary_title)
        val date: TextView = itemView.findViewById(R.id.tv_itinerary_date)
        val time: TextView = itemView.findViewById(R.id.tv_itinerary_time)
        val description: TextView = itemView.findViewById(R.id.tv_itinerary_description)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_itinerary_completed) // Nuevo campo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_itinerary, parent, false)
        return ItineraryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val currentItem = itineraryList[position]
        holder.title.text = currentItem.title
        holder.date.text = currentItem.date
        holder.time.text = currentItem.time
        holder.description.text = currentItem.description
        holder.checkBox.isChecked = currentItem.isCompleted

        // Estilo visual para itinerarios completados
        if (currentItem.isCompleted) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.description.paintFlags = holder.description.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.description.paintFlags = holder.description.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Listener para el CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isCompleted = isChecked
            onItineraryChecked(currentItem) // Notificar cambios
        }

        // Listener para eliminar itinerario con un long click
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar itinerario")
                .setMessage("¿Estás seguro de que quieres eliminar este itinerario?")
                .setPositiveButton("Eliminar") { _, _ ->
                    onItineraryDeleted(currentItem)
                    itineraryList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }
    }

    override fun getItemCount() = itineraryList.size
}
