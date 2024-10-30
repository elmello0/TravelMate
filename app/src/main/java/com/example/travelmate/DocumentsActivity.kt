package com.example.travelmate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class DocumentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var documentList: ArrayList<Document>
    private val db = FirebaseFirestore.getInstance()

    private lateinit var addDocumentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        recyclerView = findViewById(R.id.recyclerViewDocuments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        documentList = arrayListOf()

        addDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val documentName = result.data?.getStringExtra("document_name")
                val documentUri = result.data?.getStringExtra("document_uri")

                if (documentName != null && documentUri != null) {
                    val newDocument = Document(documentName, documentUri)
                    documentList.add(newDocument)
                    documentAdapter.notifyDataSetChanged()
                }
            }
        }

        loadDocuments()

        documentAdapter = DocumentAdapter(documentList) { document ->
            downloadDocument(document)
        }
        recyclerView.adapter = documentAdapter

        val fabAddDocument = findViewById<FloatingActionButton>(R.id.fab_add_document)
        fabAddDocument.setOnClickListener {
            val intent = Intent(this, AddDocumentActivity::class.java)
            addDocumentLauncher.launch(intent)
        }
    }

    private fun loadDocuments() {
        documentList.clear()

        db.collection("documents").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val doc = document.toObject(Document::class.java)
                    documentList.add(doc)
                }
                documentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar documentos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadDocument(document: Document) {
        val uri = Uri.parse(document.filePath)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "*/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se puede abrir el documento", Toast.LENGTH_SHORT).show()
        }
    }
}
