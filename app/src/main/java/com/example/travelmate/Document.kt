package com.example.travelmate

data class Document(
    var id: String = "",
    var name: String = "", // Nombre personalizado del documento
    var filePath: String = "", // URL o URI del archivo
    var uploadedBy: String = "", // Nombre del usuario que subió el documento
    var uploadedById: String = "", // ID del usuario que subió el documento
    var uploadedAt: Long = System.currentTimeMillis() // Fecha de subida en timestamp (por defecto, fecha actual)
)
