package com.example.hobbyexplore.chatgpt

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.gpt.ChatGPTMessage
import com.example.hobbyexplore.databinding.FragmentChatGptBinding

class ChatGptFragment : Fragment() {

    private lateinit var chatGptViewModel: ChatGptViewModel

    private lateinit var viewModel: ChatGptViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatGptBinding.inflate(inflater)

        chatGptViewModel = ViewModelProvider(this)[ChatGptViewModel::class.java]

        val llm = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = llm

        chatGptViewModel.messageList.observe(viewLifecycleOwner){messages ->
            val adapter = ChatGptAdapter(messages)
            binding.recyclerView.adapter = adapter
        }

        binding.sendBtn.setOnClickListener {
            val question = binding.messageEditText.text.toString()
            chatGptViewModel.addToChat(question, ChatGPTMessage.SENT_BY_ME,chatGptViewModel.getCurrentTimestamp())
            binding.messageEditText.setText("")
            chatGptViewModel.callApi(question)
        }


        return binding.root
    }


}