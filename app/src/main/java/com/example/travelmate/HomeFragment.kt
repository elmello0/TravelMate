package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var handler: Handler
    private var currentPage = 0
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var funcionalidadesNavView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        imageAdapter = ImageAdapter(requireContext(), getImageList())
        viewPager.adapter = imageAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)

        handler = Handler(Looper.getMainLooper())
        startAutoSlide()

        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        funcionalidadesNavView = view.findViewById(R.id.funcionalidades_view)

        // Debugging adicional para verificar la configuración del menú
        Log.d("HomeFragment", "Setting up navigation listeners")

        navView.setNavigationItemSelectedListener { menuItem ->
            Toast.makeText(context, "Selected: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            handleMenuItem(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        funcionalidadesNavView.setNavigationItemSelectedListener { menuItem ->
            Toast.makeText(context, "Selected: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            handleFuncionalidadesMenu(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        return view
    }

    private fun startAutoSlide() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage == getImageList().size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    private fun handleMenuItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(activity, ProfileActivity::class.java))
                Log.d("HomeFragment", "Navigating to ProfileActivity")
            }
            R.id.nav_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                Log.d("HomeFragment", "Navigating to SettingsActivity")
            }
            R.id.nav_logout -> {
                showLogoutConfirmationDialog()
                Log.d("HomeFragment", "Logging out")
            }
        }
    }

    private fun handleFuncionalidadesMenu(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_itineraries -> startActivity(Intent(activity, ItineraryActivity::class.java))
            R.id.nav_tasks -> startActivity(Intent(activity, TasksActivity::class.java))
            R.id.nav_expenses -> startActivity(Intent(activity, ExpensesActivity::class.java))
            R.id.nav_map -> startActivity(Intent(activity, MapActivity::class.java))
            R.id.nav_documents -> startActivity(Intent(activity, DocumentsActivity::class.java))
            R.id.nav_chat -> startActivity(Intent(activity, ChatActivity::class.java))
            R.id.nav_create_group -> startActivity(Intent(activity, CreateGroupActivity::class.java))
        }
    }

    private fun getImageList(): List<Int> {
        return listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6
        )
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
