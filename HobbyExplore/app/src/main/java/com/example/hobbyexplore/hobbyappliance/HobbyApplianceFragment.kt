package com.example.hobbyexplore.hobbyappliance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyApplianceBinding
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.hobbycategory.HobbyCategoryAdapter
import com.example.hobbyexplore.hobbycategory.HobbyCategoryFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseViewModel

class HobbyApplianceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyApplianceViewModel = ViewModelProvider(this).get(HobbyApplianceViewModel::class.java)

        val binding = FragmentHobbyApplianceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val hobbyApplianceAdapter = HobbyApplianceAdapter(HobbyApplianceAdapter.OnClickListener {
//            viewModel.navigateToDetail(it)
        })

        binding.hobbyApplianceRecyclerView.adapter = hobbyApplianceAdapter

        viewModel.applianceList.observe(viewLifecycleOwner, Observer { appliances->
            hobbyApplianceAdapter.submitList(appliances)
            hobbyApplianceAdapter.notifyDataSetChanged()
        })

//        binding.button.setOnClickListener {
//            it.findNavController().navigate(HobbyCategoryFragmentDirections.actionHobbyCategoryFragmentToYouTubePlayerFragment())
//            Log.i("abcde","aaaaaa")
//        }

//        viewModel.navigateToDetail.observe(
//            viewLifecycleOwner,
//            Observer {
//                if (null != it) {
//                    this.findNavController().navigate(HobbyCategoryFragmentDirections.actionHobbyCategoryFragmentToDetailFragment(it))
//                    viewModel.onDetailNavigated()
//                }
//            }
//        )
        return binding.root
    }
}