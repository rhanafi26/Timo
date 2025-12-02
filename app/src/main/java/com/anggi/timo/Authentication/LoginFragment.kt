package com.timo.app.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anggi.timo.MainActivity
import com.anggi.timo.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengambil referensi View
        val inputEmail: EditText = view.findViewById(R.id.input_email)
        val inputPassword: EditText = view.findViewById(R.id.input_password)
        val signInButton: Button = view.findViewById(R.id.button_sign_in)
        val signUpText: TextView = view.findViewById(R.id.text_sign_up_here)

        // 1. Menangani klik tombol SIGN IN
        signInButton.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // KONDISI GAGAL: Jika salah satu field kosong
                Toast.makeText(context, "Email dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // KONDISI SUKSES: Jika email dan password TIDAK KOSONG, anggap login berhasil.
            Toast.makeText(context, "Login berhasil $email!", Toast.LENGTH_LONG).show()

            // Navigasi ke MainActivity (Dashboard)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish() // Tutup AuthenticationActivity

        }

        // 2. Menangani klik "Sign Up Here" (tetap mengarah ke RegisterFragment)
        signUpText.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Gagal Navigasi: Nav Graph belum dikonfigurasi.", Toast.LENGTH_LONG).show()
            }
        }
    }
}