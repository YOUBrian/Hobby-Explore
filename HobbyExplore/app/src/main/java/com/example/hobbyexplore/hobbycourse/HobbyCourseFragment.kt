package com.example.hobbyexplore.hobbycourse

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
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentDetailBinding
import com.example.hobbyexplore.databinding.FragmentHobbyApplianceBinding
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.detail.DetailFragmentArgs
import com.example.hobbyexplore.detail.DetailFragmentDirections
import com.example.hobbyexplore.detail.DetailViewModel
import com.example.hobbyexplore.detail.DetailViewModelFactory
import com.example.hobbyexplore.hobbyappliance.HobbyApplianceAdapter
import com.example.hobbyexplore.hobbyappliance.HobbyApplianceViewModel
import com.example.hobbyexplore.hobbycategory.HobbyCategoryViewModel
import com.example.hobbyexplore.hobbyplace.HobbyPlaceFragmentDirections

class HobbyCourseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyCourseViewModel = ViewModelProvider(this).get(HobbyCourseViewModel::class.java)
        val binding = FragmentHobbyCourseBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner


        val hobbyCourseAdapter = HobbyCourseAdapter(HobbyCourseAdapter.OnClickListener {
            viewModel.navigateToYoutube(it)
        })

        binding.hobbyCourseRecyclerView.adapter = hobbyCourseAdapter

        viewModel.courseList.observe(viewLifecycleOwner, Observer { courses ->
            hobbyCourseAdapter.submitList(courses)
            hobbyCourseAdapter.notifyDataSetChanged()
        })

        viewModel.navigateToYoutube.observe(
            viewLifecycleOwner,
            Observer {
                if (null != it) {
                    this.findNavController().navigate(HobbyCourseFragmentDirections.actionHobbyCourseFragmentToYouTubePlayerFragment(it))
                    viewModel.onYoutubeNavigated()
                }
            }
        )
        return binding.root
    }
}