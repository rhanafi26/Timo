package com.anggi.timo.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

object UserManager {

    private const val PREFS_NAME = "community_user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_COLOR = "user_color"

    private val colors = listOf(
        "#FF5722", "#E91E63", "#9C27B0", "#673AB7",
        "#3F51B5", "#2196F3", "#009688", "#4CAF50",
        "#FF9800", "#795548", "#607D8B", "#F44336"
    )

    private val adjectives = listOf(
        "Kreatif", "Ceria", "Rajin", "Santai", "Hebat",
        "Keren", "Unik", "Lincah", "Tenang", "Optimis",
        "Bijak", "Pintar", "Ramah", "Gembira", "Semangat"
    )

    private val nouns = listOf(
        "Panda", "Kucing", "Kelinci", "Koala", "Rubah",
        "Beruang", "Pinguin", "Harimau", "Gajah", "Jerapah",
        "Lumba", "Kura", "Cendrawasih", "Merak", "Elang"
    )

    fun initUser(context: Context): UserData {
        val prefs = getPrefs(context)
        val existingId = prefs.getString(KEY_USER_ID, null)

        if (existingId != null) {
            return UserData(
                id = existingId,
                name = prefs.getString(KEY_USER_NAME, "Anonymous")!!,
                color = prefs.getString(KEY_USER_COLOR, "#FF5722")!!
            )
        }
        val userId = UUID.randomUUID().toString()
        val userName = generateRandomName()
        val userColor = colors.random()

        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_USER_COLOR, userColor)
            .apply()

        return UserData(id = userId, name = userName, color = userColor)
    }

    fun getUserData(context: Context): UserData {
        val prefs = getPrefs(context)
        return UserData(
            id = prefs.getString(KEY_USER_ID, "")!!,
            name = prefs.getString(KEY_USER_NAME, "Anonymous")!!,
            color = prefs.getString(KEY_USER_COLOR, "#FF5722")!!
        )
    }

    private fun generateRandomName(): String {
        val adj = adjectives.random()
        val noun = nouns.random()
        return "$adj $noun"
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

data class UserData(
    val id: String,
    val name: String,
    val color: String
)