package com.example.hobbyexplore.hobbycourse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hobbyexplore.data.Appliance
import com.example.hobbyexplore.data.Course
import com.example.hobbyexplore.databinding.ViewholderApplianceBinding
import com.example.hobbyexplore.databinding.ViewholderCourseBinding
import com.example.hobbyexplore.hobbyappliance.HobbyApplianceAdapter

class HobbyCourseAdapter (private val onClickListener: OnClickListener) : ListAdapter<Course, HobbyCourseAdapter.CourseViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (introduce: Course) -> Unit) {
        fun onClick(introduce: Course) = clickListener(introduce)
    }

    class CourseViewHolder(val binding: ViewholderCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(courseData: Course) {
            binding.course = courseData
            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HobbyCourseAdapter.CourseViewHolder {
        return HobbyCourseAdapter.CourseViewHolder(
            ViewholderCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HobbyCourseAdapter.CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(
        oldItem: Course,
        newItem: Course
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Course,
        newItem: Course
    ): Boolean {
        return oldItem == newItem
    }


}