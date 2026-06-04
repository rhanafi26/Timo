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
import com.anggi.timo.Model.TimeStudy
import com.anggi.timo.R
import com.anggi.timo.ViewModel.StudyViewModel
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

    private val laporanList = mutableListOf<LaporanModel>()

    private val viewModel by lazy {
        StudyViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView =
            inflater.inflate(
                R.layout.fragment_laporan,
                container,
                false
            )

        val btnHarian =
            rootView.findViewById<TextView>(R.id.btnHarian)

        val btnMingguan =
            rootView.findViewById<TextView>(R.id.btnMingguan)

        val btnBulanan =
            rootView.findViewById<TextView>(R.id.btnTahunan)

        pieChart =
            rootView.findViewById(R.id.pieChart)

        tvTanggal =
            rootView.findViewById(R.id.tvTanggal)

        tvFocus =
            rootView.findViewById(R.id.tvTingkatFokus)

        tvAverage =
            rootView.findViewById(R.id.tvRataRata)

        tvBreak =
            rootView.findViewById(R.id.tvIstirahat)

        tvAchievement =
            rootView.findViewById(R.id.tvPencapaian)

        val recyclerView =
            rootView.findViewById<RecyclerView>(
                R.id.recyclerViewLaporan
            )

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        adapter =
            LaporanAdapter(laporanList)

        recyclerView.adapter =
            adapter

        recyclerView.isNestedScrollingEnabled =
            false

        btnBulanan.text = "Bulanan"

        val blueColor =
            Color.parseColor("#1A94FF")

        fun selectButton(active: TextView) {

            listOf(
                btnHarian,
                btnMingguan,
                btnBulanan
            ).forEach {

                it.setBackgroundColor(
                    Color.TRANSPARENT
                )

                it.setTextColor(
                    Color.WHITE
                )
            }

            active.setBackgroundColor(
                Color.WHITE
            )

            active.setTextColor(
                blueColor
            )
        }

        btnHarian.setOnClickListener {

            selectButton(btnHarian)

            tvTanggal.text =
                getTodayDate()

            loadDailyReport()
        }

        btnMingguan.setOnClickListener {

            selectButton(btnMingguan)

            tvTanggal.text =
                getLast7DaysRange()

            loadWeeklyReport()
        }

        btnBulanan.setOnClickListener {

            selectButton(btnBulanan)

            tvTanggal.text =
                getLast30DaysRange()

            loadMonthlyReport()
        }

        btnHarian.performClick()

        return rootView
    }

    private fun getTodayDate(): String {

        return SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    }

    private fun getLast7DaysRange(): String {

        val endDate =
            Calendar.getInstance()

        val startDate =
            Calendar.getInstance()

        startDate.add(
            Calendar.DAY_OF_MONTH,
            -7
        )

        val sdf =
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            )

        return "${sdf.format(startDate.time)} - ${
            sdf.format(endDate.time)
        }"
    }

    private fun getLast30DaysRange(): String {

        val endDate =
            Calendar.getInstance()

        val startDate =
            Calendar.getInstance()

        startDate.add(
            Calendar.DAY_OF_MONTH,
            -30
        )

        val sdf =
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            )

        return "${sdf.format(startDate.time)} - ${
            sdf.format(endDate.time)
        }"
    }

    private fun setupChart(
        entries: List<PieEntry>,
        title: String
    ) {

        val dataSet =
            PieDataSet(entries, title)

        dataSet.colors = listOf(
            Color.parseColor("#2ECC71"),
            Color.parseColor("#F1C40F"),
            Color.parseColor("#3498DB"),
            Color.parseColor("#E74C3C")
        )

        dataSet.valueTextSize = 14f

        pieChart.data =
            PieData(dataSet)

        pieChart.centerText =
            title

        pieChart.description.isEnabled =
            false

        pieChart.animateY(1000)

        pieChart.invalidate()
    }

    private fun loadDailyReport() {

        viewModel.getAllStudyData { studies ->

            val today =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)

            val filtered =
                studies.filter {
                    it.created.startsWith(today)
                }

            processReport(
                filtered,
                "Harian"
            )
        }
    }

    private fun loadWeeklyReport() {

        viewModel.getAllStudyData { studies ->

            val calendar =
                Calendar.getInstance()

            calendar.add(
                Calendar.DAY_OF_MONTH,
                -7
            )

            val limitDate =
                calendar.time

            val sdf =
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
                )

            val filtered =
                studies.filter {

                    try {

                        val studyDate =
                            sdf.parse(it.created)

                        studyDate != null &&
                                studyDate.after(limitDate)

                    } catch (e: Exception) {

                        false
                    }
                }

            processReport(
                filtered,
                "Mingguan"
            )
        }
    }

    private fun loadMonthlyReport() {

        viewModel.getAllStudyData { studies ->

            val calendar =
                Calendar.getInstance()

            calendar.add(
                Calendar.DAY_OF_MONTH,
                -30
            )

            val limitDate =
                calendar.time

            val sdf =
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
                )

            val filtered =
                studies.filter {

                    try {

                        val studyDate =
                            sdf.parse(it.created)

                        studyDate != null &&
                                studyDate.after(limitDate)

                    } catch (e: Exception) {

                        false
                    }
                }

            processReport(
                filtered,
                "Bulanan"
            )
        }
    }

    private fun processReport(
        studies: List<TimeStudy>,
        title: String
    ) {

        val totalStudy =
            studies.sumOf { it.time }

        val totalBreak =
            studies.sumOf { it.breakTime }

        val focus =
            ProgressUtils.hitungTingkatFokus(
                totalStudy,
                totalBreak
            )

        tvFocus.text = focus

        tvBreak.text =
            totalBreak.toString()

        tvAverage.text =
            ProgressUtils.formatDuration(
                if (studies.isEmpty())
                    0
                else
                    totalStudy / studies.size
            )

        viewModel.getTotalTarget { totalTarget ->

            val progress =
                if (totalTarget > 0)
                    ((totalStudy.toDouble() / totalTarget) * 100)
                        .toInt()
                        .coerceAtMost(100)
                else
                    0

            tvAchievement.text = "$progress%"
        }

        generateChart(title)
    }

    private fun generateChart(
        title: String
    ) {

        viewModel.getAllTypeStudy { types ->

            val entries = mutableListOf<PieEntry>()
            val laporanBaru = mutableListOf<LaporanModel>()

            var processed = 0

            if (types.isEmpty()) {

                adapter.updateData(emptyList())

                setupChart(
                    emptyList(),
                    title
                )

                return@getAllTypeStudy
            }

            for (item in types) {

                viewModel.getStudyStatistic(
                    item.id
                ) { totalStudy, totalBreak ->

                    processed++

                    if (totalStudy > 0) {

                        val fokus =
                            ProgressUtils.hitungTingkatFokus(
                                totalStudy,
                                totalBreak
                            )

                        entries.add(
                            PieEntry(
                                totalStudy.toFloat(),
                                item.title
                            )
                        )

                        laporanBaru.add(
                            LaporanModel(
                                fokus,
                                item.title,
                                ProgressUtils.formatDuration(
                                    totalStudy
                                )
                            )
                        )
                    }

                    if (processed == types.size) {

                        laporanBaru.sortByDescending {
                            ProgressUtils.timeToSeconds(
                                it.duration
                            )
                        }

                        adapter.updateData(
                            laporanBaru
                        )

                        setupChart(
                            entries,
                            title
                        )
                    }
                }
            }
        }
    }
}