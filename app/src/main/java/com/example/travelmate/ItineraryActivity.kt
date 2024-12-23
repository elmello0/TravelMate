package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)

        groupId = intent.getStringExtra("group_id") // Recibir el ID del grupo
        if (groupId == null) {
            Toast.makeText(this, "Error: No se encontró el ID del grupo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerViewItinerary)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        itineraryList = arrayListOf()
        itineraryAdapter = ItineraryAdapter(
            itineraryList,
            onItineraryChecked = { itinerary -> updateItineraryInFirestore(itinerary) },
            onItineraryDeleted = { itinerary -> deleteItinerary(itinerary) }
        )
        recyclerView.adapter = itineraryAdapter

        loadItineraryData()

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
        db.collection("groups").document(groupId!!)
            .collection("itineraries")
            .add(itinerary)
            .addOnSuccessListener { documentReference ->
                itinerary.documentId = documentReference.id
                Toast.makeText(this, "Itinerario añadido", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al añadir itinerario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadItineraryData() {
        db.collection("groups").document(groupId!!)
            .collection("itineraries")
            .get()
            .addOnSuccessListener { documents ->
                itineraryList.clear()
                for (document in documents) {
                    val itinerary = document.toObject(Itinerary::class.java).apply {
                        documentId = document.id
                    }
                    itineraryList.add(itinerary)
                }
                itineraryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar itinerarios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateItineraryInFirestore(itinerary: Itinerary) {
        db.collection("groups").document(groupId!!)
            .collection("itineraries")
            .document(itinerary.documentId!!)
            .set(itinerary)
            .addOnSuccessListener {
                Toast.makeText(this, "Itinerario actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar itinerario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteItinerary(itinerary: Itinerary) {
        db.collection("groups").document(groupId!!)
            .collection("itineraries")
            .document(itinerary.documentId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Itinerario eliminado", Toast.LENGTH_SHORT).show()
                itineraryList.remove(itinerary)
                itineraryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar itinerario", Toast.LENGTH_SHORT).show()
            }
    }
}
