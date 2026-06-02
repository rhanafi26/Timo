package com.anggi.timo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anggi.timo.databinding.FragmentProfilBinding
import androidx.navigation.fragment.findNavController
import com.anggi.timo.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isEditing = false
    private var currentCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loadProfileData()
        updateDateDisplay()
        setEditMode(false)

        binding.btnLogout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(
                requireActivity(),
                AuthenticationActivity::class.java
            )

            startActivity(intent)
            requireActivity().finish()
        }
        binding.btnEditProfileBottom.setOnClickListener {
            if (isEditing) {
                saveProfileData()
            } else {
                startEditing()
            }
        }

        binding.ivPreviousDate.setOnClickListener { changeDate(-1) }
        binding.ivNextDate.setOnClickListener { changeDate(1) }
        binding.dateSelectorContainer.setOnClickListener { showDatePickerDialog() }
        binding.refresh.setOnClickListener {
            Toast.makeText(
                context,
                "Memperbarui data waktu belajar...",
                Toast.LENGTH_SHORT).show()
        }

    }

    private fun startEditing() {
        isEditing = true
        binding.btnEditProfileBottom.text = "SIMPAN & KEMBALI"
        setEditMode(true)
        Toast.makeText(
            context,
            "Silakan ubah username.",
            Toast.LENGTH_LONG).show()
        binding.usernameValue.requestFocus()
    }

    private fun saveProfileData() {
        val username = binding.usernameValue.text.toString().trim()

        if (username.isEmpty()) {
            binding.usernameValue.error = "Username tidak boleh kosong"
            return
        }

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .update("username", username)
            .addOnSuccessListener {

                isEditing = false
                setEditMode(false)
                binding.btnEditProfileBottom.text = "EDIT PROFILE"

                Toast.makeText(
                    requireContext(),
                    "Username berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController()
                    .navigate(R.id.action_profilFragment_to_dashboardFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun setEditMode(editable: Boolean) {
        val editViews = binding.usernameValue
        editViews.isFocusable = editable
        editViews.isFocusableInTouchMode = editable
        editViews.isClickable = editable
        editViews.setBackgroundResource(if (editable) R.drawable.edittext_border else 0)

    }

    private fun loadProfileData() {
        val currentUser = auth.currentUser ?: return

        binding.emailValue.setText(currentUser.email)
        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val username =
                        document.getString("username") ?: "Pengguna"

                    binding.usernameValue.setText(username)

                } else {

                    binding.usernameValue.setText("Pengguna")
                }
            }
        val totalTime = "12:45:30"
        binding.totalTime.text = totalTime
    }

    private fun changeDate(days: Int) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, days)
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID"))
        binding.tvDateDisplay.text = dateFormat.format(currentCalendar.time)
    }

    private fun showDatePickerDialog() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                currentCalendar.set(selectedYear, selectedMonth, selectedDay)
                updateDateDisplay()
                Toast.makeText(
                    context,
                    "Data akan dimuat untuk tanggal: ${binding.tvDateDisplay.text}",
                    Toast.LENGTH_SHORT).show()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}