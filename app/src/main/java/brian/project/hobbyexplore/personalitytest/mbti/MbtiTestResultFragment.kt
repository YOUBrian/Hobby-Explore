package brian.project.hobbyexplore.personalitytest.mbti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentMbtiTestResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MbtiTestResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMbtiTestResultBinding.inflate(inflater, container, false)

        val typeString = arguments?.getString("typeString") ?: return null
        Log.i("MBTI_TYPE", typeString)

        // Find the corresponding Personality based on typeString
        val matchedPersonality = personalities.find { it.type == typeString }
        Log.i("MBTI_TYPE", matchedPersonality.toString())

        if (matchedPersonality != null) {
            binding.mbtiTestResultImage.setImageResource(matchedPersonality.image)
            binding.mbtiTestResultEn.text = matchedPersonality.type
            binding.mbtiTestResultTw.text = matchedPersonality.name
            binding.mbtiTestResultContent.text = matchedPersonality.description
        }

        // Save MBTI result to Firestore using merge to avoid overwriting other fields
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)

            val data = mapOf("mbtiResult" to typeString)
            userDoc.set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.i("MbtiTestResultFragment", "MBTI saved: $typeString")
                }
                .addOnFailureListener { e ->
                    Log.e("MbtiTestResultFragment", "Failed to save MBTI", e)
                }
        }

        binding.systemRecommendButton.setOnClickListener {
            it.findNavController().navigate(
                MbtiTestResultFragmentDirections
                    .actionMbtiTestResultFragmentToChatGptFragment(typeString)
            )
        }

        return binding.root
    }
}
