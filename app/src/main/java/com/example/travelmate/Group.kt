package com.example.travelmate

data class Group(
    var groupId: String = "",
    var name: String = "",
    var description: String = "",
    var createdBy: String = "",
    var members: List<Map<String, String>> = emptyList() // Lista de mapas con detalles de miembros
)
