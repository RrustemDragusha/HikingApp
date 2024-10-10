package com.example.hikingapp


data class GeocodingResponse(
    val results: List<GeocodingResult>
)

data class GeocodingResult(
    val geometry: GeocodingGeometry
)

data class GeocodingGeometry(
    val location: GeocodingLocation
)

data class GeocodingLocation(
    val lat: Double,
    val lng: Double
)
