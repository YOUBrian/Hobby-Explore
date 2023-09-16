package com.example.hobbyexplore.hobbycategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyCategoryBinding

class HobbyCategoryFragment : Fragment() {

    companion object {
        fun newInstance() = HobbyCategoryFragment()
    }

    private lateinit var viewModel: HobbyCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHobbyCategoryBinding.inflate(inflater)
        binding.button.setOnClickListener {
            it.findNavController().navigate(HobbyCategoryFragmentDirections.actionHobbyCategoryFragmentToYouTubePlayerFragment())
            Log.i("abcde","aaaaaa")
        }

        return inflater.inflate(R.layout.fragment_hobby_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HobbyCategoryViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.getData()
    }

}