package com.anggi.timo.laporan

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.anggi.timo.ViewModel.RoomBelajarViewModel
import com.anggi.timo.ViewModel.RoomBelajarViewModelFactory
import com.anggi.timo.database.AppDatabase
import com.anggi.timo.repository.LaporanBelajarRepository
import com.anggi.timo.utils.ProgressUtils
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LaporanFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var tvTanggal: TextView
    private lateinit var tvFocus: TextView
    private lateinit var tvAverage: TextView
    private lateinit var tvBreak: TextView
    private lateinit var tvAchievement: TextView
    private lateinit var adapter: LaporanAdapter

    // Gunakan ViewModel Room
    private val roomViewModel: RoomBelajarViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = LaporanBelajarRepository(database.laporanBelajarDao())
        RoomBelajarViewModelFactory(repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_laporan, container, false)

        // Binding UI
        val btnHarian = rootView.findViewById<TextView>(R.id.btnHarian)
        val btnMingguan = rootView.findViewById<TextView>(R.id.btnMingguan)
        val btnBulanan = rootView.findViewById<TextView>(R.id.btnTahunan)

        pieChart = rootView.findViewById(R.id.pieChart)
        tvTanggal = rootView.findViewById(R.id.tvTanggal)
        tvFocus = rootView.findViewById(R.id.tvTingkatFokus)
        tvAverage = rootView.findViewById(R.id.tvRataRata)
        tvBreak = rootView.findViewById(R.id.tvIstirahat)
        tvAchievement = rootView.findViewById(R.id.tvPencapaian)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewLaporan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dengan list kosong
        adapter = LaporanAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        btnBulanan.text = "Bulanan"

        // Logic Tombol
        btnHarian.setOnClickListener {
            tvTanggal.text = getTodayDate()
            loadDailyReportRoom()
        }
        btnMingguan.setOnClickListener {
            tvTanggal.text = getLast7DaysRange()
            loadWeeklyReportRoom()
        }
        btnBulanan.setOnClickListener {
            tvTanggal.text = getLast30DaysRange()
            loadMonthlyReportRoom()
        }

        btnHarian.performClick()
        return rootView
    }

    private fun loadDailyReportRoom() {
        val tanggalDB = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        roomViewModel.getStatistikListHarian(tanggalDB).observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Harian")
        }
    }

    private fun loadWeeklyReportRoom() {
        val cal = Calendar.getInstance()
        val formatDB = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDateDB = formatDB.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, -7)
        val startDateDB = formatDB.format(cal.time)

        roomViewModel.getStatistikListMingguan(startDateDB, endDateDB).observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Mingguan")
        }
    }

    private fun loadMonthlyReportRoom() {
        val bulanDB = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().time)

        roomViewModel.getStatistikListBulanan(bulanDB).observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Bulanan")
        }
    }

    private fun processRoomDataToUI(listPelajaran: List<StatistikPelajaran>, title: String) {
        var totalFokus = 0
        var totalBreak = 0
        val entries = mutableListOf<PieEntry>()
        val laporanBaru = mutableListOf<LaporanModel>()

        if (listPelajaran.isEmpty()) {
            adapter.updateData(emptyList())
            setupChart(emptyList(), title)
            tvFocus.text = "0%"
            tvBreak.text = "0"
            tvAverage.text = "00:00:00"
            tvAchievement.text = "0%"
            return
        }

        for (item in listPelajaran) {
            totalFokus += item.totalFokus
            totalBreak += item.totalIstirahat

            entries.add(PieEntry(item.totalFokus.toFloat(), item.jenisBelajar))


            laporanBaru.add(LaporanModel(
                ProgressUtils.hitungTingkatFokus(item.totalFokus, item.totalIstirahat),
                item.jenisBelajar,
                ProgressUtils.formatDuration(item.totalFokus)
            ))
        }

        adapter.updateData(laporanBaru)
        setupChart(entries, title)

        tvBreak.text = totalBreak.toString()
        tvFocus.text = ProgressUtils.hitungTingkatFokus(totalFokus, totalBreak)

        tvAverage.text = ProgressUtils.formatDuration(totalFokus / listPelajaran.size)
        tvAchievement.text = "100%"
    }

    private fun setupChart(entries: List<PieEntry>, title: String) {
        val dataSet = PieDataSet(entries, title)
        dataSet.colors = listOf(Color.parseColor("#2ECC71"), Color.parseColor("#F1C40F"), Color.parseColor("#3498DB"), Color.parseColor("#E74C3C"))
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun getTodayDate() = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
    private fun getLast7DaysRange() = "7 Hari Terakhir"
    private fun getLast30DaysRange() = "30 Hari Terakhir"
}