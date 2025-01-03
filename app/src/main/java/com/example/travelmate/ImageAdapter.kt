package com.example.travelmate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.travelmate.R

class ImageAdapter(
    private val context: Context,
    private val imageList: List<Int>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        // Set the image resource
        imageView.setImageResource(imageList[position])

        // Add a fade-in animation to the image
        imageView.alpha = 0f
        imageView.animate().alpha(1f).setDuration(600).start()

        // Optional: Set rounded corners or shadow if supported by the layout
        imageView.clipToOutline = true

        // Add the view to the container
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
