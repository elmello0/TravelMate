package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class DocumentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var documentList: ArrayList<Document>
    private lateinit var db: FirebaseFirestore
    private lateinit var noDocumentsMessage: TextView
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        // Inicializar RecyclerView y mensaje de "No hay documentos"
        recyclerView = findViewById(R.id.recyclerViewDocuments)
        noDocumentsMessage = findViewById(R.id.tvNoDocuments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        db = FirebaseFirestore.getInstance()
        documentList = ArrayList()

        // Obtener el groupId del Intent
        groupId = intent.getStringExtra("group_id")
        if (groupId.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo cargar el grupo. Verifica que estás accediendo desde un grupo válido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar el adaptador
        documentAdapter = DocumentAdapter(documentList) { document ->
            downloadDocument(document)
        }
        recyclerView.adapter = documentAdapter

        // Cargar documentos asociados al grupo
        loadDocuments()

        // Configurar FloatingActionButton para añadir documentos
        val fabAddDocument = findViewById<FloatingActionButton>(R.id.fab_add_document)
        fabAddDocument.setOnClickListener {
            val intent = Intent(this, AddDocumentActivity::class.java)
            intent.putExtra("group_id", groupId) // Pasar el groupId a AddDocumentActivity
            startActivity(intent)
        }
    }

    private fun loadDocuments() {
        documentList.clear()

        db.collection("groups")
            .document(groupId!!)
            .collection("documents")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar documentos: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DocumentsActivity", "Error al cargar documentos", error)
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    documentList.clear() // Limpiar la lista actual
                    for (document in querySnapshot) {
                        val doc = document.toObject(Document::class.java).apply {
                            id = document.id // Agregar el ID del documento
                        }
                        documentList.add(doc)
                    }

                    // Mostrar u ocultar el mensaje de "No hay documentos"
                    if (documentList.isEmpty()) {
                        noDocumentsMessage.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noDocumentsMessage.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }

                    documentAdapter.notifyDataSetChanged()
                }
            }
    }


    private fun downloadDocument(document: Document) {
        // Implementar lógica para descargar documento
        Toast.makeText(this, "Descargar documento: ${document.name}", Toast.LENGTH_SHORT).show()
    }
}
