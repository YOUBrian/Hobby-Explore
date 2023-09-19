package com.example.hobbyexplore.hobbyappliance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class HobbyAppliaceFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyAppliaceFragment()
    }

    private lateinit var viewModel: HobbyAppliaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hobby_appliace, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyAppliaceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}