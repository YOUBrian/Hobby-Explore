package com.example.hobbyexplore.place

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentPlaceBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class PlaceFragment : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlaceBinding.inflate(inflater)
        val mapFragment = binding.placeGoogleMap
        mapFragment
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

}