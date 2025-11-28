package com.anggi.timo.laporan

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class LaporanFragment : Fragment() {

    private lateinit var adapter: LaporanAdapter
    private val listLaporan = mutableListOf<LaporanModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_laporan, container, false)

        val btnHarian = rootView.findViewById<TextView>(R.id.btnHarian)
        val btnMingguan = rootView.findViewById<TextView>(R.id.btnMingguan)
        val btnTahunan = rootView.findViewById<TextView>(R.id.btnTahunan)
        val pieChart = rootView.findViewById<PieChart>(R.id.pieChart)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewLaporan)

        val purpleColor = Color.parseColor("#1A94FF")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LaporanAdapter(listLaporan)
        recyclerView.adapter = adapter

        fun showChart(data: List<PieEntry>, title: String) {
            val dataSet = PieDataSet(data, title)
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextSize = 14f

            val pieData = PieData(dataSet)
            pieChart.data = pieData
            pieChart.centerText = title
            pieChart.setCenterTextSize(16f)
            pieChart.description.isEnabled = false
            pieChart.animateY(1000)
            pieChart.invalidate()
        }

        fun selectButton(active: TextView) {
            val buttons = listOf(btnHarian, btnMingguan, btnTahunan)
            buttons.forEach { btn ->
                btn.setBackgroundColor(Color.TRANSPARENT)
                btn.setTextColor(Color.WHITE)
            }
            active.setBackgroundColor(Color.WHITE)
            active.setTextColor(purpleColor)
        }

        fun loadHarian() {
            listLaporan.clear()
            listLaporan.add(LaporanModel("A", "Kotlin", "1:00:00"))
            listLaporan.add(LaporanModel("B", "Java", "2:30:00"))
            adapter.notifyDataSetChanged()
        }

        fun loadMingguan() {
            listLaporan.clear()
            listLaporan.add(LaporanModel("A", "UI/UX", "6:00:00"))
            listLaporan.add(LaporanModel("B", "SQL", "2:00:00"))
            listLaporan.add(LaporanModel("C", "API", "3:20:00"))
            adapter.notifyDataSetChanged()
        }

        fun loadTahunan() {
            listLaporan.clear()
            listLaporan.add(LaporanModel("A", "Pemrograman", "50:00:00"))
            listLaporan.add(LaporanModel("B", "Jaringan", "40:00:00"))
            listLaporan.add(LaporanModel("C", "Manajemen Proyek", "30:00:00"))
            adapter.notifyDataSetChanged()
        }

        // Button Action
        btnHarian.setOnClickListener {
            selectButton(btnHarian)
            showChart(
                listOf(
                    PieEntry(40f, "Transaksi A"),
                    PieEntry(60f, "Transaksi B")
                ), "Harian"
            )
            loadHarian()
        }

        btnMingguan.setOnClickListener {
            selectButton(btnMingguan)
            showChart(
                listOf(
                    PieEntry(30f, "A"),
                    PieEntry(20f, "B"),
                    PieEntry(50f, "C")
                ), "Mingguan"
            )
            loadMingguan()
        }

        btnTahunan.setOnClickListener {
            selectButton(btnTahunan)
            showChart(
                listOf(
                    PieEntry(10f, "A"),
                    PieEntry(20f, "B"),
                    PieEntry(30f, "C"),
                    PieEntry(40f, "D")
                ), "Tahunan"
            )
            loadTahunan()
        }

        // Trigger default
        btnHarian.performClick()

        return rootView
    }
}