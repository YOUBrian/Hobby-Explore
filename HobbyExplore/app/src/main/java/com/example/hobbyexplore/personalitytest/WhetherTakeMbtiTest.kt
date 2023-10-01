package com.example.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentPersonalityTestBinding
import com.example.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding


class WhetherTakeMbtiTest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWhetherTakeMbtiTestBinding.inflate(inflater)
        binding.enterMbtiResult.visibility = View.GONE
        binding.mbtiInput.visibility = View.GONE
        binding.systemRecommendButton.visibility = View.GONE

        binding.noButton.setOnClickListener {
            binding.enterMbtiResult.visibility = View.VISIBLE
            binding.mbtiInput.visibility = View.VISIBLE
            binding.systemRecommendButton.visibility = View.VISIBLE
        }
        binding.systemRecommendButton.setOnClickListener {
            it.findNavController().navigate(WhetherTakeMbtiTestDirections.actionWhetherTakeMbtiTestToChatGptFragment(""))
        }
        binding.mbtiYesButton.setOnClickListener {
            it.findNavController().navigate(WhetherTakeMbtiTestDirections.actionWhetherTakeMbtiTestToMbtiTestFragment())
        }
        return binding.root
    }
}