package com.example.hobbyexplore.personalitytest.mbti

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentMbtiTestResultBinding

class MbtiTestResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMbtiTestResultBinding.inflate(inflater)

        binding.systemRecommendButton.setOnClickListener {
            it.findNavController().navigate(MbtiTestResultFragmentDirections.actionMbtiTestResultFragmentToSystemRecommendsHobbyFragment())
        }
        return binding.root
    }

}