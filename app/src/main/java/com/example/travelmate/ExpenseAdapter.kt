package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val expenseList: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseDescription: TextView = itemView.findViewById(R.id.tvExpenseDescription)
        val expenseAmount: TextView = itemView.findViewById(R.id.tvExpenseAmount)
        val expensePaidBy: TextView = itemView.findViewById(R.id.tvPaidBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = expenseList[position]
        holder.expenseDescription.text = currentExpense.description // Descripción del gasto
        holder.expenseAmount.text = "$${currentExpense.amount}" // Monto del gasto con símbolo de dólar
        holder.expensePaidBy.text = "Pagado por: ${currentExpense.paidBy}" // Persona que pagó
    }

    override fun getItemCount() = expenseList.size
}
