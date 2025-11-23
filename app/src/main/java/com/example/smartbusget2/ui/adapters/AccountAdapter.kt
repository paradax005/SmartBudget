package com.example.smartbusget2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbusget2.R
import com.example.smartbusget2.data.model.Account

class AccountAdapter(
    private val onItemClick: (Account) -> Unit,
    private val onItemLongClick: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvBalance)
        private val ivIcon: android.widget.ImageView = itemView.findViewById(R.id.ivIcon)
        private val viewIconContainer: android.view.View = itemView.findViewById(R.id.viewIconContainer)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)

        fun bind(account: Account) {
            tvName.text = account.name
            tvType.text = account.type
            tvBalance.text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault()).format(account.balance)
            
            val context = itemView.context
            val type = account.type?.lowercase() ?: "cash"
            
            // Set Icon and Color based on type
            val (iconRes, colorRes) = when {
                type.contains("check") -> Pair(R.drawable.ic_type_checking, R.color.account_checking)
                type.contains("save") || type.contains("savings") -> Pair(R.drawable.ic_type_savings, R.color.account_savings)
                type.contains("credit") -> Pair(R.drawable.ic_type_credit, R.color.account_credit)
                type.contains("invest") -> Pair(R.drawable.ic_type_investment, R.color.account_investment)
                else -> Pair(R.drawable.ic_type_cash, R.color.account_cash)
            }
            
            ivIcon.setImageResource(iconRes)
            viewIconContainer.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(context, colorRes)
            )
            
            // Set Balance Color and Badge
            if (type.contains("credit")) {
                tvBalance.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                tvStatusBadge.visibility = View.VISIBLE
                tvStatusBadge.text = "Debt"
                tvStatusBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(context, R.color.error_red)
                )
            } else {
                tvBalance.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.success_green))
                tvStatusBadge.visibility = View.GONE
            }
            
            itemView.setOnClickListener { onItemClick(account) }
            itemView.setOnLongClickListener {
                onItemLongClick(account)
                true
            }
        }
    }

    class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.actualId() == newItem.actualId()
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
}

