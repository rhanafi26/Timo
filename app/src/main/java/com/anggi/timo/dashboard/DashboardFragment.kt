package com.anggi.timo.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.anggi.timo.TambahTujuanDialog

class DashboardFragment : Fragment() {
    private lateinit var adapter: DashboardAdapter
    private val listDashboard = mutableListOf<DashboardModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewDashboard)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listDashboard.clear()
        listDashboard.add(DashboardModel("A", "Kotlin", "1:00:00","100%" ))
        listDashboard.add(DashboardModel("A", "Kotlin", "1:00:00","100%" ))
        listDashboard.add(DashboardModel("A", "Kotlin", "1:00:00","100%" ))
        adapter = DashboardAdapter(listDashboard)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false


        val timerButton = rootView.findViewById<ConstraintLayout>(R.id.timerCircleContainer)
        val timerIcon = rootView.findViewById<ImageView>(R.id.ivTimerIcon)

        var seconds = 0
         var isRunning = false
        val btnFinish = rootView.findViewById<Button>(R.id.btnFinish)

         val handler = android.os.Handler(android.os.Looper.getMainLooper())
         lateinit var runnable: Runnable

        fun updateTimerText() {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            val timeString = String.format("%02d:%02d:%02d", hours, minutes, secs)
            val tvTimer = rootView.findViewById<TextView>(R.id.tvTimerMain)
            tvTimer.text = timeString

            if (seconds > 0) {
                btnFinish.visibility = View.VISIBLE
            } else {
                btnFinish.visibility = View.GONE
            }
        }

         fun startStopwatch() {
            if (!isRunning) {

                runnable = object : Runnable {
                    override fun run() {
                        seconds++
                        updateTimerText()
                        handler.postDelayed(this, 1000)
                    }
                }
                handler.post(runnable)
                isRunning = true

                timerIcon.setImageResource(R.drawable.ic_pause)
            }
        }

         fun pauseStopwatch() {
            if (isRunning) {

                handler.removeCallbacks(runnable)
                isRunning = false

                timerIcon.setImageResource(R.drawable.ic_play)
            }
        }

// --- A. SETUP TOMBOL PLAY/PAUSE (LINGKARAN) ---

        timerButton.setOnClickListener {
            if (isRunning) {
                pauseStopwatch()
            } else {
                startStopwatch()
            }
        }
        val btnTambah = rootView.findViewById<CardView>(R.id.btnTambah)
        btnTambah.setOnClickListener {
            TambahTujuanDialog().show(parentFragmentManager, "TambahTujuanDialog")
        }




        btnFinish.setOnClickListener {
            // 1. Matikan Timer
            pauseStopwatch()

            // 2. Ambil waktu terakhir
            val finalTime = rootView.findViewById<TextView>(R.id.tvTimerMain).text.toString()

            // 3. (Opsional) Tampilkan pesan atau simpan data
            Toast.makeText(requireContext(), "Selesai! Waktu: $finalTime", Toast.LENGTH_SHORT).show()



            // 4. (Opsional) Reset timer kembali ke 0 jika diperlukan
             seconds = 0
             updateTimerText()
        }

        return rootView
    }

}