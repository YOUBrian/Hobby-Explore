package com.example.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentEnterBudgetBinding
import com.example.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding

class EnterBudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEnterBudgetBinding.inflate(inflater)
        val budget = binding.budgetInput.text.toString()
        binding.systemRecommendApplianceButton.setOnClickListener {
//            val budgetToInt = budget.toInt()
            it.findNavController().navigate(EnterBudgetFragmentDirections.actionEnterBudgetFragmentToHobbyAppliaceFragment(999))
        }
        return binding.root
    }
}