package brian.project.hobbyexplore.hobbycategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import brian.project.hobbyexplore.NavigationDirections
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentHobbyCategoryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class HobbyCategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyCategoryViewModel =
            ViewModelProvider(this).get(HobbyCategoryViewModel::class.java)

        val binding = FragmentHobbyCategoryBinding.inflate(inflater)

        val hobbyCategoryAdapter = HobbyCategoryAdapter(HobbyCategoryAdapter.OnClickListener {
            Log.i("HobbyClick", "Clicked on hobby: ${it}")
            viewModel.navigateToDetail(it)
        })

        binding.hobbyCategoryRecyclerView.adapter = hobbyCategoryAdapter

        viewModel.introduceList.observe(viewLifecycleOwner, Observer { sports ->
            hobbyCategoryAdapter.submitList(sports)
            hobbyCategoryAdapter.notifyDataSetChanged()
        })

//        binding.button.setOnClickListener {
//            it.findNavController().navigate(HobbyCategoryFragmentDirections.actionHobbyCategoryFragmentToYouTubePlayerFragment())
//        }

        viewModel.navigateToDetail.observe(
            viewLifecycleOwner,
            Observer {
                if (null != it) {
                    this.findNavController().navigate(
                        HobbyCategoryFragmentDirections.actionHobbyCategoryFragmentToDetailFragment(
                            it
                        )
                    )
                    viewModel.onDetailNavigated()
                }
            }
        )
        return binding.root
    }
}