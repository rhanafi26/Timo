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


class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val inputEmail: EditText = view.findViewById(R.id.input_email_register)
        val inputUsername: EditText = view.findViewById(R.id.input_username_register)
        val inputPassword: EditText = view.findViewById(R.id.input_passwordsignup)
        val signUpButton: Button = view.findViewById(R.id.button_sign_up)
        val signInText: TextView = view.findViewById(R.id.text_sign_in_here)

        signUpButton.setOnClickListener{
            val email = inputEmail.text.toString().trim()
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
                Toast.makeText(context, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpButton.isEnabled = false
            signUpButton.text = "Loading..."

            val intent = Intent(activity, MainActivity::class.java)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->

                    if (task.isSuccessful) {

                        val firebaseUser = auth.currentUser

                        if (firebaseUser != null) {

                            val userData = hashMapOf(
                                "uid" to firebaseUser.uid,
                                "username" to username,
                                "email" to email,
                                "createdAt" to System.currentTimeMillis()
                            )

                            db.collection("users")
                                .document(firebaseUser.uid)
                                .set(userData)
                                .addOnSuccessListener {

                                    signUpButton.isEnabled = true
                                    signUpButton.text = "Sign Up"

                                    Toast.makeText(
                                        context,
                                        "Registrasi berhasil",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    startActivity(intent)

                                    requireActivity().finish()
                                }
                                .addOnFailureListener { e ->

                                    signUpButton.isEnabled = true
                                    signUpButton.text = "Sign Up"

                                    Toast.makeText(
                                        context,
                                        "Gagal menyimpan data: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }

                    } else {

                        signUpButton.isEnabled = true
                        signUpButton.text = "Sign Up"

                        Toast.makeText(
                            context,
                            "Registrasi gagal: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        signInText.setOnClickListener{
            try {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Gagal Navigasi: Nav Graph belum dikonfigurasi.", Toast.LENGTH_LONG).show()
            }
        }
    }
}