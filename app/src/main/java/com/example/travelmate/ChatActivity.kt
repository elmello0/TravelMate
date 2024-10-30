package com.example.travelmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageButton

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<ChatMessage>
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar la lista de mensajes
        messageList = ArrayList()

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMessages)
        chatAdapter = ChatAdapter(messageList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Configurar el campo de entrada y el botón de enviar
        messageInput = findViewById(R.id.etMessageInput)
        sendButton = findViewById(R.id.btnSendMessage)

        // Acción al hacer clic en el botón de enviar
        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString()

            if (messageContent.isNotEmpty()) {
                val newMessage = ChatMessage("Tu nombre", messageContent)
                messageList.add(newMessage)
                chatAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
                messageInput.text.clear()
            }
        }
    }
}
