package com.project.distance.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LocationAPI {

    @GET("geofence")
    suspend fun getLocations(): Coordinate.Response

    @POST("geofence")
    suspend fun addDistance(@Body distance: Distance): Distance.Response
}

interface MapDataSource {

    /**
     * Get location coordinates from server.
     */
    suspend fun getLocations(): Coordinate.Response

    /**
     * Post location coordinates to server.
     */
    suspend fun addDistance(distance: Distance): Distance.Response
}

class MapRepository(private val apiService: LocationAPI) : MapDataSource {

    override suspend fun getLocations() = apiService.getLocations()

    override suspend fun addDistance(distance: Distance) = apiService.addDistance(distance)
}