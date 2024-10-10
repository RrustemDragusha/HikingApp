package com.example.hikingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar

class HikingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hiking_details)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { onBackPressed() }

        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")
        val phoneNumber = intent.getStringExtra("phone_number")
        val website = intent.getStringExtra("website")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val userRatingsTotal = intent.getIntExtra("user_ratings_total", 0)
        @Suppress("DEPRECATION")
        val photos: ArrayList<Photo>? = intent.getParcelableArrayListExtra("photos")

        findViewById<TextView>(R.id.textViewHikingName).text = name
        findViewById<TextView>(R.id.textViewHikingAddress).text = address
        findViewById<TextView>(R.id.textViewHikingRating).text = getString(R.string.rating_reviews, rating, userRatingsTotal)

        val viewPagerPhotos = findViewById<ViewPager2>(R.id.viewPagerPhotos)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLoadingPhoto)

        if (photos != null && photos.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            val photoPagerAdapter = PhotoPagerAdapter(this, photos.take(10), "AIzaSyADiW1b8gYiKa-hXF1maGu1fnxw2mKQHus")
            viewPagerPhotos.adapter = photoPagerAdapter
            progressBar.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
        }

       // equipents jon statike
        val equipmentList = listOf("Water", "Hiking Boots", "First Aid Kit", "Map", "Compass", "Snacks")
        findViewById<TextView>(R.id.textViewHikingEquipment).text = equipmentList.joinToString(separator = "\n")
    }
}
