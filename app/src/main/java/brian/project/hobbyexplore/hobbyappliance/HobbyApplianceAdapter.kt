package brian.project.hobbyexplore.hobbyappliance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brian.project.hobbyexplore.data.Appliance
import brian.project.hobbyexplore.data.Introduce
import brian.project.hobbyexplore.databinding.ViewholderApplianceBinding
import brian.project.hobbyexplore.databinding.ViewholderCategoryBinding
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryAdapter

class HobbyApplianceAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Appliance, HobbyApplianceAdapter.ApplianceViewHolder>(DiffCallback()) {

    class OnClickListener(val clickListener: (introduce: Appliance) -> Unit) {
        fun onClick(introduce: Appliance) = clickListener(introduce)
    }

    class ApplianceViewHolder(val binding: ViewholderApplianceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(applianceData: Appliance) {
            binding.appliance = applianceData
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HobbyApplianceAdapter.ApplianceViewHolder {
        return HobbyApplianceAdapter.ApplianceViewHolder(
            ViewholderApplianceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: HobbyApplianceAdapter.ApplianceViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener.onClick(getItem(position))
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Appliance>() {
    override fun areItemsTheSame(
        oldItem: Appliance,
        newItem: Appliance
    ): Boolean {

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Appliance,
        newItem: Appliance
    ): Boolean {
        return oldItem == newItem
    }
}
