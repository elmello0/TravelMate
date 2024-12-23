package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class ExpenseAdapter(
    private var expenseList: List<Expense>, // Lista original de gastos
    private val onExpenseLongClick: (Expense) -> Unit // Callback para long click
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

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

        // Formato CLP
        val formatCLP = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

        holder.expenseDescription.text = currentExpense.description
        holder.expenseAmount.text = formatCLP.format(currentExpense.amount)
        holder.expensePaidBy.text = "Pagado por: ${currentExpense.paidBy}"

        // Lógica para eliminar gasto al hacer long click
        holder.itemView.setOnLongClickListener {
            onExpenseLongClick(currentExpense)
            true
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateExpenses(newExpenses: List<Expense>) {
        this.expenseList = newExpenses
        notifyDataSetChanged() // Notificar cambios en los datos
    }
}
