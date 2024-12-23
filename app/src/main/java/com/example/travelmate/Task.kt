package com.example.travelmate

import java.io.Serializable
import java.util.Date

data class Task(
    val title: String = "",
    val description: String = "",
    var isCompleted: Boolean = false, // Permite modificar desde el adaptador
    var documentId: String? = null,  // ID del documento en Firestore
    val createdAt: Date = Date(),    // Fecha de creación
    var updatedAt: Date? = null      // Fecha de última modificación
) : Serializable
