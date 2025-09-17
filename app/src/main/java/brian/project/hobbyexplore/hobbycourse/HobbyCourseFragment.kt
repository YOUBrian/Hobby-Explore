package brian.project.hobbyexplore.hobbycourse

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentDetailBinding
import brian.project.hobbyexplore.databinding.FragmentHobbyApplianceBinding
import brian.project.hobbyexplore.databinding.FragmentHobbyCourseBinding
import brian.project.hobbyexplore.detail.DetailFragmentArgs
import brian.project.hobbyexplore.detail.DetailFragmentDirections
import brian.project.hobbyexplore.detail.DetailViewModel
import brian.project.hobbyexplore.detail.DetailViewModelFactory
import brian.project.hobbyexplore.hobbyappliance.HobbyApplianceAdapter
import brian.project.hobbyexplore.hobbyappliance.HobbyApplianceFragmentArgs
import brian.project.hobbyexplore.hobbyappliance.HobbyApplianceViewModel
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryViewModel
import brian.project.hobbyexplore.hobbyplace.HobbyPlaceFragmentDirections

class HobbyCourseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyCourseViewModel =
            ViewModelProvider(this).get(HobbyCourseViewModel::class.java)
        val sportName = HobbyCourseFragmentArgs.fromBundle(requireArguments()).sportName
        val binding = FragmentHobbyCourseBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getCourseData(sportName.toString())

        val hobbyCourseAdapter = HobbyCourseAdapter(HobbyCourseAdapter.OnClickListener {
            viewModel.navigateToYoutube(it)
        })

        binding.hobbyCourseRecyclerView.adapter = hobbyCourseAdapter

        viewModel.courseList.observe(viewLifecycleOwner, Observer { courses ->
            hobbyCourseAdapter.submitList(courses)
            hobbyCourseAdapter.notifyDataSetChanged()
        })

        binding.placeButton.setOnClickListener {
            it.findNavController().navigate(
                HobbyCourseFragmentDirections.actionHobbyCourseFragmentToHobbyPlaceFragment(
                    sportName
                )
            )
        }

        binding.applianceButton.setOnClickListener {
            it.findNavController().navigate(
                HobbyCourseFragmentDirections.actionHobbyCourseFragmentToHobbyAppliaceFragment(
                    sportName,
                    9999
                )
            )
        }

        viewModel.navigateToYoutube.observe(
            viewLifecycleOwner,
            Observer {
                if (null != it) {
                    this.findNavController().navigate(
                        HobbyCourseFragmentDirections.actionHobbyCourseFragmentToYouTubePlayerFragment(
                            it
                        )
                    )
                    viewModel.onYoutubeNavigated()
                }
            }
        )
        return binding.root
    }
}