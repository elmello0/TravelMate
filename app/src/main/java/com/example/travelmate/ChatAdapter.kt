package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderNameTextView: TextView = itemView.findViewById(R.id.senderName) // Cambiado para mostrar el nombre
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContent)
        val messageImageView: ImageView = itemView.findViewById(R.id.messageImage)
        val messageDocumentTextView: TextView = itemView.findViewById(R.id.messageDocument)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        // Mostrar el nombre del remitente
        holder.senderNameTextView.text = message.senderName // Ahora se usa senderName en lugar de senderId

        // Manejar los diferentes tipos de mensajes (texto, imagen, documento)
        when (message.type) {
            "text" -> {
                holder.messageContentTextView.visibility = View.VISIBLE
                holder.messageContentTextView.text = message.content
                holder.messageImageView.visibility = View.GONE
                holder.messageDocumentTextView.visibility = View.GONE
            }
            "image" -> {
                holder.messageImageView.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.imageUrl).into(holder.messageImageView)
                holder.messageContentTextView.visibility = View.GONE
                holder.messageDocumentTextView.visibility = View.GONE
            }
            "document" -> {
                holder.messageDocumentTextView.visibility = View.VISIBLE
                holder.messageDocumentTextView.text = "Documento: ${message.documentUrl}"
                holder.messageContentTextView.visibility = View.GONE
                holder.messageImageView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = messages.size
}
