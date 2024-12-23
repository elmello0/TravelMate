package com.example.travelmate

data class ChatMessage(
    val senderId: String = "",
    val senderName: String = "", // Nuevo campo para el nombre del remitente
    val content: String? = null,
    val imageUrl: String? = null,
    val documentUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "text"
)

