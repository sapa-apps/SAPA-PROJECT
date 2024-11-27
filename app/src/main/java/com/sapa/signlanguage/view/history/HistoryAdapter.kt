package com.sapa.signlanguage.view.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapa.signlanguage.data.db.TranslationHistory
import com.sapa.signlanguage.databinding.ItemHistoryBinding


class HistoryAdapter(
    private val onItemClick: (TranslationHistory) -> Unit
) : ListAdapter<TranslationHistory, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: TranslationHistory, onItemClick: (TranslationHistory) -> Unit) {
            binding.originalText.text = history.originalText
            binding.translatedText.text = history.translatedText
            binding.root.setOnClickListener { onItemClick(history) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("HistoryAdapter", "Binding item: $item")
        holder.bind(item, onItemClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<TranslationHistory>() {
        override fun areItemsTheSame(oldItem: TranslationHistory, newItem: TranslationHistory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TranslationHistory, newItem: TranslationHistory) =
            oldItem == newItem
    }
}

