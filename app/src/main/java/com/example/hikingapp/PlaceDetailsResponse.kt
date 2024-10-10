package com.example.hikingapp

import android.os.Parcel
import android.os.Parcelable

data class PlaceDetailsResponse(
    val result: PlaceDetails
)

data class PlaceDetails(
    val name: String,
    val geometry: Geometry,
    val photos: List<Photo>?,
    val vicinity: String,
    val rating: Double?,
    val user_ratings_total: Int?,
    val formatted_address: String,
    val international_phone_number: String?,
    val website: String?
)

data class Photo(
    val photo_reference: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photo_reference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}
