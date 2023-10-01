package com.example.hobbyexplore.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentDetailBinding
import com.example.hobbyexplore.hobbycategory.HobbyCategoryViewModel

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val application = requireNotNull(activity).application
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val introduce = DetailFragmentArgs.fromBundle(requireArguments()).selectProduct

        val viewModelFactory = DetailViewModelFactory(introduce)

        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(DetailViewModel::class.java)
        binding.introduce = viewModel

        binding.detailAppliance.setOnClickListener {
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyAppliaceFragment(-1))
        }

        binding.detailCourse.setOnClickListener {
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyCourseFragment())
        }

        binding.detailPlace.setOnClickListener {
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyPlaceFragment())
        }

        return binding.root
    }


}