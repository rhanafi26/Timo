package com.anggi.timo.repository

import com.anggi.timo.database.LaporanBelajarDao
import com.anggi.timo.laporan.LaporanBelajarEntity

class LaporanBelajarRepository(private val dao: LaporanBelajarDao) {

    fun getTotalFokusHarian(tanggal: String) = dao.getTotalFokusHarian(tanggal)
    fun getTotalIstirahatHarian(tanggal: String) = dao.getTotalIstirahatHarian(tanggal)

    fun getTotalFokusMingguan(startDate: String, endDate: String) = dao.getTotalFokusMingguan(startDate, endDate)
    fun getTotalIstirahatMingguan(startDate: String, endDate: String) = dao.getTotalIstirahatMingguan(startDate, endDate)

    fun getTotalFokusBulanan(bulan: String) = dao.getTotalFokusBulanan(bulan)
    fun getTotalIstirahatBulanan(bulan: String) = dao.getTotalIstirahatBulanan(bulan)

    fun getStatistikListHarian(tanggal: String) = dao.getStatistikListHarian(tanggal)
    fun getStatistikListMingguan(startDate: String, endDate: String) = dao.getStatistikListMingguan(startDate, endDate)
    fun getStatistikListBulanan(bulan: String) = dao.getStatistikListBulanan(bulan)

    suspend fun getCount(): Int = dao.getCount()

    suspend fun insert(laporan: LaporanBelajarEntity) { dao.insertLaporan(laporan) }
    suspend fun update(laporan: LaporanBelajarEntity) { dao.updateLaporan(laporan) }
    suspend fun delete(laporan: LaporanBelajarEntity) { dao.deleteLaporan(laporan) }

    suspend fun clearSemuaData() { dao.clearSemuaData() }
}