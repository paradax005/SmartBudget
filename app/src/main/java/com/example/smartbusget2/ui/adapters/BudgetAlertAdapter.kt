package com.example.smartbusget2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbusget2.R
import com.example.smartbusget2.data.remote.BudgetStatusItem
import java.text.NumberFormat
import java.util.*

class BudgetAlertAdapter(
    private val onItemClick: (BudgetStatusItem) -> Unit
) : ListAdapter<BudgetStatusItem, BudgetAlertAdapter.BudgetAlertViewHolder>(BudgetAlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetAlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_alert, parent, false)
        return BudgetAlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetAlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BudgetAlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvBudgetInfo: TextView = itemView.findViewById(R.id.tvBudgetInfo)
        private val tvExceededAmount: TextView = itemView.findViewById(R.id.tvExceededAmount)

        fun bind(budgetStatus: BudgetStatusItem) {
            val category = budgetStatus.category
            tvCategoryIcon.text = category.icon ?: "📊"
            tvCategoryName.text = category.name
            
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val spent = format.format(budgetStatus.spent)
            val budget = format.format(budgetStatus.amount)
            tvBudgetInfo.text = "Spent: $spent / Budget: $budget (${budgetStatus.percentage}%)"
            
            val exceeded = budgetStatus.spent - budgetStatus.amount
            tvExceededAmount.text = format.format(exceeded)
            
            itemView.setOnClickListener { onItemClick(budgetStatus) }
        }
    }

    class BudgetAlertDiffCallback : DiffUtil.ItemCallback<BudgetStatusItem>() {
        override fun areItemsTheSame(oldItem: BudgetStatusItem, newItem: BudgetStatusItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BudgetStatusItem, newItem: BudgetStatusItem): Boolean {
            return oldItem == newItem
        }
    }
}


