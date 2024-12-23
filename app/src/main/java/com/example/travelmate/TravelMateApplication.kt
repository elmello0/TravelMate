package com.example.travelmate

import android.app.Application
import com.google.firebase.FirebaseApp

class TravelMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
    }
}
