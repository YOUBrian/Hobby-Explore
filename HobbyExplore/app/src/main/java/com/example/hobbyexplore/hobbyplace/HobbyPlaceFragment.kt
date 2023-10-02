package com.example.hobbyexplore.hobbyplace

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentHobbyCourseBinding
import com.example.hobbyexplore.databinding.FragmentHobbyPlaceBinding
import com.example.hobbyexplore.hobbyappliance.HobbyApplianceFragmentArgs
import com.example.hobbyexplore.hobbyappliance.HobbyApplianceFragmentDirections
import com.example.hobbyexplore.hobbycategory.HobbyCategoryFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseAdapter
import com.example.hobbyexplore.hobbycourse.HobbyCourseFragmentDirections
import com.example.hobbyexplore.hobbycourse.HobbyCourseViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HobbyPlaceFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: HobbyPlaceViewModel = ViewModelProvider(this).get(HobbyPlaceViewModel::class.java)
        val sportName = HobbyPlaceFragmentArgs.fromBundle(requireArguments()).sportName
        val binding = FragmentHobbyPlaceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getPlaceData(sportName)

        val hobbyPlaceAdapter = HobbyPlaceAdapter(HobbyPlaceAdapter.OnClickListener {
            viewModel.navigateToMap(it)
        })

        binding.hobbyPlaceRecyclerView.adapter = hobbyPlaceAdapter

        viewModel.placeList.observe(viewLifecycleOwner, Observer { places ->
            hobbyPlaceAdapter.submitList(places)
            hobbyPlaceAdapter.notifyDataSetChanged()
        })

//        binding.placeButton.setOnClickListener {
//            it.findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToMapsFragment())
//            Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
//        }

        binding.courseButton.setOnClickListener {
            it.findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToHobbyCourseFragment(sportName))
        }

        binding.applianceButton.setOnClickListener {
            it.findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToHobbyAppliaceFragment(sportName, 9999))
        }

        viewModel.navigateToMap.observe(
            viewLifecycleOwner,
            Observer {
                if (null != it) {
                    findNavController().navigate(HobbyPlaceFragmentDirections.actionHobbyPlaceFragmentToMapsFragment(latitude.toFloat(), longitude.toFloat()))
                    viewModel.onMapNavigated()
                }
            }
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermissionAndGetLocation()

        return binding.root
    }

    private fun checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 請求定位權限
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            // 已有權限，取得位置
            getLocation()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // 使用 location.latitude 和 location.longitude 可取得位置
                    latitude = location?.latitude ?: 0.0
                    longitude = location?.longitude ?: 0.0
                    Toast.makeText(requireContext(), "位置: ${location?.latitude}, ${location?.longitude}", Toast.LENGTH_LONG).show()
                }
        } else {
            // Handle case where permission is not granted
            Toast.makeText(requireContext(), "定位權限未被授予", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(requireContext(), "定位權限被拒絕", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val LOCATION_REQUEST_CODE = 1234
    }

}