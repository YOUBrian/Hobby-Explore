package com.example.hobbyexplore.profile

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentProfileBinding.inflate(inflater)


        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val mbtiResult = sharedPref?.getString("MBTI_Result", "N/A")
        val selectedHobbyTitle = sharedPref?.getString("Selected_Hobby_Title", "N/A")  // 使用"N/A"作為默認值

        binding.selectHobby.text = selectedHobbyTitle

        binding.mbtiResult.text = "MBTI人格 : $mbtiResult"
        binding.selectHobby.text = "選擇的興趣 : $selectedHobbyTitle"

        binding.mbtiButton.setOnClickListener {
            it.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWhetherTakeMbtiTest())
        }

        return binding.root
    }
}
