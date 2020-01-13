package com.project.distance.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object DistanceUtil {

    /**
     * Computes the approximate distance in meters between two locations.
     */
    fun calculateDistance(origin: LatLng, destination: LatLng): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            origin.latitude,
            origin.longitude,
            destination.latitude,
            destination.longitude,
            result
        )
        return result.first()
    }

    /**
     * Convert distance in meter to kilometer.
     */
    fun toKilometer(distanceInMeters: Float) = distanceInMeters / 1000

}