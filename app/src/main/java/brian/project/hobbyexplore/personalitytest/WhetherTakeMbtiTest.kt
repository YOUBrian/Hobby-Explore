package brian.project.hobbyexplore.personalitytest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WhetherTakeMbtiTest : Fragment() {

    private val mbtiTypes = listOf(
        "INTJ", "INTP", "ENTJ", "ENTP", "INFJ", "INFP", "ENFJ", "ENFP",
        "ISTJ", "ISFJ", "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWhetherTakeMbtiTestBinding.inflate(inflater)

        binding.enterMbtiResult.visibility = View.GONE
        binding.mbtiInput.visibility = View.GONE
        binding.systemRecommendButton.visibility = View.GONE

        // Show manual input when choosing "No"
        binding.noButton.setOnClickListener {
            binding.enterMbtiResult.visibility = View.VISIBLE
            binding.mbtiInput.visibility = View.VISIBLE
            binding.systemRecommendButton.visibility = View.VISIBLE
        }

        // Save MBTI when user inputs manually
        binding.systemRecommendButton.setOnClickListener { button ->
            val userInput = binding.mbtiInput.text.toString().trim().uppercase()
            if (userInput in mbtiTypes) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("mbtiResult", userInput)
                        .addOnSuccessListener {
                            Log.i("WhetherTakeMbtiTest", "MBTI saved to Firestore: $userInput")
                            button.findNavController().navigate(
                                WhetherTakeMbtiTestDirections
                                    .actionWhetherTakeMbtiTestToChatGptFragment(userInput)
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e("WhetherTakeMbtiTest", "Failed to save MBTI", e)
                            // Keep original Toast
                            Toast.makeText(context, R.string.non_mbti_personality, Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, R.string.non_mbti_personality, Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to test
        binding.mbtiYesButton.setOnClickListener {
            it.findNavController().navigate(
                WhetherTakeMbtiTestDirections.actionWhetherTakeMbtiTestToMbtiTestFragment()
            )
        }

        return binding.root
    }
}
