package brian.project.hobbyexplore.personalitytest.mbti

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentMbtiTestResultBinding

class MbtiTestResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMbtiTestResultBinding.inflate(inflater)

        val typeString = arguments?.getString("typeString") ?: return null
        Log.i("MBTI_TYPE", "$typeString")
        // Find the corresponding Personality based on typeString
        val matchedPersonality = personalities.find { it.type == typeString }
        Log.i("MBTI_TYPE", "$matchedPersonality")

        binding.mbtiTestResultImage.setImageResource(matchedPersonality!!.image)
        binding.mbtiTestResultEn.text = matchedPersonality.type
        binding.mbtiTestResultTw.text = matchedPersonality.name
        binding.mbtiTestResultContent.text = matchedPersonality.description

        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref?.edit()) {
            this?.putString("MBTI_Result", typeString)
            this?.apply()
        }


        binding.systemRecommendButton.setOnClickListener {
            it.findNavController().navigate(
                MbtiTestResultFragmentDirections.actionMbtiTestResultFragmentToChatGptFragment(
                    typeString
                )
            )
        }
        return binding.root
    }


}