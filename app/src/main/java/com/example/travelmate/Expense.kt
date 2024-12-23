package com.example.travelmate

data class Expense(
    var description: String = "",
    var amount: Double = 0.0,
    var paidBy: String = "",
    var groupName: String = "", // Nuevo campo para asociar el gasto con un grupo
    var documentId: String? = null
)
