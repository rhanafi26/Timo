package com.anggi.timo.laporan

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LaporanFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var pieChart: PieChart
    private lateinit var tvTanggal: TextView
    private lateinit var tvFocus: TextView
    private lateinit var tvAverage: TextView
    private lateinit var tvBreak: TextView
    private lateinit var tvAchievement: TextView
    private lateinit var adapter: LaporanAdapter

    private lateinit var btnHarian: TextView
    private lateinit var btnMingguan: TextView
    private lateinit var btnBulanan: TextView

    private var ivArrowLeft: ImageView? = null
    private var ivArrowRight: ImageView? = null

    private var currentCalendar = Calendar.getInstance()
    private var currentMode = "Harian"
    private var currentStatisticsLiveData: LiveData<List<StatistikPelajaran>>? = null

    private val roomViewModel: RoomBelajarViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = LaporanBelajarRepository(database.laporanBelajarDao())
        RoomBelajarViewModelFactory(repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_laporan, container, false)

        btnHarian = rootView.findViewById(R.id.btnHarian)
        btnMingguan = rootView.findViewById(R.id.btnMingguan)
        btnBulanan = rootView.findViewById(R.id.btnTahunan)

        pieChart = rootView.findViewById(R.id.pieChart)
        tvTanggal = rootView.findViewById(R.id.tvTanggal)
        tvFocus = rootView.findViewById(R.id.tvTingkatFokus)
        tvAverage = rootView.findViewById(R.id.tvRataRata)
        tvBreak = rootView.findViewById(R.id.tvIstirahat)
        tvAchievement = rootView.findViewById(R.id.tvPencapaian)

        ivArrowLeft = rootView.findViewById(R.id.ivArrowLeft)
        ivArrowRight = rootView.findViewById(R.id.ivArrowRight)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewLaporan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LaporanAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        btnBulanan.text = "Bulanan"

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            roomViewModel.getCount { jumlah ->
                if (jumlah == 0) {
                    db.collection("time_study")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { documents ->
                            documents.forEach { doc ->

                                val tanggal = doc.getString("created") ?: ""
                                val inputFormat = SimpleDateFormat(
                                    "dd-MM-yyyy HH:mm",
                                    Locale.getDefault()
                                )

                                val outputFormat = SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                )

                                val date = inputFormat.parse(tanggal)

                                val tanggalBaru = outputFormat.format(date!!)

                                val durasiFokus = doc.getLong("time")?.toInt() ?: 0
                                val durasiIstirahat = doc.getLong("breakTime")?.toInt() ?: 0

                                val typeStudyId = doc.getString("typeStudyId") ?: ""
                                val typeStudyName = doc.getString("typeStudyName")


                                if (!typeStudyName.isNullOrEmpty()) {
                                    val laporanBaru = LaporanBelajarEntity(
                                        tanggal = tanggalBaru,
                                        jenisBelajar = typeStudyName,
                                        durasiFokus = durasiFokus,
                                        durasiIstirahat = durasiIstirahat
                                    )
                                    roomViewModel.insert(laporanBaru)
                                }
                                else if (typeStudyId.isNotEmpty()) {
                                    db.collection("tujuan").document(typeStudyId).get()
                                        .addOnSuccessListener { tDoc ->
                                            val namaPelajaran = tDoc.getString("title") ?: "Pelajaran"
                                            val laporanBaru = LaporanBelajarEntity(
                                                tanggal = tanggalBaru,
                                                jenisBelajar = namaPelajaran,
                                                durasiFokus = durasiFokus,
                                                durasiIstirahat = durasiIstirahat
                                            )
                                            roomViewModel.insert(laporanBaru)
                                        }
                                }
                            }
                        }
                }
            }
        }

        btnHarian.setOnClickListener {
            currentMode = "Harian"
            currentCalendar = Calendar.getInstance()
            setTabActive(btnHarian, btnMingguan, btnBulanan)
            updateDateAndLoadData()
        }

        btnMingguan.setOnClickListener {
            currentMode = "Mingguan"
            currentCalendar = Calendar.getInstance()
            setTabActive(btnMingguan, btnHarian, btnBulanan)
            updateDateAndLoadData()
        }

        btnBulanan.setOnClickListener {
            currentMode = "Bulanan"
            currentCalendar = Calendar.getInstance()
            setTabActive(btnBulanan, btnHarian, btnMingguan)
            updateDateAndLoadData()
        }

        ivArrowLeft?.setOnClickListener { navigateDate(-1) }
        ivArrowRight?.setOnClickListener { navigateDate(1) }

        btnHarian.performClick()
    }

    private fun setTabActive(activeBtn: TextView, inactiveBtn1: TextView, inactiveBtn2: TextView) {
        activeBtn.setBackgroundColor(Color.WHITE)
        activeBtn.setTextColor(Color.parseColor("#1A94FF"))

        inactiveBtn1.setBackgroundColor(Color.TRANSPARENT)
        inactiveBtn1.setTextColor(Color.WHITE)

        inactiveBtn2.setBackgroundColor(Color.TRANSPARENT)
        inactiveBtn2.setTextColor(Color.WHITE)
    }

    private fun navigateDate(direction: Int) {
        when (currentMode) {
            "Harian" -> currentCalendar.add(Calendar.DAY_OF_MONTH, direction)
            "Mingguan" -> currentCalendar.add(Calendar.DAY_OF_MONTH, direction * 7)
            "Bulanan" -> currentCalendar.add(Calendar.MONTH, direction)
        }
        updateDateAndLoadData()
    }

    private fun updateDateAndLoadData() {
        when (currentMode) {
            "Harian" -> {
                tvTanggal.text = getTodayDate()
                loadDailyReportRoom()
            }
            "Mingguan" -> {
                tvTanggal.text = getLast7DaysRange()
                loadWeeklyReportRoom()
            }
            "Bulanan" -> {
                tvTanggal.text = getLast30DaysRange()
                loadMonthlyReportRoom()
            }
        }
    }

    private fun loadDailyReportRoom() {
        val tanggalDB = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentCalendar.time)
        currentStatisticsLiveData?.removeObservers(viewLifecycleOwner)
        currentStatisticsLiveData = roomViewModel.getStatistikListHarian(tanggalDB)
        currentStatisticsLiveData?.observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Harian")
        }
    }

    private fun loadWeeklyReportRoom() {
        val formatDB = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDateDB = formatDB.format(currentCalendar.time)
        val calStart = currentCalendar.clone() as Calendar
        calStart.add(Calendar.DAY_OF_MONTH, -6)
        val startDateDB = formatDB.format(calStart.time)

        currentStatisticsLiveData?.removeObservers(viewLifecycleOwner)
        currentStatisticsLiveData = roomViewModel.getStatistikListMingguan(startDateDB, endDateDB)
        currentStatisticsLiveData?.observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Mingguan")
        }
    }

    private fun loadMonthlyReportRoom() {
        val bulanDB = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentCalendar.time)
        currentStatisticsLiveData?.removeObservers(viewLifecycleOwner)
        currentStatisticsLiveData = roomViewModel.getStatistikListBulanan(bulanDB)
        currentStatisticsLiveData?.observe(viewLifecycleOwner) { list ->
            processRoomDataToUI(list ?: emptyList(), "Bulanan")
        }
    }

    private fun processRoomDataToUI(listPelajaran: List<StatistikPelajaran>, title: String) {
        var totalFokus = 0
        var totalBreak = 0
        val entries = mutableListOf<PieEntry>()
        val laporanBaru = mutableListOf<LaporanModel>()
        val listFokus = mutableListOf<String>()

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

            val agregate = ProgressUtils.hitungTingkatFokus(item.totalFokus, item.totalIstirahat)
            listFokus.add(agregate)

            laporanBaru.add(LaporanModel(
                agregate,
                item.jenisBelajar,
                ProgressUtils.formatDuration(item.totalFokus)
            ))
        }

        adapter.updateData(laporanBaru)
        setupChart(entries, title)

        tvBreak.text = totalBreak.toString()
        tvFocus.text = ProgressUtils.calculateAverageFocus(listFokus)
        tvAverage.text = ProgressUtils.formatDuration(totalFokus / listPelajaran.size)
        tvAchievement.text = "100%"
    }

    private fun setupChart(entries: List<PieEntry>, title: String) {
        val dataSet = PieDataSet(entries, title)
        dataSet.colors = listOf(Color.parseColor("#2ECC71"), Color.parseColor("#F1C40F"), Color.parseColor("#3498DB"), Color.parseColor("#E74C3C"))
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("EEE, dd/MMM/yyyy", Locale("id", "ID")).format(currentCalendar.time)
    }

    private fun getLast7DaysRange(): String {
        val formatUI = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val endDate = formatUI.format(currentCalendar.time)
        val calStart = currentCalendar.clone() as Calendar
        calStart.add(Calendar.DAY_OF_MONTH, -6)
        val startDate = formatUI.format(calStart.time)

        return "$startDate - $endDate"
    }

    private fun getLast30DaysRange(): String {
        return SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(currentCalendar.time)
    }
}