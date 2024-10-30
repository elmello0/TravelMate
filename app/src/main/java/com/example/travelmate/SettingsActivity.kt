package com.example.travelmate

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchThemeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchThemeButton = findViewById(R.id.btnSwitchTheme)

        // Cambiar tema (esto es un ejemplo; puedes implementarlo con más lógica)
        switchThemeButton.setOnClickListener {
            Toast.makeText(this, "Cambiar tema (a implementar)", Toast.LENGTH_SHORT).show()
        }
    }
}
