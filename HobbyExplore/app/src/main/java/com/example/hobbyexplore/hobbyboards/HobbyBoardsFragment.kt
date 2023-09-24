package com.example.hobbyexplore.hobbyboards

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyBoardsBinding

class HobbyBoardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHobbyBoardsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        val viewModel: HobbyBoardsViewModel = ViewModelProvider(this).get(HobbyBoardsViewModel::class.java)
        viewModel.refresh()
        val hobbyBoardsAdapter = HobbyBoardsAdapter(HobbyBoardsAdapter.OnClickListener{})

        binding.hobbyBoardsRecyclerView.adapter = hobbyBoardsAdapter

        viewModel.messageList.observe(viewLifecycleOwner, Observer { messages ->
            hobbyBoardsAdapter.submitList(messages)
        })

        binding.layoutSwipeRefreshBoards.setOnRefreshListener {
            viewModel.refresh()
//            viewModel.postMessageData()
        }
        binding.postButton.setOnClickListener {
//            viewModel.postMessageData()
//            viewModel.postApplianceData()
            Toast.makeText(requireContext(), "POST", Toast.LENGTH_SHORT).show()
            it.findNavController().navigate(HobbyBoardsFragmentDirections.actionHobbyBoardsFragmentToPostFragment())
        }

        viewModel.refreshStatus.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    binding.layoutSwipeRefreshBoards.isRefreshing = it
                }
            }
        )

        return binding.root
    }

}