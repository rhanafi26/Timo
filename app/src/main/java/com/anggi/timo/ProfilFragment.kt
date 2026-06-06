package com.anggi.timo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anggi.timo.ViewModel.StudyViewModel
import com.anggi.timo.authentication.AuthenticationActivity
import com.anggi.timo.databinding.FragmentProfilBinding
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
    private lateinit var studyViewModel: StudyViewModel // Inisialisasi ViewModel

    private var isEditing = false
    private var currentCalendar: Calendar = Calendar.getInstance()

    // 1. Format tanggal untuk dicari di Database ("dd-MM-yyyy") seperti "17-11-2025"
    private val firebaseSearchFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // 2. Format tanggal untuk ditampilkan di UI ("dd MMMM yyyy") seperti "17 November 2025"
    private val uiDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

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

        // Memasang ViewModel
        studyViewModel = ViewModelProvider(this)[StudyViewModel::class.java]

        loadProfileData()
        updateDateDisplay() // Memuat tanggal hari ini dan durasi belajar hari ini
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
            Toast.makeText(context, "Memperbarui data waktu belajar...", Toast.LENGTH_SHORT).show()
            updateDateDisplay() // Refresh manual
        }
    }

    private fun startEditing() {
        isEditing = true
        binding.btnEditProfileBottom.text = "SIMPAN & KEMBALI"
        setEditMode(true)
        Toast.makeText(context, "Silakan ubah username.", Toast.LENGTH_LONG).show()
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

                Toast.makeText(requireContext(), "Username berhasil diperbarui", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profilFragment_to_dashboardFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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
                    val username = document.getString("username") ?: "Pengguna"
                    binding.usernameValue.setText(username)
                } else {
                    binding.usernameValue.setText("Pengguna")
                }
            }

        // Catatan: Hardcode 'val totalTime = "12:45:30"' dihapus, durasi akan diambil langsung dari Firebase.
    }

    private fun changeDate(days: Int) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, days)
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        // 1. Ubah tulisan kalender di atas tulisan durasi belajar ("17 November 2025")
        binding.tvDateDisplay.text = uiDateFormat.format(currentCalendar.time)

        // 2. Ubah tanggal menjadi format Firebase ("17-11-2025")
        val searchDate = firebaseSearchFormat.format(currentCalendar.time)

        // 3. Panggil data dari Firebase menggunakan ViewModel
        fetchStudyTimeForDate(searchDate)
    }

    private fun fetchStudyTimeForDate(dateString: String) {
        // Mengubah teks angka menjadi indikator memuat agar aplikasi terasa responsif
        binding.totalTime.text = "Memuat..."

        studyViewModel.getStudyTimeByDate(dateString) { totalStudyTime ->

            val jam = totalStudyTime / 3600
            val menit = (totalStudyTime % 3600) / 60
            val detik = totalStudyTime % 60

            // Format ke string HH:MM:SS
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", jam, menit, detik)

            binding.totalTime.text = formattedTime
        }
    }

    private fun showDatePickerDialog() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                currentCalendar.set(selectedYear, selectedMonth, selectedDay)

                // Ketika klik OK di kalender pop-up, otomatis refresh UI dan Data
                updateDateDisplay()
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