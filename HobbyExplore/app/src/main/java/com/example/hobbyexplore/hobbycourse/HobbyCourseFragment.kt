package com.example.hobbyexplore.hobbycourse

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentDetailBinding
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.detail.DetailFragmentArgs
import com.example.hobbyexplore.detail.DetailFragmentDirections
import com.example.hobbyexplore.detail.DetailViewModel
import com.example.hobbyexplore.detail.DetailViewModelFactory
import com.example.hobbyexplore.hobbycategory.HobbyCategoryViewModel

class HobbyCourseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHobbyCourseBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val viewModel: HobbyCourseViewModel = ViewModelProvider(this).get(HobbyCourseViewModel::class.java)

        binding.courseButton.setOnClickListener {
            it.findNavController().navigate(HobbyCourseFragmentDirections.actionHobbyCourseFragmentToYouTubePlayerFragment())
        }
        return binding.root
    }


}