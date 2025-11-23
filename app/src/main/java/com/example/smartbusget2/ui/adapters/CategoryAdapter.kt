package com.example.smartbusget2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbusget2.R
import com.example.smartbusget2.data.model.Category

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit,
    private val onItemLongClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)

        fun bind(category: Category) {
            try {
                tvName.text = category.name ?: "Unknown Category"
                tvType.text = category.type?.replaceFirstChar { it.uppercase() } ?: "Unknown"
                tvIcon.text = category.icon ?: "📁"
                
                itemView.setOnClickListener { onItemClick(category) }
                itemView.setOnLongClickListener {
                    onItemLongClick(category)
                    true
                }
            } catch (e: Exception) {
                android.util.Log.e("CategoryAdapter", "Error binding category: ${e.message}")
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.actualId() == newItem.actualId()
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}




