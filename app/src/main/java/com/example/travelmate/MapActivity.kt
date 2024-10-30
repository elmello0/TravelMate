package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.net.URLEncoder
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URL

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var searchView: SearchView
    private lateinit var btnBackToMenu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Configuración inicial de osmdroid
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        // Inicializar el MapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Configurar zoom y posición inicial
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(-33.4489, -70.6693) // Ejemplo: Santiago, Chile
        mapController.setCenter(startPoint)

        // Inicializar SearchView
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Botón para regresar al menú principal
        btnBackToMenu = findViewById(R.id.btnBackToMenu)
        btnBackToMenu.setOnClickListener {
            finish() // Cierra MapActivity y vuelve a la actividad anterior
        }
    }

    private fun searchLocation(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val geocodeUrl = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = URL(geocodeUrl).readText()
                val jsonArray = JSONArray(jsonResponse) // Cambiar JSONObject a JSONArray

                if (jsonArray.length() > 0) {
                    val locationJson = jsonArray.getJSONObject(0)
                    val lat = locationJson.getDouble("lat")
                    val lon = locationJson.getDouble("lon")

                    withContext(Dispatchers.Main) {
                        val geoPoint = GeoPoint(lat, lon)
                        mapView.controller.setCenter(geoPoint)
                        addMarker(geoPoint)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al buscar ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMarker(geoPoint: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        mapView.invalidate() // Refresca el mapa para mostrar el marcador
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // necesario para osmdroid
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // necesario para osmdroid
    }
}
