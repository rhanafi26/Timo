package com.anggi.timo.laporan

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tabel_laporan_belajar",
    indices = [Index(value = ["tanggal"])]
)
data class LaporanBelajarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: String,
    val jenisBelajar: String,
    val durasiFokus: Int,
    val durasiIstirahat: Int
)