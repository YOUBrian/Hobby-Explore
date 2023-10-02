package com.example.hobbyexplore.chatgpt

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.gpt.ChatGPTMessage
import com.example.hobbyexplore.databinding.FragmentSystemRecommendsHobbyBinding

class ChatGptFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ChatGptViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSystemRecommendsHobbyBinding.inflate(inflater)

        binding.selectButton.visibility = View.GONE
        binding.changeButton.visibility = View.GONE
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.loadingAnimation.playAnimation()

        val typeString = arguments?.getString("typeString") ?: return null

        viewModel.introduceList.observe(viewLifecycleOwner, Observer { introduce ->
            introduce?.let {
                binding.loadingAnimation.pauseAnimation()
                binding.loadingAnimation.visibility = View.GONE
                binding.selectButton.visibility = View.VISIBLE
                binding.changeButton.visibility = View.VISIBLE

                binding.detailTitle.text = introduce.title
                binding.detailContent.text = introduce.content
                Glide.with(this@ChatGptFragment)
                    .load(introduce.image)
                    .into(binding.detailImage)
            }
        })

        viewModel.addToChat(typeString, ChatGPTMessage.SENT_BY_ME, viewModel.getCurrentTimestamp())
        viewModel.callApi(typeString)

        binding.changeButton.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
            binding.loadingAnimation.visibility = View.VISIBLE
            binding.loadingAnimation.playAnimation()

            val randomSport = viewModel.getRandomSportWithoutRepetition()
            viewModel.getHobbyData(randomSport)
        }

        binding.selectButton.setOnClickListener {
            it.findNavController().navigate(ChatGptFragmentDirections.actionChatGptFragmentToEnterBudgetFragment())
        }

        return binding.root
    }
}
