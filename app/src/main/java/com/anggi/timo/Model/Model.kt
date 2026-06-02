package com.anggi.timo.Model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String
)

data class TypeStudy(
    val title: String,
    val target: Int,
    val userId: String,
    )

data class TimeStudy(
    val id: Int,
    val time: Int,
    val breakTime: Int,
    val created: Long,
    val userId: Int,
    val typeStudyId: Int
)
