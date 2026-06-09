package com.anggi.timo.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.anggi.timo.TambahTujuanDialog
import com.anggi.timo.ViewModel.RoomBelajarViewModel
import com.anggi.timo.ViewModel.RoomBelajarViewModelFactory
import com.anggi.timo.ViewModel.StudyViewModel
import com.anggi.timo.database.AppDatabase
import com.anggi.timo.dialogJenisBelajar.PilihJenisBelajarDialog
import com.anggi.timo.laporan.LaporanBelajarEntity
import com.anggi.timo.repository.LaporanBelajarRepository
import com.anggi.timo.utils.ProgressUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val studyViewModel: StudyViewModel by viewModels()

    private val roomViewModel: RoomBelajarViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = LaporanBelajarRepository(database.laporanBelajarDao())
        RoomBelajarViewModelFactory(repository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DashboardAdapter

    private val listDashboard = mutableListOf<DashboardModel>()

    private var seconds = 0
    private var isRunning = false

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var tvClock: TextView
    private lateinit var btnFinish: Button
    private lateinit var timerIcon: ImageView

    // Add references to summary TextViews
    private lateinit var tvTotalTarget: TextView
    private lateinit var tvAverageFocus: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvTimerMain: TextView
    private var breakCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews(rootView)
        setupRecyclerView()
        setupTimer()
        setupButtons(rootView)

        loadDashboardSummary()
        loadTargetBelajar()

        return rootView
    }
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        tvClock = view.findViewById(R.id.tvClock)
        btnFinish = view.findViewById(R.id.btnFinish)
        timerIcon = view.findViewById(R.id.ivTimerIcon)

        // Initialize summary TextViews here
        tvTotalTarget = view.findViewById(R.id.tvTotalTarget)
        tvAverageFocus = view.findViewById(R.id.tvAverageFocus)
        tvProgress = view.findViewById(R.id.tvProgress)
        tvTimerMain = view.findViewById(R.id.tvTimerMain)
    }

    private fun setupRecyclerView() {
        adapter = DashboardAdapter(listDashboard)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupTimer() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                seconds++
                updateTimerText()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun setupButtons(rootView: View) {
        val timerButton = rootView.findViewById<ConstraintLayout>(R.id.timerCircleContainer)
        val btnTambah = rootView.findViewById<CardView>(R.id.btnTambah)
        val notificationBell = rootView.findViewById<ImageView>(R.id.ivNotificationBell)
        val notificationBadge = rootView.findViewById<View>(R.id.vNotificationBadge)
        val notificationPopup = rootView.findViewById<CardView>(R.id.cvNotificationPopup)

        timerButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnTambah.setOnClickListener {
            TambahTujuanDialog {
                loadTargetBelajar()
            }.show(
                parentFragmentManager,
                "TambahTujuanDialog"
            )
        }

        btnFinish.setOnClickListener {
            finishStudySession()
        }

        notificationBell.setOnClickListener {
            if (notificationPopup.visibility == View.VISIBLE) {
                notificationPopup.visibility = View.GONE
            } else {
                notificationPopup.visibility = View.VISIBLE
                notificationBadge.visibility = View.GONE
            }
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            handler.post(runnable)
            isRunning = true
            timerIcon.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            breakCount++

            handler.removeCallbacks(runnable)
            isRunning = false
            timerIcon.setImageResource(R.drawable.ic_play)
        }
    }

    private fun updateTimerText() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, secs)

        tvClock.text = timeString
        tvTimerMain.text = timeString

        btnFinish.visibility = if (seconds > 0) View.VISIBLE else View.GONE
    }

    private fun finishStudySession() {
        pauseTimer()

        PilihJenisBelajarDialog { typeStudyId ->

            val waktuFokus = seconds
            val waktuIstirahat = breakCount


            studyViewModel.saveTimeStudy(
                typeStudyId = typeStudyId,
                studyTime = waktuFokus,
                breakTime = waktuIstirahat
            )

            db.collection("tujuan").document(typeStudyId).get()
                .addOnSuccessListener { document ->
                    val namaPelajaran = document.getString("title") ?: "Lainnya"


                    val tanggalHariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val laporanBaru = LaporanBelajarEntity(
                        tanggal = tanggalHariIni,
                        jenisBelajar = namaPelajaran,
                        durasiFokus = waktuFokus,
                        durasiIstirahat = waktuIstirahat
                    )
                }

            Toast.makeText(
                requireContext(),
                "Waktu belajar tersimpan",
                Toast.LENGTH_SHORT
            ).show()

            // Reset Timer dan Istirahat untuk sesi selanjutnya
            seconds = 0
            breakCount = 0
            updateTimerText()

            loadTargetBelajar()

        }.show(parentFragmentManager, "PilihJenisBelajarDialog")
    }

    private fun loadDashboardSummary() {
        // Use the class-level TextView references instead of requireView().findViewById
        studyViewModel.getTodayStudyTime { totalSeconds ->
            tvClock.text = ProgressUtils.formatDuration(totalSeconds)
            tvTimerMain.text = ProgressUtils.formatDuration(totalSeconds)
        }

        studyViewModel.getTotalTarget { totalTarget ->
            tvTotalTarget.text = ProgressUtils.formatDuration(totalTarget)
        }

        studyViewModel.getAverageFocus { averageFocus ->
            tvAverageFocus.text = averageFocus
        }

        studyViewModel.getOverallProgress { progress ->
            tvProgress.text = progress
        }
    }

    private fun loadTargetBelajar() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("tujuan")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                listDashboard.clear()

                if (documents.isEmpty) {
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                var processedCount = 0
                val totalDocuments = documents.size()

                for (document in documents) {
                    val id = document.id
                    val title = document.getString("title") ?: ""
                    val target = document.getLong("target")?.toInt() ?: 0
                    studyViewModel.getTotalStudyTime(id) { totalStudy, totalBreak  ->
                        val focus = ProgressUtils.hitungTingkatFokus(totalStudy, totalBreak )
                        val progress = ProgressUtils.hitungPersentase(totalStudy, target)
                        listDashboard.add(
                            DashboardModel(
                                focus,
                                title,
                                ProgressUtils.formatDuration(target),
                                progress
                            )
                        )

                        processedCount++

                        // Only notify when all documents are processed
                        if (processedCount == totalDocuments) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error loading data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}