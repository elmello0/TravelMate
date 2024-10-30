package com.example.travelmate

data class Document(
    val id: String = "",          // ID del documento en Firebase
    val name: String = "",         // Nombre del documento
    val filePath: String = ""      // Ruta del archivo
)
