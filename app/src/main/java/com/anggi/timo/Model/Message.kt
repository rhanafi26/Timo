package com.anggi.timo.Modelpackage

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderColor: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
