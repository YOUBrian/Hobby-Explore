package com.example.hobbyexplore.hobbycourse

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class HobbyCourseFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyCourseFragment()
    }

    private lateinit var viewModel: HobbyCourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hobby_course, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyCourseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}