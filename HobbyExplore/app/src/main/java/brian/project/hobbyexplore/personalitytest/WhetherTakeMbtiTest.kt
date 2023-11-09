package brian.project.hobbyexplore.personalitytest

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentPersonalityTestBinding
import brian.project.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding


class WhetherTakeMbtiTest : Fragment() {
    private val mbtiTypes = listOf("INTJ", "INTP", "ENTJ", "ENTP", "INFJ", "INFP", "ENFJ", "ENFP",
        "ISTJ", "ISFJ", "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWhetherTakeMbtiTestBinding.inflate(inflater)
        binding.enterMbtiResult.visibility = View.GONE
        binding.mbtiInput.visibility = View.GONE
        binding.systemRecommendButton.visibility = View.GONE

        binding.noButton.setOnClickListener {
            binding.enterMbtiResult.visibility = View.VISIBLE
            binding.mbtiInput.visibility = View.VISIBLE
            binding.systemRecommendButton.visibility = View.VISIBLE
        }
        binding.systemRecommendButton.setOnClickListener {
            val userInput = binding.mbtiInput.text.toString().trim().toUpperCase()
            if (userInput in mbtiTypes) {

                val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {
                    this?.putString("MBTI_Result", userInput)
                    this?.apply()
                }
                it.findNavController().navigate(WhetherTakeMbtiTestDirections.actionWhetherTakeMbtiTestToChatGptFragment(userInput))
            } else {
                Toast.makeText(context, R.string.non_mbti_personality, Toast.LENGTH_SHORT).show()
            }
        }
        binding.mbtiYesButton.setOnClickListener {
            it.findNavController().navigate(WhetherTakeMbtiTestDirections.actionWhetherTakeMbtiTestToMbtiTestFragment())
        }
        return binding.root
    }
}