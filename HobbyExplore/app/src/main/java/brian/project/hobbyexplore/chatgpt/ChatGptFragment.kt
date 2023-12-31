package brian.project.hobbyexplore.chatgpt

import android.annotation.SuppressLint
import android.content.Context
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
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.data.Introduce
import brian.project.hobbyexplore.data.gpt.ChatGPTMessage
import brian.project.hobbyexplore.databinding.FragmentSystemRecommendsHobbyBinding

class ChatGptFragment : Fragment() {
    private var currentIntroduce: Introduce? = null

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
        binding.orangeView.visibility = View.GONE
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.detailImageCardView.visibility = View.GONE
        binding.detailContentCardView.visibility = View.GONE
        binding.loadingAnimation.playAnimation()

        val typeString = arguments?.getString("typeString") ?: return null

        viewModel.introduceList.observe(viewLifecycleOwner, Observer { introduce ->
            introduce?.let {
                binding.loadingAnimation.pauseAnimation()
                binding.loadingAnimation.visibility = View.GONE
                binding.selectButton.visibility = View.VISIBLE
                binding.changeButton.visibility = View.VISIBLE
                binding.orangeView.visibility = View.VISIBLE
                binding.detailImageCardView.visibility = View.VISIBLE
                binding.detailContentCardView.visibility = View.VISIBLE

                binding.detailTitle.text = introduce.title
                binding.detailContent.text = introduce.content
                Glide.with(this@ChatGptFragment)
                    .load(introduce.image)
                    .into(binding.detailImage)
                currentIntroduce = introduce
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
            currentIntroduce?.let { introduce ->

                val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {
                    this?.putString("Selected_Hobby_Title", introduce.title)
                    this?.apply()
                    Log.i("cccccccccc", "$typeString")
                    it.findNavController().navigate(ChatGptFragmentDirections.actionChatGptFragmentToHobbyApplianceFragment(introduce.name, 9999))
                }
            }

        }


        return binding.root
    }


}
