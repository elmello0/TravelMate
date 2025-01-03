package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmate.R

class ImageRecyclerAdapter(private val imageList: List<Int>) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageRes: Int) {
            // Set the image
            imageView.setImageResource(imageRes)

            // Add a smooth fade-in animation for when the image is loaded
            imageView.alpha = 0f
            imageView.animate().alpha(1f).setDuration(500).start()

            // Optional: Set corner radius or shadow if item_image.xml supports it
            imageView.clipToOutline = true
        }
    }
}
