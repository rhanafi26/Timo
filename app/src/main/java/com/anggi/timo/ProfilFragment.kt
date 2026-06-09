package com.anggi.timo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anggi.timo.ViewModel.RoomBelajarViewModel
import com.anggi.timo.ViewModel.RoomBelajarViewModelFactory
import com.anggi.timo.ViewModel.StudyViewModel
import com.anggi.timo.authentication.AuthenticationActivity
import com.anggi.timo.database.AppDatabase
import com.anggi.timo.databinding.FragmentProfilBinding
import com.anggi.timo.repository.LaporanBelajarRepository
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
    private lateinit var studyViewModel: StudyViewModel

    private var isEditing = false
    private var currentCalendar: Calendar = Calendar.getInstance()


    private val firebaseSearchFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


    private val uiDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    private val roomViewModel: RoomBelajarViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = LaporanBelajarRepository(database.laporanBelajarDao())
        RoomBelajarViewModelFactory(repository)
    }

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


        studyViewModel = ViewModelProvider(this)[StudyViewModel::class.java]

        loadProfileData()
        updateDateDisplay()
        setEditMode(false)

        binding.btnLogout.setOnClickListener {
            roomViewModel.clearSemuaData()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
            updateDateDisplay()
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

    }

    private fun changeDate(days: Int) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, days)
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        binding.tvDateDisplay.text = uiDateFormat.format(currentCalendar.time)

        val searchDate = firebaseSearchFormat.format(currentCalendar.time)

        fetchStudyTimeForDate(searchDate)
    }

    private fun fetchStudyTimeForDate(dateString: String) {

        binding.totalTime.text = "Memuat..."

        studyViewModel.getStudyTimeByDate(dateString) { totalStudyTime ->

            val jam = totalStudyTime / 3600
            val menit = (totalStudyTime % 3600) / 60
            val detik = totalStudyTime % 60


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