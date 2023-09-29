package com.example.hobbyexplore.personalitytest.mbti

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentMbtiTestResultBinding

class MbtiTestResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMbtiTestResultBinding.inflate(inflater)

        val typeString = arguments?.getString("typeString") ?: return null
        Log.i("MBTI_TYPE", "$typeString")
        // 根據typeString找到對應的Personality
        val matchedPersonality = personalities.find { it.type == typeString }
        Log.i("MBTI_TYPE", "$matchedPersonality")

        binding.mbtiTestResultImage.setImageResource(matchedPersonality!!.image)
        binding.mbtiTestResultEn.text = matchedPersonality.type
        binding.mbtiTestResultTw.text = matchedPersonality.name
        binding.mbtiTestResultContent.text = matchedPersonality.description



        binding.systemRecommendButton.setOnClickListener {
            it.findNavController().navigate(MbtiTestResultFragmentDirections.actionMbtiTestResultFragmentToChatGptFragment(typeString))
        }
        return binding.root
    }


}