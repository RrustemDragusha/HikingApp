package com.example.hikingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var apiService: ApiService
    private lateinit var editTextLocation: EditText
    private lateinit var editTextRadius: EditText
    private lateinit var buttonSearch: Button
    private lateinit var searchForm: LinearLayout
    private lateinit var imageViewToggleSearch: ImageView
    private lateinit var textViewSearchTitle: TextView

    private var isSearchFormVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializimi i Retrofit per API calls
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Inicializimi i Google Maps
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializimi i elementeve te UI per kerkim
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextRadius = findViewById(R.id.editTextRadius)
        buttonSearch = findViewById(R.id.buttonSearch)
        searchForm = findViewById(R.id.searchForm)
        imageViewToggleSearch = findViewById(R.id.imageViewToggleSearch)
        textViewSearchTitle = findViewById(R.id.textViewSearchTitle)

        buttonSearch.setOnClickListener {
            val location = editTextLocation.text.toString()
            val radiusStr = editTextRadius.text.toString()

            if (location.isEmpty() || radiusStr.isEmpty()) {
                showToast("Please enter a location and radius.")
            } else {
                val radius = radiusStr.toInt() * 1000 // Convert km to meters
                geocodeLocationAndFetchHikingPlaces(location, radius)
                toggleSearchFormVisibility()
            }
        }

        imageViewToggleSearch.setOnClickListener {
            toggleSearchFormVisibility()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    // Gjeokodimi dhe vendosja e markerave ne harte
    private fun geocodeLocationAndFetchHikingPlaces(location: String, radius: Int) {
        val geocodingCall = apiService.getGeocoding(location, "AIzaSyADiW1b8gYiKa-hXF1maGu1fnxw2mKQHus")
        geocodingCall.enqueue(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.firstOrNull()?.geometry?.location?.let { location ->
                        val latLng = LatLng(location.lat, location.lng)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        fetchHikingPlaces("${location.lat},${location.lng}", radius, "park", "hiking")
                    } ?: run {
                        showToast("Location not found.")
                    }
                } else {
                    showToast("Error geocoding location: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                showToast("Geocoding request failed: ${t.message}")
            }
        })
    }

    private fun fetchHikingPlaces(location: String, radius: Int, type: String, keyword: String) {
        val call = apiService.getHikingPlaces(location, radius, type, keyword, "AIzaSyADiW1b8gYiKa-hXF1maGu1fnxw2mKQHus")
        call.enqueue(object : Callback<HikingPlacesResponse> {
            override fun onResponse(call: Call<HikingPlacesResponse>, response: Response<HikingPlacesResponse>) {
                if (response.isSuccessful) {
                    mMap.clear()
                    response.body()?.results?.let { results ->
                        for (result in results) {
                            val latLng = LatLng(result.geometry.location.lat, result.geometry.location.lng)
                            val marker = mMap.addMarker(MarkerOptions().position(latLng).title(result.name))
                            marker?.tag = result.place_id
                        }
                    } ?: run {
                        showToast("No hiking places found.")
                    }
                } else {
                    showToast("Error fetching hiking places: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HikingPlacesResponse>, t: Throwable) {
                showToast("Network request failed: ${t.message}")
            }
        })

        // kur klikohet nje marker m thirret funksioni per me marr
        //detajet e vendit prej API, dhe dergohen te dhenat per tu shfaq
        mMap.setOnMarkerClickListener { marker ->
            val placeId = marker.tag as String
            fetchPlaceDetails(placeId)
            true
        }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val call = apiService.getPlaceDetails(placeId, "AIzaSyADiW1b8gYiKa-hXF1maGu1fnxw2mKQHus")
        call.enqueue(object : Callback<PlaceDetailsResponse> {
            override fun onResponse(call: Call<PlaceDetailsResponse>, response: Response<PlaceDetailsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.result?.let { placeDetails ->
                        val intent = Intent(this@MainActivity, HikingDetailsActivity::class.java)
                        intent.putExtra("name", placeDetails.name)
                        intent.putExtra("latitude", placeDetails.geometry.location.lat)
                        intent.putExtra("longitude", placeDetails.geometry.location.lng)
                        intent.putExtra("address", placeDetails.formatted_address)
                        intent.putExtra("phone_number", placeDetails.international_phone_number)
                        intent.putExtra("website", placeDetails.website)
                        intent.putExtra("rating", placeDetails.rating)
                        intent.putExtra("user_ratings_total", placeDetails.user_ratings_total)
                        intent.putParcelableArrayListExtra("photos", ArrayList(placeDetails.photos ?: emptyList()))
                        startActivity(intent)
                    }
                } else {
                    showToast("Error fetching place details: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PlaceDetailsResponse>, t: Throwable) {
                showToast("Network request failed: ${t.message}")
            }
        })
    }

    private fun toggleSearchFormVisibility() {
        if (isSearchFormVisible) {
            searchForm.visibility = View.GONE
            imageViewToggleSearch.setImageResource(R.drawable.ic_expand_more)
        } else {
            searchForm.visibility = View.VISIBLE
            imageViewToggleSearch.setImageResource(R.drawable.ic_expand_less)
        }
        isSearchFormVisible = !isSearchFormVisible
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
