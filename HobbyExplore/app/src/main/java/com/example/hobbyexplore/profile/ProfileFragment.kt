package com.example.hobbyexplore.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.hobbyexplore.chatgpt.ChatGptViewModel
import com.example.hobbyexplore.databinding.FragmentProfileBinding
import com.example.hobbyexplore.hobbycourse.HobbyCourseViewModel

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: ProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val binding = FragmentProfileBinding.inflate(inflater)
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        binding.photo = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mbtiResult = sharedPref?.getString("MBTI_Result", "N/A")
        val selectedHobbyTitle = sharedPref?.getString("Selected_Hobby_Title", "N/A")  // 使用"N/A"作為默認值

        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userImage = logInSharedPref?.getString("photoUrl", "")
        val userName = logInSharedPref?.getString("displayName", "N/A")

        viewModel.getUserPhoto(userImage.toString())

        binding.nickname.text = "$userName"

        binding.selectHobby.text = selectedHobbyTitle
        binding.mbtiResult.text = "$mbtiResult"
        binding.selectHobby.text = "$selectedHobbyTitle"

        binding.mbtiButton.setOnClickListener {
            it.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWhetherTakeMbtiTest())
        }

        return binding.root
    }
}
