package com.anggi.timo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anggi.timo.laporan.LaporanBelajarEntity
import com.anggi.timo.laporan.StatistikPelajaran
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanBelajarDao {

    @Query("SELECT COALESCE(SUM(durasiFokus), 0) FROM tabel_laporan_belajar WHERE tanggal = :tanggal")
    fun getTotalFokusHarian(tanggal: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durasiIstirahat), 0) FROM tabel_laporan_belajar WHERE tanggal = :tanggal")
    fun getTotalIstirahatHarian(tanggal: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durasiFokus), 0) FROM tabel_laporan_belajar WHERE tanggal BETWEEN :startDate AND :endDate")
    fun getTotalFokusMingguan(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durasiIstirahat), 0) FROM tabel_laporan_belajar WHERE tanggal BETWEEN :startDate AND :endDate")
    fun getTotalIstirahatMingguan(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durasiFokus), 0) FROM tabel_laporan_belajar WHERE tanggal LIKE :bulan || '%'")
    fun getTotalFokusBulanan(bulan: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durasiIstirahat), 0) FROM tabel_laporan_belajar WHERE tanggal LIKE :bulan || '%'")
    fun getTotalIstirahatBulanan(bulan: String): Flow<Int>

    @Query("SELECT jenisBelajar, SUM(durasiFokus) as totalFokus, SUM(durasiIstirahat) as totalIstirahat, (SUM(durasiFokus) + SUM(durasiIstirahat)) as totalBelajar FROM tabel_laporan_belajar WHERE tanggal = :tanggal GROUP BY jenisBelajar ORDER BY totalFokus DESC")
    fun getStatistikListHarian(tanggal: String): Flow<List<StatistikPelajaran>>

    @Query("SELECT jenisBelajar, SUM(durasiFokus) as totalFokus, SUM(durasiIstirahat) as totalIstirahat, (SUM(durasiFokus) + SUM(durasiIstirahat)) as totalBelajar FROM tabel_laporan_belajar WHERE tanggal BETWEEN :startDate AND :endDate GROUP BY jenisBelajar ORDER BY totalFokus DESC")
    fun getStatistikListMingguan(startDate: String, endDate: String): Flow<List<StatistikPelajaran>>

    @Query("SELECT jenisBelajar, SUM(durasiFokus) as totalFokus, SUM(durasiIstirahat) as totalIstirahat, (SUM(durasiFokus) + SUM(durasiIstirahat)) as totalBelajar FROM tabel_laporan_belajar WHERE tanggal LIKE :bulan || '%' GROUP BY jenisBelajar ORDER BY totalFokus DESC")
    fun getStatistikListBulanan(bulan: String): Flow<List<StatistikPelajaran>>

    @Query("SELECT COUNT(*) FROM tabel_laporan_belajar")
    suspend fun getCount(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaporan(laporan: LaporanBelajarEntity)

    @Update
    suspend fun updateLaporan(laporan: LaporanBelajarEntity)

    @Delete
    suspend fun deleteLaporan(laporan: LaporanBelajarEntity)

    @Query("DELETE FROM tabel_laporan_belajar")
    suspend fun clearSemuaData()
}