package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddDocumentActivity : AppCompatActivity() {

    private lateinit var documentNameEditText: EditText
    private lateinit var selectFileButton: Button
    private lateinit var uploadButton: Button
    private lateinit var cancelButton: Button
    private var selectedFileUri: Uri? = null
    private lateinit var selectedFileIcon: ImageView

    private val PICK_FILE_REQUEST_CODE = 100

    // Firebase instances
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_document)

        documentNameEditText = findViewById(R.id.etDocumentName)
        selectFileButton = findViewById(R.id.btnSelectFile)
        uploadButton = findViewById(R.id.btnSave)
        cancelButton = findViewById(R.id.btnCancel)
        selectedFileIcon = findViewById(R.id.ivUploadFile)

        // Select file
        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        }

        // Upload document
        uploadButton.setOnClickListener {
            val documentName = documentNameEditText.text.toString().trim()
            if (documentName.isNotEmpty() && selectedFileUri != null) {
                uploadFileToFirebase(documentName, selectedFileUri!!)
            } else {
                Toast.makeText(this, "Por favor, selecciona un archivo y añade un nombre", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadFileToFirebase(documentName: String, fileUri: Uri) {
        // Create a unique path for the file in Firebase Storage
        val fileRef = storage.reference.child("documents/${UUID.randomUUID()}")

        // Upload the file to Firebase Storage
        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                // Get the download URL after uploading
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveDocumentToFirestore(documentName, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir archivo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDocumentToFirestore(name: String, fileUrl: String) {
        val document = Document(name = name, filePath = fileUrl)
        db.collection("documents").add(document)
            .addOnSuccessListener {
                Toast.makeText(this, "Documento añadido", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("document_name", name)
                resultIntent.putExtra("document_uri", fileUrl)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar documento en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.data

            if (selectedFileUri != null) {
                selectedFileIcon.setImageResource(R.drawable.ic_file_selected)

                // Get the name of the selected file
                val cursor = contentResolver.query(selectedFileUri!!, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val fileName = it.getString(nameIndex)
                        documentNameEditText.setText(fileName)
                    }
                }
            } else {
                Toast.makeText(this, "No se pudo obtener el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
