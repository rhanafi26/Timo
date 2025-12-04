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
import androidx.fragment.app.viewModels
import com.anggi.timo.ViewModel.AuthViewModel

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    private val authViewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
            authViewModel.login(email, password)
        }

        authViewModel.loginResult.observe(viewLifecycleOwner) { user ->
            if (user != null)
            {
                val pref = requireActivity().getSharedPreferences("session", 0)
                with(pref.edit()) {
                    putInt("userId", user.id)
                    putString("username", user.username)
                    putString("email", user.email)
                    apply()
                }
                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            } else {
                Toast.makeText(context, "Email atau Password salah!", Toast.LENGTH_SHORT).show()
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