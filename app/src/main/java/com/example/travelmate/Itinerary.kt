package com.example.travelmate

import java.io.Serializable

data class Itinerary(
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val group: String = ""
) : Serializable
