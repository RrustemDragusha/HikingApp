package com.example.hikingapp

data class HikingPlacesResponse(
    val results: List<HikingPlace>
)

data class HikingPlace(
    val name: String,
    val geometry: Geometry,
    val place_id: String
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)


