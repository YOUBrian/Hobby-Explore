package brian.project.hobbyexplore.hobbyplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brian.project.hobbyexplore.data.Place
import brian.project.hobbyexplore.databinding.ViewholderCourseBinding
import brian.project.hobbyexplore.databinding.ViewholderPlaceBinding
import brian.project.hobbyexplore.hobbycourse.HobbyCourseAdapter

class HobbyPlaceAdapter (private val onClickListener: OnClickListener) : ListAdapter<Place, HobbyPlaceAdapter.PlaceViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (introduce: Place) -> Unit) {
        fun onClick(introduce: Place) = clickListener(introduce)
    }

    class PlaceViewHolder(val binding: ViewholderPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(placeData: Place) {
            binding.place = placeData
            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HobbyPlaceAdapter.PlaceViewHolder {
        return HobbyPlaceAdapter.PlaceViewHolder(
            ViewholderPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HobbyPlaceAdapter.PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(
        oldItem: Place,
        newItem: Place
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Place,
        newItem: Place
    ): Boolean {
        return oldItem == newItem
    }


}