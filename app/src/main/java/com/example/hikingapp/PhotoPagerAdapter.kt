package com.example.hikingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoPagerAdapter(private val context: Context, private val photos: List<Photo>, private val apiKey: String) :
    RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {

        // krijon pamjen e qdo fotos qe ka mu vendos ne karosel
        //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    // per qdo foto krijohet URL-i tu perdor api key dhe referencen e fotos
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo.photo_reference}&key=$apiKey"
        Picasso.get().load(photoUrl).placeholder(R.drawable.ic_no_image_available).into(holder.imageView)
    }

    // ktehn numrin e fotov qe duhet mu shfaq ne RecycleView
    override fun getItemCount(): Int {
        return photos.size
    }
   // perfaqson nje foto individuale ne karosel
    // ImageView vendi ku shfaqet foto
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPhoto)
    }
}
