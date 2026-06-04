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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val inputEmail: EditText = view.findViewById(R.id.input_email)
        val inputPassword: EditText = view.findViewById(R.id.input_password)
        val signInButton: Button = view.findViewById(R.id.button_sign_in)
        val signUpText: TextView = view.findViewById(R.id.text_sign_up_here)


        signInButton.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(context, "Email dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            signInButton.isEnabled = false
            signInButton.text = "Loading..."


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->

                    signInButton.isEnabled = true
                    signInButton.text = "Sign In"

                    if (task.isSuccessful) {

                        Toast.makeText(
                            context,
                            "Login berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                requireActivity(),
                                MainActivity::class.java
                            )
                        )

                        requireActivity().finish()

                    } else {

                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Login gagal",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }

        }

        signUpText.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Gagal Memuat", Toast.LENGTH_LONG).show()
            }
        }
    }
}