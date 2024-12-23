package com.example.travelmate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton // Cambiado a ImageButton
    private lateinit var attachImageButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var groupId: String = ""

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Obtener el ID del grupo del intent
        groupId = intent.getStringExtra("group_id") ?: ""
        if (groupId.isEmpty()) {
            Toast.makeText(this, "No se especificó un grupo válido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar vistas
        chatRecyclerView = findViewById(R.id.recyclerViewChat)
        messageInput = findViewById(R.id.etMessageInput)
        sendButton = findViewById(R.id.btnSendMessage) // Inicialización correcta
        attachImageButton = findViewById(R.id.btnAttachImage)

        // Configurar RecyclerView
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Cargar mensajes desde Firebase Firestore
        loadChatMessages()

        // Enviar mensaje de texto
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message, null, null)
            }
        }

        // Adjuntar imagen
        attachImageButton.setOnClickListener {
            selectImage()
        }
    }

    private fun loadChatMessages() {
        db.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar mensajes: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    chatMessages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(ChatMessage::class.java)
                        if (message != null) {
                            chatMessages.add(message)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                }
            }
    }

    private fun sendMessage(text: String?, imageUrl: String?, documentUrl: String?) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para enviar mensajes.", Toast.LENGTH_SHORT).show()
            return
        }

// Utilizamos el nombre del usuario (displayName) o un valor por defecto si está vacío
        val senderName = currentUser.displayName ?: "Usuario Anónimo"

        val message = ChatMessage(
            senderId = currentUser.uid,
            senderName = senderName, // Añadimos el nombre del remitente aquí
            content = text,
            imageUrl = imageUrl,
            documentUrl = documentUrl,
            timestamp = System.currentTimeMillis(),
            type = when {
                imageUrl != null -> "image"
                documentUrl != null -> "document"
                else -> "text"
            }
        )


        db.collection("groups")
            .document(groupId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                messageInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                uploadImage(imageUri)
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("chat_images/$filename")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    sendMessage(null, uri.toString(), null)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
