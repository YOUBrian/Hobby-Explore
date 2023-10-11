package com.example.hobbyexplore.hobbyplace

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hobbyexplore.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0
    private var placeLatitude: Double = 0.0
    private var placeLongitude: Double = 0.0
    private var placeTitle: String = ""


    private val callback = OnMapReadyCallback { googleMap ->
        val userLocation = LatLng(userLatitude, userLongitude)
        val placeLocation = LatLng(placeLatitude, placeLongitude)
        val zoomLevel = 18f
        val userMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_blue)
        googleMap.addMarker(MarkerOptions().position(userLocation).title("您的位置").icon(userMarker))
        googleMap.addMarker(MarkerOptions().position(placeLocation).title(placeTitle).icon(userMarker))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, zoomLevel))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userLatitude = MapsFragmentArgs.fromBundle(requireArguments()).userLatitude.toDouble()
        userLongitude = MapsFragmentArgs.fromBundle(requireArguments()).userLongitude.toDouble()
        placeLatitude = MapsFragmentArgs.fromBundle(requireArguments()).placeLatitude.toDouble()
        placeLongitude = MapsFragmentArgs.fromBundle(requireArguments()).placeLongitude.toDouble()
        placeTitle = MapsFragmentArgs.fromBundle(requireArguments()).placeTitle
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}

