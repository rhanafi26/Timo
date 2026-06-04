package com.anggi.timo.Model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String
)

data class TypeStudy(
    var id: String = "",
    val title: String = "",
    val target: Int=0,
    val userId: String=""
)
data class TimeStudy(
    val time: Int = 0,
    val breakTime: Int = 0,
    val created: String = "",
    val userId: String = "",
    val typeStudyId: String = ""
)

data class LaporanStat(
    val fokus: String,
    val rataRata: String,
    val istirahat: Int,
    val pencapaian: String
)