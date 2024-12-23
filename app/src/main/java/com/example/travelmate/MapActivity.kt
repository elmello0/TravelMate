package com.example.travelmate

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var searchView: AutoCompleteTextView
    private lateinit var mapTypeButton: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val PLACES_API_KEY = "AIzaSyCtdRCCC0C0W9Sx0U2I2Gskivwr_gJA6ME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Inicializar el AutoCompleteTextView
        searchView = findViewById(R.id.searchView)
        searchView.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = textView.text.toString()
                if (query.isNotBlank()) {
                    searchLocation(query)
                } else {
                    Toast.makeText(
                        this,
                        "Por favor, ingresa un lugar para buscar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true // Indica que el evento fue manejado
            } else {
                false
            }
        }

        // Agregar un adaptador de sugerencias
        val suggestions =
            resources.getStringArray(R.array.map_suggestions) // Definir en un array.xml
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        searchView.setAdapter(adapter)

        // Inicializar otros componentes
        mapTypeButton = findViewById(R.id.btnMapType)
        mapTypeButton.setOnClickListener { toggleMapType() }
        categorySpinner = findViewById(R.id.categorySpinner)
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categories = resources.getStringArray(R.array.map_categories)
                searchNearbyPlaces(categories[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    // Inicializar botón de cambiar tipo de mapa
        mapTypeButton = findViewById(R.id.btnMapType)
        mapTypeButton.setOnClickListener {
            toggleMapType()
        }

        // Inicializar spinner de categorías
        categorySpinner = findViewById(R.id.categorySpinner)
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categories = resources.getStringArray(R.array.map_categories)
                if (position in categories.indices) {
                    searchNearbyPlaces(categories[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configurar el botón para ir a la ubicación actual
        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            moveToCurrentLocation()
        }

        // Verificar permisos de ubicación y mover a la ubicación actual
        checkLocationPermission()
    }

    private fun moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    googleMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Mi ubicación actual")
                    )
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            checkLocationPermission()
        }
    }

    private fun toggleMapType() {
        googleMap.mapType = when (googleMap.mapType) {
            GoogleMap.MAP_TYPE_NORMAL -> GoogleMap.MAP_TYPE_SATELLITE
            GoogleMap.MAP_TYPE_SATELLITE -> GoogleMap.MAP_TYPE_HYBRID
            GoogleMap.MAP_TYPE_HYBRID -> GoogleMap.MAP_TYPE_TERRAIN
            else -> GoogleMap.MAP_TYPE_NORMAL
        }
        Toast.makeText(this, "Tipo de mapa cambiado", Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableUserLocation()
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            moveToCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedQuery&key=$PLACES_API_KEY"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = URL(geocodeUrl).readText()
                val jsonObject = JSONObject(jsonResponse)

                val results = jsonObject.optJSONArray("results")

                if (results != null && results.length() > 0) {
                    val location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lon = location.getDouble("lng")
                    val formattedAddress = results.getJSONObject(0).optString("formatted_address", query)

                    withContext(Dispatchers.Main) {
                        val latLng = LatLng(lat, lon)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(formattedAddress)
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MapActivity, "No se encontraron resultados para '$query'", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, "Error al buscar ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchNearbyPlaces(category: String) {
        val currentLocation = googleMap.cameraPosition.target
        val placesUrl =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&radius=1500&type=$category&key=$PLACES_API_KEY"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = URL(placesUrl).readText()
                val jsonObject = JSONObject(jsonResponse)
                val results = jsonObject.optJSONArray("results")

                withContext(Dispatchers.Main) {
                    googleMap.clear()
                    if (results != null && results.length() > 0) {
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val location = place.getJSONObject("geometry").getJSONObject("location")
                            val lat = location.getDouble("lat")
                            val lon = location.getDouble("lng")
                            val name = place.optString("name", "Sin nombre")

                            googleMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(name))
                        }
                    } else {
                        Toast.makeText(this@MapActivity, "No se encontraron lugares para '$category'", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, "Error al buscar lugares: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
