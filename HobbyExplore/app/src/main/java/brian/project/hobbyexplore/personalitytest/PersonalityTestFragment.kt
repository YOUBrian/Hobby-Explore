package brian.project.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import brian.project.hobbyexplore.databinding.FragmentPersonalityTestBinding
import brian.project.hobbyexplore.detail.DetailFragmentDirections

class PersonalityTestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPersonalityTestBinding.inflate(inflater)
        binding.yesButton.setOnClickListener {
            it.findNavController().navigate(PersonalityTestFragmentDirections.actionPersonalityTestFragmentToWhetherTakeMbtiTest())
        }

        binding.noButton.setOnClickListener {
            it.findNavController().navigate(PersonalityTestFragmentDirections.actionPersonalityTestFragmentToHobbyCategoryFragment())
        }

        return binding.root
    }
}