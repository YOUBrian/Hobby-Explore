package brian.project.hobbyexplore.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentDetailBinding
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryViewModel

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val application = requireNotNull(activity).application
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val introduce = DetailFragmentArgs.fromBundle(requireArguments()).selectProduct

        val viewModelFactory = DetailViewModelFactory(introduce)

        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(DetailViewModel::class.java)
        binding.introduce = viewModel
        Log.i("viewModel", "viewModelData:${introduce}")
        binding.detailAppliance.setOnClickListener {
            Log.i("asfddasf", "${introduce.name}")
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyAppliaceFragment(introduce.name,9999))
        }

        binding.detailCourse.setOnClickListener {
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyCourseFragment(introduce.name))
        }

        binding.detailPlace.setOnClickListener {
            it.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHobbyPlaceFragment(introduce.name))
        }

        return binding.root
    }


}