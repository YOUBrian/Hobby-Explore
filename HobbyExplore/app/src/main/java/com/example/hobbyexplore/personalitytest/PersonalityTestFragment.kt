package com.example.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.databinding.FragmentPersonalityTestBinding
import com.example.hobbyexplore.detail.DetailFragmentDirections

class PersonalityTestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPersonalityTestBinding.inflate(inflater)
        binding.yesButton.setOnClickListener {
            it.findNavController().navigate(PersonalityTestFragmentDirections.actionPersonalityTestFragmentToWhetherTakeMbtiTest())
        }

        binding.noButton.setOnClickListener {
            it.findNavController().navigate(PersonalityTestFragmentDirections.actionPersonalityTestFragmentToHobbyCategoryFragment())
        }

        return binding.root
    }
}