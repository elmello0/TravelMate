package com.example.travelmate

import com.example.travelmate.Itinerary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItineraryAdapter(private val itineraryList: List<Itinerary>) : RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_itinerary_title)
        val date: TextView = itemView.findViewById(R.id.tv_itinerary_date)
        val time: TextView = itemView.findViewById(R.id.tv_itinerary_time)
        val description: TextView = itemView.findViewById(R.id.tv_itinerary_description)
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
    }

    override fun getItemCount() = itineraryList.size
}
