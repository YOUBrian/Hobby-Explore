package com.example.hobbyexplore.hobbyboards

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.hobbyexplore.databinding.FragmentPostBinding
import com.example.hobbyexplore.timestampToDateString

class PostFragment : Fragment() {
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).toTypedArray()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater)
        val viewModel: PostViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        val content = binding.userContentInput.text.toString()
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
            val content = binding.userContentInput.text.toString()
            val rating = binding.ratingBar.rating
            it.findNavController().navigate(PostFragmentDirections.actionPostFragmentToHobbyBoardsFragment())
            viewModel.postMessageData(content, rating, imageUri)
            Log.i("getImageUri", "getImageUri: $imageUri")
            Log.i("getimageStringToUri", "imageStringToUri: $imageStringToUri")
            Log.i("getrating", "star: $rating")
        }

        binding.ratingBar.setOnRatingBarChangeListener{ _, rating, _ ->
//            Log.i("getrating", "star: $rating")
//            viewModel.postMessageData(content.toString(),rating.toFloat(),imageUrl.toString())
        }

        val args = PostFragmentArgs.fromBundle(requireArguments())
        val contentFromArgs = args.content
        val imageUrlFromArgs = args.imageUri

        binding.userContentInput.setText(contentFromArgs)
        Glide.with(this).load(imageUrlFromArgs).into(binding.postImage)


        return binding.root
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showPermissionDeniedMessage()
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check and request permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // ... (您原本的其他代码)
    }
    private fun allPermissionsGranted() = CameraFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
    }
}