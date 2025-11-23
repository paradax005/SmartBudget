package com.example.smartbusget2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbusget2.R
import com.example.smartbusget2.data.model.Budget

class BudgetAdapter(
    private val onItemClick: (Budget) -> Unit,
    private val onItemLongClick: (Budget) -> Unit
) : ListAdapter<Budget, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryId: TextView = itemView.findViewById(R.id.tvCategoryId)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)

        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(budget: Budget) {
            tvCategoryId.text = budget.categoryId ?: "No category"
            tvAmount.text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault()).format(budget.amount)
            
            // Display month/year instead of period
            val monthYear = if (budget.month != null && budget.year != null) {
                "${budget.month}/${budget.year}"
            } else {
                "N/A"
            }
            tvPeriod.text = monthYear
            
            // Calculate progress from spent amount
            val spent = budget.spent ?: 0.0
            val progress = if (budget.amount > 0) {
                ((spent / budget.amount) * 100).toInt()
            } else {
                0
            }
            
            progressBar.progress = progress.coerceIn(0, 100)
            tvProgress.text = "$progress%"
            
            // Set Status and Colors
            val context = itemView.context
            val (statusText, colorRes) = when {
                progress >= 100 -> Pair("Exceeded", R.color.error_red)
                progress >= 80 -> Pair("Warning", R.color.warning_yellow)
                else -> Pair("On Track", R.color.success_green)
            }
            
            tvStatus.text = statusText
            tvStatus.setTextColor(androidx.core.content.ContextCompat.getColor(context, colorRes))
            tvStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(context, R.color.surface_darker)
            )
            
            // Update Progress Bar Tint
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(context, colorRes)
            )
            
            itemView.setOnClickListener { onItemClick(budget) }
            itemView.setOnLongClickListener {
                onItemLongClick(budget)
                true
            }
        }
    }

    class BudgetDiffCallback : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.actualId() == newItem.actualId()
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem == newItem
        }
    }
}

