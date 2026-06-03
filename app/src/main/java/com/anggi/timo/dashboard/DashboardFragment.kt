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
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.anggi.timo.TambahTujuanDialog
import com.anggi.timo.utils.ProgressUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DashboardAdapter
    private val listDashboard = mutableListOf<DashboardModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewDashboard)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadTargetBelajar()
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
            pauseStopwatch()
            val finalTime = rootView.findViewById<TextView>(R.id.tvTimerMain).text.toString()
            Toast.makeText(requireContext(), "Selesai! Waktu: $finalTime", Toast.LENGTH_SHORT).show()
            seconds = 0
            updateTimerText()
        }


        val notificationBell = rootView.findViewById<ImageView>(R.id.ivNotificationBell)
        val notificationBadge = rootView.findViewById<View>(R.id.vNotificationBadge)
        val notificationPopup = rootView.findViewById<CardView>(R.id.cvNotificationPopup)

        notificationBell.setOnClickListener {
            if (notificationPopup.visibility == View.VISIBLE) {

                notificationPopup.visibility = View.GONE
            } else {

                notificationPopup.visibility = View.VISIBLE
                notificationBadge.visibility = View.GONE
            }
        }

        return rootView
    }
    private fun loadTargetBelajar() {

        val uid = auth.currentUser?.uid ?: return

        db.collection("tujuan")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->

                listDashboard.clear()

                for (document in documents) {

                    val title =
                        document.getString("title") ?: ""

                    val target =
                        document.getLong("target")?.toInt() ?: 0

                    listDashboard.add(
                        DashboardModel(
                            "A",
                            title,
                             ProgressUtils.formatDuration(target),
                            "0%"
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}