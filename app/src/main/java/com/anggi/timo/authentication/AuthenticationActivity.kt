package com.anggi.timo.authentication // Pastikan package ini sesuai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anggi.timo.R // Import R dari package root

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ PASTIKAN R.layout.activity_authentication SUDAH ADA
        setContentView(R.layout.activity_authentication)
    }
}