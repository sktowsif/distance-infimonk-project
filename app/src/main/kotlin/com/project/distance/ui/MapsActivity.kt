package com.project.distance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.project.distance.R
import com.project.distance.data.Coordinate
import com.project.distance.data.Distance
import com.project.distance.data.MapViewModel
import com.project.distance.data.Outcome
import com.project.distance.ext.gone
import com.project.distance.ext.show
import com.project.distance.utils.Meta.STATUS_SUCCESS
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var loadingSnackBar: Snackbar
    private lateinit var mMap: GoogleMap

    // To keep track that api call has been completed,
    // and we don't need to get data from server anymore.
    private var isDataAvailable: Boolean = false

    private val mapViewModel by viewModel<MapViewModel>()

    private val coordinatesObserver = Observer<Outcome<Coordinate.Response>> {
        when (it) {
            is Outcome.Progress -> if (it.loading) loadingSnackBar.show() else loadingSnackBar.dismiss()
            is Outcome.Failure -> toast(R.string.err_something_wrong)
            is Outcome.Success -> {
                if (it.data.statusCode == STATUS_SUCCESS) {
                    isDataAvailable = true
                    addLocationMarker(it.data.coordinates)
                } else toast(R.string.err_something_wrong)
            }
        }
    }

    private val postDistanceObserver = Observer<Outcome<Distance.Response>> {
        when (it) {
            is Outcome.Failure -> toast(R.string.err_distance_post_failure)
            is Outcome.Success -> {
                // Show valid message when distance is post to server
                if (it.data.statusCode == STATUS_SUCCESS) toast(R.string.msg_distance_post_success)
                else toast(R.string.err_distance_post_failure)
            }
        }
    }

    private val shortestDistanceObserver = Observer<Outcome<Float>> {
        if (it is Outcome.Success) {
            toast(getString(R.string.temp_msg_shortest_distance, it.data))
            mapViewModel.addDistance(it.data)
        }
    }

    private val internetConnectivityObserver = Observer<Boolean> { isConnected ->
        if (isConnected) {
            lblMessage.gone()
            if (!isDataAvailable) mapViewModel.fetchCoordinates()
        } else {
            lblMessage.show()
            lblMessage.setText(R.string.err_no_internet)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        loadingSnackBar = Snackbar.make(
            contentView!!,
            R.string.msg_loading_map,
            Snackbar.LENGTH_INDEFINITE
        )

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel.postDistanceResponse().observe(this, postDistanceObserver)
        mapViewModel.getShortestDistance().observe(this, shortestDistanceObserver)
        mapViewModel.connectivityLiveData.observe(this, internetConnectivityObserver)
        mapViewModel.geofenceData.observe(this, coordinatesObserver)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Map ready to use, fetch the location details from server
        mapViewModel.fetchCoordinates()
    }

    private fun addLocationMarker(coordinates: List<Coordinate>) {
        val areaBound = LatLngBounds.builder()
        coordinates.forEach {
            // Create and add marker for each location coordinates
            val latLng = LatLng(it.lat, it.lng)
            mMap.addMarker(MarkerOptions().position(latLng))

            // We add the coordinates to create a map camera and zoom settings
            areaBound.include(latLng)
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(areaBound.build(), 100))

        // Calculate the shortest distance between all locations
        mapViewModel.calculateShortestDistance(coordinates)
    }
}
