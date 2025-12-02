package com.timo.app.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anggi.timo.R

/**
 * Activity yang menampung semua Fragment yang berhubungan dengan Otentikasi (Login, Register).
 */
class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
    }
}