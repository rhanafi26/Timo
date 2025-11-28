package com.anggi.timo

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anggi.timo.databinding.FragmentProfilBinding
import androidx.navigation.fragment.findNavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

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

        loadProfileData()
        updateDateDisplay()
        setEditMode(false)

        // Hapus fungsi klik pada tvEditProfileTop
        binding.tvEditProfileTop.setOnClickListener(null)

        // Logika tombol bawah untuk Edit/Simpan & Navigasi
        binding.btnEditProfileBottom.setOnClickListener {
            if (isEditing) {
                // Mode Edit: Lakukan Simpan dan Navigasi
                if (saveProfileData(view)) {
                    // Jika validasi sukses, lakukan navigasi
                    Toast.makeText(
                        context,
                        "Data Berhasil Disimpan! Kembali ke Dashboard.",
                        Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_profilFragment_to_dashboardFragment)
                }
                // Jika validasi gagal (kosong), mode edit tetap aktif
            } else {
                // Mode View: Mulai Mode Edit
                startEditing()
            }
        }

        // Logika Kalender dan Refresh
        binding.ivPreviousDate.setOnClickListener { changeDate(-1) }
        binding.ivNextDate.setOnClickListener { changeDate(1) }
        binding.dateSelectorContainer.setOnClickListener { showDatePickerDialog() }
        binding.refresh.setOnClickListener {
            Toast.makeText(
                context,
                "Memperbarui data waktu belajar...",
                Toast.LENGTH_SHORT).show()
        }

        // Kita hanya akan pakai tombol bawah.
        binding.tvEditProfileTop.setOnClickListener(null)
    }

    private fun startEditing() {
        isEditing = true
        // Teks tombol bawah disiapkan untuk aksi Simpan
        binding.btnEditProfileBottom.text = "SIMPAN & KEMBALI"
        setEditMode(true)
        Toast.makeText(
            context,
            "Edit Profile! Silahkan ubah Email dan Username.",
            Toast.LENGTH_LONG).show()
        binding.emailValue.requestFocus()
    }

    // Mengembalikan Boolean untuk menandakan apakah proses simpan valid
    private fun saveProfileData(view: View): Boolean {
        val newEmail = binding.emailValue.text.toString()
        val newUsername = binding.usernameValue.text.toString()

        if (newEmail.isBlank() || newUsername.isBlank()) {
            Toast.makeText(
                context,
                "Email dan Username tidak boleh kosong!",
                Toast.LENGTH_SHORT).show()
            return false // Gagal validasi
        }

        // Data sudah tersimpan di binding.emailValue.text

        isEditing = false
        // Tidak perlu setEditMode(false) atau ganti teks tombol karena akan langsung navigasi.

        // Hilangkan keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)

        return true // Sukses
    }

    private fun setEditMode(editable: Boolean) {
        val editViews = listOf(binding.emailValue, binding.usernameValue)

        for (et in editViews) {
            et.isFocusable = editable
            et.isFocusableInTouchMode = editable
            et.isClickable = editable

            // Mengatur background (membutuhkan R.drawable.edittext_border)
            et.setBackgroundResource(if (editable) R.drawable.edittext_border else 0)
        }
    }

    private fun loadProfileData() {
        val email = "DheandraaKhairunnisa@gmail.com"
        val username = "Meonkie"
        val totalTime = "12:45:30"

        binding.emailValue.setText(email)
        binding.usernameValue.setText(username)
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