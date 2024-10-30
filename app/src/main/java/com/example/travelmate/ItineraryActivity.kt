package com.example.travelmate

import com.example.travelmate.Itinerary
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItineraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itineraryAdapter: ItineraryAdapter
    private lateinit var itineraryList: ArrayList<Itinerary>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val ADD_ITINERARY_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerViewItinerary)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        itineraryList = arrayListOf()
        itineraryAdapter = ItineraryAdapter(itineraryList)
        recyclerView.adapter = itineraryAdapter

        loadItineraryData() // Cargar datos desde Firestore

        val fabAddItinerary = findViewById<FloatingActionButton>(R.id.fab_add_itinerary)
        fabAddItinerary.setOnClickListener {
            val intent = Intent(this, AddItineraryActivity::class.java)
            startActivityForResult(intent, ADD_ITINERARY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_ITINERARY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newItinerary = data?.getSerializableExtra("new_itinerary") as Itinerary?
            newItinerary?.let {
                saveItineraryToFirestore(it)
                itineraryList.add(it)
                itineraryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun saveItineraryToFirestore(itinerary: Itinerary) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .collection("itineraries")
                .add(itinerary)
                .addOnSuccessListener { documentReference ->
                    Log.d("ItineraryActivity", "Itinerary added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("ItineraryActivity", "Error adding itinerary", e)
                }
        }
    }

    private fun loadItineraryData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .collection("itineraries")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val itinerary = document.toObject(Itinerary::class.java)
                        itineraryList.add(itinerary)
                    }
                    itineraryAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("ItineraryActivity", "Error getting itineraries", e)
                }
        }
    }
}
