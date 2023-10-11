package com.example.hobbyexplore.hobbyboards

import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hobbyexplore.data.Message
import com.example.hobbyexplore.databinding.ViewholderBoardsBinding

class HobbyBoardsAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Message, HobbyBoardsAdapter.BoardsViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (message: Message) -> Unit) {
        fun onClick(message: Message) = clickListener(message)
    }

    class BoardsViewHolder(val binding: ViewholderBoardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messageData: Message) {
            binding.boards = messageData
            binding.executePendingBindings()
        }

        // Toggle visibility of the boards_image using TransitionManager
        fun toggleImageVisibility() {
            val isImageVisible = binding.boardsImage.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup)
            binding.boardsImage.visibility = if (isImageVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyBoardsAdapter.BoardsViewHolder {
        return HobbyBoardsAdapter.BoardsViewHolder(
            ViewholderBoardsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HobbyBoardsAdapter.BoardsViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            // Toggle image visibility when an item is clicked
            holder.toggleImageVisibility()
            // If you want to keep the original click behavior, uncomment the following line:
            // onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
