package com.example.hobbyexplore.hobbyappliance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyApplianceBinding
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.hobbycourse.HobbyCourseFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseViewModel

class HobbyApplianceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHobbyApplianceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val viewModel: HobbyApplianceViewModel = ViewModelProvider(this).get(HobbyApplianceViewModel::class.java)

        return binding.root
    }


}