package brian.project.hobbyexplore.hobbyappliance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentHobbyApplianceBinding
import brian.project.hobbyexplore.databinding.FragmentHobbyCourseBinding
import brian.project.hobbyexplore.detail.DetailFragmentArgs
import brian.project.hobbyexplore.detail.DetailFragmentDirections
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryAdapter
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryFragmentDirections
import brian.project.hobbyexplore.hobbycourse.HobbyCourseFragmentDirections
import brian.project.hobbyexplore.hobbycourse.HobbyCourseViewModel

class HobbyApplianceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyApplianceViewModel = ViewModelProvider(this).get(HobbyApplianceViewModel::class.java)
        val sportName = HobbyApplianceFragmentArgs.fromBundle(requireArguments()).sportName
        val binding = FragmentHobbyApplianceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        Log.i("getSportssss", "$sportName")
        viewModel.getApplianceData(sportName)

        val hobbyApplianceAdapter = HobbyApplianceAdapter(HobbyApplianceAdapter.OnClickListener {
//            viewModel.navigateToDetail(it)
        })

        binding.hobbyApplianceRecyclerView.adapter = hobbyApplianceAdapter

        viewModel.applianceList.observe(viewLifecycleOwner, Observer { appliances->
            hobbyApplianceAdapter.submitList(appliances)
            // hobbyApplianceAdapter.notifyDataSetChanged()
        })

        binding.courseButton.setOnClickListener {
            it.findNavController().navigate(HobbyApplianceFragmentDirections.actionHobbyAppliaceFragmentToHobbyCourseFragment(sportName))
        }

        binding.placeButton.setOnClickListener {
            it.findNavController().navigate(HobbyApplianceFragmentDirections.actionHobbyAppliaceFragmentToHobbyPlaceFragment(sportName))
        }

        return binding.root
    }
}