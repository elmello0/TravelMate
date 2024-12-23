package com.example.travelmate

data class Chat(
    val chatId: String = "",
    val name: String = "",
    val participants: List<String> = emptyList()
)
