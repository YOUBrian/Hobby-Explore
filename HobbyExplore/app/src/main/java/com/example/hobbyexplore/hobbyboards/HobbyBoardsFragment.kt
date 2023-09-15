package com.example.hobbyexplore.hobbyboards

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

class HobbyBoardsFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyBoardsFragment()
    }

    private lateinit var viewModel: HobbyBoardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hobby_boards, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyBoardsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}