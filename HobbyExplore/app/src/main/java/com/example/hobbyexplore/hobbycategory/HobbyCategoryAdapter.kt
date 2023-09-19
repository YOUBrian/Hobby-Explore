package com.example.hobbyexplore.hobbycategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hobbyexplore.data.Introduce
import com.example.hobbyexplore.databinding.ViewholderCategoryBinding

class HobbyCategoryAdapter(private val onClickListener: OnClickListener) : ListAdapter<Introduce, HobbyCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (introduce: Introduce) -> Unit) {
        fun onClick(introduce: Introduce) = clickListener(introduce)
    }

    class CategoryViewHolder(val binding: ViewholderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(introduceData: Introduce) {
            binding.introduce = introduceData
            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HobbyCategoryAdapter.CategoryViewHolder {
        return HobbyCategoryAdapter.CategoryViewHolder(
            ViewholderCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HobbyCategoryAdapter.CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Introduce>() {
    override fun areItemsTheSame(
        oldItem: Introduce,
        newItem: Introduce
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Introduce,
        newItem: Introduce
    ): Boolean {
        return oldItem == newItem
    }


}