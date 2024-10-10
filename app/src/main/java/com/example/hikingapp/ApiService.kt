package com.example.hikingapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("place/nearbysearch/json")
    fun getHikingPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String
    ): Call<HikingPlacesResponse>

    @GET("geocode/json")
    fun getGeocoding(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Call<GeocodingResponse>

    @GET("place/details/json")
    fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Call<PlaceDetailsResponse>
}
