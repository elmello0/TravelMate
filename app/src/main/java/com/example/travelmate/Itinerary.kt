package com.example.travelmate

import java.io.Serializable

data class Itinerary(
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val group: String = "",
    var isCompleted: Boolean = false, // Campo necesario para manejar itinerarios completados
    var documentId: String? = null // ID para Firestore
) : Serializable
