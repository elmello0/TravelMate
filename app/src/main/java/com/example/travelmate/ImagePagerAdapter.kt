import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.travelmate.R

class ImagePagerAdapter(private val context: Context, private val images: List<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(images[position])
        container.addView(view)
        return view
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
