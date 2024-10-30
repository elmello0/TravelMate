package com.example.travelmate

import java.io.Serializable

data class Expense(
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: String = ""
) : Serializable
