package com.project.distance.data

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("status") val statusCode: Int? = null,
    @SerializedName("response") val status: String? = null,
    @SerializedName("msg") val message: String? = null
)

data class Coordinate(
    @SerializedName("geofenceId") val geofenceId: Int,
    @SerializedName("latitude") val lat: Double,
    @SerializedName("longitude") val lng: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
) {
    data class Response(@SerializedName("data") val coordinates: List<Coordinate>) : BaseResponse()
}

data class Distance(
    @SerializedName("distanceId") val id: Int? = null,
    @SerializedName("distance") val distance: Float,
    @SerializedName("userId") val userId: Int,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    data class Response(@SerializedName("data") val distance: Distance) : BaseResponse()
}

sealed class Outcome<T> {

    data class Progress<T>(var loading: Boolean) : Outcome<T>()
    data class Success<T>(var data: T) : Outcome<T>()
    data class Failure<T>(val e: Throwable) : Outcome<T>()

    companion object {

        fun <T> loading(isLoading: Boolean): Outcome<T> =
            Progress(isLoading)

        fun <T> success(data: T): Outcome<T> =
            Success(data)

        fun <T> failure(e: Throwable): Outcome<T> =
            Failure(e)
    }
}