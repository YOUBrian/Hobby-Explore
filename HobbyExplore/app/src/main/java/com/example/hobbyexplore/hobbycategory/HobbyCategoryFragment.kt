package com.example.hobbyexplore.hobbycategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class HobbyCategoryFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyCategoryFragment()
    }

    private lateinit var viewModel: HobbyCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hobby_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyCategoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}