import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.travelmate.R

class ImagePagerAdapter(
    private val context: Context,
    private val images: List<Int>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Inflate the custom layout for each page
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image, container, false)

        // Set the image to the ImageView
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(images[position])

        // Add a fade-in animation when the image is displayed
        imageView.alpha = 0f
        imageView.animate().alpha(1f).setDuration(500).start()

        // Add the view to the container
        container.addView(view)
        return view
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
