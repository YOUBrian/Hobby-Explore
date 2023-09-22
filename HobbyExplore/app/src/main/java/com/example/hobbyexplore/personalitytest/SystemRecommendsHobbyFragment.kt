package com.example.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentSystemRecommendsHobbyBinding
import com.example.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding

class SystemRecommendsHobbyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSystemRecommendsHobbyBinding.inflate(inflater)
        binding.selectButton.setOnClickListener {
            it.findNavController()
                .navigate(SystemRecommendsHobbyFragmentDirections.actionSystemRecommendsHobbyFragmentToEnterBudgetFragment())
        }
        return binding.root
    }
}