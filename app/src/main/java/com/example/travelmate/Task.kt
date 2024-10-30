package com.example.travelmate

import java.io.Serializable

data class Task(
    val title: String = "",
    val description: String = "",
    var isCompleted: Boolean = false // Cambia a 'var' para permitir la modificación
) : Serializable
