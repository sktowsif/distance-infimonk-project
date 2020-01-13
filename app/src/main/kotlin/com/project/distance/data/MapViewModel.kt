package com.project.distance.data

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.project.distance.ext.*
import com.project.distance.utils.ConnectivityLiveData
import com.project.distance.utils.DistanceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MapViewModel(
    application: Application,
    private val repository: MapDataSource
) : AndroidViewModel(application), AnkoLogger {

    // To observer internet connectivity
    val connectivityLiveData = ConnectivityLiveData(application)

    private val _fetchGeofence = MutableLiveData<Boolean>()

    val geofenceData = _fetchGeofence.switchMap {
        liveData<Outcome<Coordinate.Response>>(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emitLoading()
            try {
                val result = repository.getLocations()
                info { result }
                emitSuccess(result)
            } catch (ex: Exception) {
                ex.printStackTrace()
                emitFailure(ex)
            }
        }
    }

    fun fetchCoordinates() {
        _fetchGeofence.value = true
    }

    private val _postDistanceOutcome = MutableLiveData<Outcome<Distance.Response>>()

    fun postDistanceResponse() = _postDistanceOutcome

    fun addDistance(distanceInKm: Float) {
        viewModelScope.launch {
            _postDistanceOutcome.loading()
            try {
                val result = withContext(Dispatchers.IO) {
                    val distance = Distance(
                        distance = distanceInKm,
                        userId = generateRandomUserId()
                    )
                    repository.addDistance(distance)
                }
                _postDistanceOutcome.success(result)
            } catch (ex: Exception) {
                ex.printStackTrace()
                _postDistanceOutcome.failure(ex)
            }
        }
    }

    /**
     * Generate two digit random number.
     */
    private fun generateRandomUserId() = (10..99).random()

    private val _shortestDistance = MutableLiveData<Outcome<Float>>()

    fun getShortestDistance() = _shortestDistance

    /**
     * Calculate the shortest distance between all the coordinates in a list.
     */
    fun calculateShortestDistance(coordinates: List<Coordinate>) {
        viewModelScope.launch {
            _shortestDistance.loading()
            try {
                val result = withContext(Dispatchers.Default) {
                    // Get all the possible combinations between three locations
                    val combinations = coordinates.permute()
                    // Compute the distance between each one of them
                    val distances = combinations.map {
                        DistanceUtil.calculateDistance(
                            LatLng(it.first.lat, it.first.lng),
                            LatLng(it.second.lat, it.second.lng)
                        )
                    }.toList()

                    DistanceUtil.toKilometer(distances.max()!!)
                }
                _shortestDistance.success(result)
            } catch (ex: Exception) {
                ex.printStackTrace()
                _shortestDistance.failure(ex)
            }
        }
    }

}