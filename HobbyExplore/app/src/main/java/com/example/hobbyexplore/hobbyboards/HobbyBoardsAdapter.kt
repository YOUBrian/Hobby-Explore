package com.example.hobbyexplore.hobbyboards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hobbyexplore.data.Introduce
import com.example.hobbyexplore.data.Message
import com.example.hobbyexplore.databinding.ViewholderBoardsBinding
import com.example.hobbyexplore.databinding.ViewholderCategoryBinding
import com.example.hobbyexplore.hobbycategory.HobbyCategoryAdapter

class HobbyBoardsAdapter (private val onClickListener: OnClickListener) : ListAdapter<Message, HobbyBoardsAdapter.BoardsViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (message: Message) -> Unit) {
        fun onClick(message: Message) = clickListener(message)
    }

    class BoardsViewHolder(val binding: ViewholderBoardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messageData: Message) {
            binding.boards = messageData
            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HobbyBoardsAdapter.BoardsViewHolder {
        return HobbyBoardsAdapter.BoardsViewHolder(
            ViewholderBoardsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HobbyBoardsAdapter.BoardsViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(
        oldItem: Message,
        newItem: Message
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Message,
        newItem: Message
    ): Boolean {
        return oldItem == newItem
    }


}