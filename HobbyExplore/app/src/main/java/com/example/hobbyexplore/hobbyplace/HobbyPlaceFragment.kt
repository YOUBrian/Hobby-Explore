package com.example.hobbyexplore.hobbyplace

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class HobbyPlaceFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyPlaceFragment()
    }

    private lateinit var viewModel: HobbyPlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hobby_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyPlaceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}