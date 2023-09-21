package com.example.hobbyexplore.personalitytest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class PersonalityTestFragment : Fragment() {

    companion object {
        fun newInstance() = PersonalityTestFragment()
    }

    private lateinit var viewModel: PersonalityTestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personality_test, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PersonalityTestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}