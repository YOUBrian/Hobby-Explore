package com.example.hobbyexplore.personalitytest.mbti

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class MbtiTestResultFragment : Fragment() {

    companion object {
        fun newInstance() = MbtiTestResultFragment()
    }

    private lateinit var viewModel: MbtiTestResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mbti_test_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MbtiTestResultViewModel::class.java)
        // TODO: Use the ViewModel
    }

}