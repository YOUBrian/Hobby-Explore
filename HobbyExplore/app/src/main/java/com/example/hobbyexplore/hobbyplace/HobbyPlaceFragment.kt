package com.example.hobbyexplore.hobbyplace

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.databinding.FragmentHobbyPlaceBinding
import com.example.hobbyexplore.hobbycategory.HobbyCategoryFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseAdapter
import com.example.hobbyexplore.hobbycourse.HobbyCourseFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseViewModel

class HobbyPlaceFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyPlaceViewModel = ViewModelProvider(this).get(HobbyPlaceViewModel::class.java)
        val binding = FragmentHobbyPlaceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val hobbyPlaceAdapter = HobbyPlaceAdapter(HobbyPlaceAdapter.OnClickListener {
            viewModel.navigateToMap(it)
        })

        binding.hobbyPlaceRecyclerView.adapter = hobbyPlaceAdapter

        viewModel.placeList.observe(viewLifecycleOwner, Observer { places ->
            hobbyPlaceAdapter.submitList(places)
            hobbyPlaceAdapter.notifyDataSetChanged()
        })

//        binding.placeButton.setOnClickListener {
//            it.findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToMapsFragment())
//            Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
//        }

        viewModel.navigateToMap.observe(
            viewLifecycleOwner,
            Observer {
                if (null != it) {
                    this.findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToMapsFragment(it))
                    viewModel.onMapNavigated()
                }
            }
        )
        return binding.root
    }


}