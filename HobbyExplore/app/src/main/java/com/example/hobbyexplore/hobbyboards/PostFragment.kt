package com.example.hobbyexplore.hobbyboards

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentPostBinding
import com.example.hobbyexplore.detail.DetailFragmentArgs

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater)
        val viewModel: PostViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        val content = binding.userContentInput.text
        val imageUrl =  PostFragmentArgs.fromBundle(requireArguments()).imageUrl


        binding.cameraButton.setOnClickListener {
            it.findNavController().navigate(PostFragmentDirections.actionPostFragmentToCameraFragment())
        }

        binding.publishButton.setOnClickListener {
            val rating = binding.ratingBar.rating
            it.findNavController().navigate(PostFragmentDirections.actionPostFragmentToHobbyBoardsFragment())
            viewModel.postMessageData(content.toString(),rating,imageUrl)
            Log.i("getrating", "star: $rating")
        }

        binding.ratingBar.setOnRatingBarChangeListener{ _, rating, _ ->
//            Log.i("getrating", "star: $rating")
//            viewModel.postMessageData(content.toString(),rating.toFloat(),imageUrl.toString())
        }
        return binding.root
    }

}