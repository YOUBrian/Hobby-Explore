package brian.project.hobbyexplore.chatgpt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
        val binding = FragmentSystemRecommendsHobbyBinding.inflate(inflater, container, false)

        // Initial UI state
        binding.selectButton.visibility = View.GONE
        binding.changeButton.visibility = View.GONE
        binding.orangeView.visibility = View.GONE
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.detailImageCardView.visibility = View.GONE
        binding.detailContentCardView.visibility = View.GONE
        binding.loadingAnimation.playAnimation()

        // Get MBTI type from arguments
        val typeString = arguments?.getString("typeString") ?: return null

        // Observe introduce data
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

        // Call GPT API with MBTI type
        viewModel.addToChat(typeString, ChatGPTMessage.SENT_BY_ME, viewModel.getCurrentTimestamp())
        viewModel.callApi(typeString)

        // Change hobby suggestion
        binding.changeButton.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
            binding.loadingAnimation.visibility = View.VISIBLE
            binding.loadingAnimation.playAnimation()

            val randomSport = viewModel.getRandomSportWithoutRepetition()
            viewModel.getHobbyData(randomSport)
        }

        // Select hobby and save to Firestore
        binding.selectButton.setOnClickListener { button ->
            currentIntroduce?.let { introduce ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)

                    val data = mapOf("selectedHobby" to introduce.title)
                    userDoc.set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.i("ChatGptFragment", "Selected hobby saved: ${introduce.title}")
                            button.findNavController().navigate(
                                ChatGptFragmentDirections.actionChatGptFragmentToHobbyApplianceFragment(
                                    introduce.name,
                                    9999
                                )
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatGptFragment", "Failed to save selected hobby", e)
                        }
                }
            }
        }

        return binding.root
    }
}
