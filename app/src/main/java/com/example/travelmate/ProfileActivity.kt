package com.example.travelmate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var saveProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernameEditText = findViewById(R.id.etUsername)
        saveProfileButton = findViewById(R.id.btnSaveProfile)

        // Guardar el perfil
        saveProfileButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            if (username.isNotEmpty()) {
                // Guardar nombre de usuario (en una base de datos local, SharedPreferences, etc.)
                Toast.makeText(this, "Nombre de usuario guardado: $username", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
