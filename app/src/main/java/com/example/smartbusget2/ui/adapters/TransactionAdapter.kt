package com.example.smartbusget2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbusget2.R
import com.example.smartbusget2.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        private val viewCategoryIcon: View = itemView.findViewById(R.id.viewCategoryIcon)

        fun bind(transaction: Transaction) {
            tvDescription.text = transaction.description ?: "No description"
            
            // Format amount with currency
            val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
            tvAmount.text = currencyFormat.format(transaction.amount)
            
            // Set amount color based on type
            tvAmount.setTextColor(
                itemView.context.getColor(
                    if (transaction.type.lowercase() == "income") R.color.emerald_500 else R.color.red_500
                )
            )
            
            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(transaction.date)
                tvDate.text = date?.let { outputFormat.format(it) } ?: transaction.date
            } catch (e: Exception) {
                tvDate.text = transaction.date
            }
            
            tvCategory.text = transaction.category?.name ?: "No category"
            
            // Set category icon (placeholder emoji for now)
            tvCategoryIcon.text = "📊"
            
            // Set icon background gradient based on type
            val gradientDrawable = if (transaction.type.lowercase() == "income") {
                itemView.context.getDrawable(R.drawable.gradient_icon_background_income)
            } else {
                itemView.context.getDrawable(R.drawable.gradient_icon_background_expense)
            }
            gradientDrawable?.let {
                viewCategoryIcon.background = it
            }
            
            itemView.setOnClickListener {
                itemView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onItemClick(transaction)
            }
            itemView.setOnLongClickListener {
                itemView.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                onItemLongClick(transaction)
                true
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.actualId() == newItem.actualId()
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

