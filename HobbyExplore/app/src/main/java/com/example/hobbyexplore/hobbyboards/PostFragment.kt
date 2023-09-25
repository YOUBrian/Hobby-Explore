package com.example.hobbyexplore.hobbyboards

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.hobbyexplore.databinding.FragmentPostBinding
import com.example.hobbyexplore.timestampToDateString

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater)
        val viewModel: PostViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        val content = binding.userContentInput.text
        val imageUri =  PostFragmentArgs.fromBundle(requireArguments()).imageUri
        val imageStringToUri = Uri.parse(imageUri)
        viewModel.uploadPhoto.value = imageUri
        viewModel.uploadPhoto.observe(viewLifecycleOwner, Observer { newImageUri ->
            // 在这里更新 binding.imageView 的图片
            Glide.with(this) // 使用 Glide 或其他图片加载库加载图片
                .load(newImageUri) // 使用新的图片 URI 更新 ImageView
                .into(binding.postImage)
        })



        binding.cameraButton.setOnClickListener {
            it.findNavController().navigate(PostFragmentDirections.actionPostFragmentToCameraFragment())
        }

        binding.publishButton.setOnClickListener {
            val rating = binding.ratingBar.rating
            it.findNavController().navigate(PostFragmentDirections.actionPostFragmentToHobbyBoardsFragment())
            viewModel.postMessageData(content.toString(),rating,imageUri)
            Log.i("getImageUri", "getImageUri: $imageUri")
            Log.i("getimageStringToUri", "imageStringToUri: $imageStringToUri")
            Log.i("getrating", "star: $rating")
        }

        binding.ratingBar.setOnRatingBarChangeListener{ _, rating, _ ->
//            Log.i("getrating", "star: $rating")
//            viewModel.postMessageData(content.toString(),rating.toFloat(),imageUrl.toString())
        }
        return binding.root
    }

}