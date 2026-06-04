package com.anggi.timo.ViewModel

import androidx.lifecycle.ViewModel
import com.anggi.timo.Model.TimeStudy
import com.anggi.timo.Model.TypeStudy
import com.anggi.timo.utils.ProgressUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class StudyViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun tambahJenisBelajar(
        judul: String,
        waktu: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            onError("User belum login")
            return
        }

        val data = TypeStudy(
            title = judul,
            target = waktu,
            userId = uid
        )

        db.collection("tujuan")
            .add(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Terjadi kesalahan")
            }
    }

    fun saveTimeStudy(
        typeStudyId: String,
        studyTime: Int,
        breakTime: Int = 0
    ) {

        val uid = auth.currentUser?.uid ?: return
        val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", java.util.Locale.getDefault())
        val formattedDate = dateFormat.format(java.util.Date())
        val data = TimeStudy(
            time = studyTime,
            breakTime = breakTime,
            created = formattedDate,  // Now storing as formatted string
            userId = uid,
            typeStudyId = typeStudyId
        )

        db.collection("time_study")
            .add(data)
    }

    fun getTotalStudyTime(
        typeStudyId: String,
        callback: (Int) -> Unit
    ) {

        db.collection("time_study")
            .whereEqualTo("typeStudyId", typeStudyId)
            .get()
            .addOnSuccessListener { documents ->

                var total = 0

                for (document in documents) {
                    total += document.getLong("time")?.toInt() ?: 0
                }

                callback(total)
            }
            .addOnFailureListener {
                callback(0)
            }
    }

    fun getTodayStudyTime(
        callback: (Int) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        // Get today's date in the same format as stored
        val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
        val todayDate = dateFormat.format(java.util.Date())

        db.collection("time_study")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                var total = 0

                for (document in documents) {
                    val createdDate = document.getString("created") ?: ""
                    // Check if the created date starts with today's date
                    if (createdDate.startsWith(todayDate)) {
                        total += document.getLong("time")?.toInt() ?: 0
                    }
                }

                callback(total)
            }
            .addOnFailureListener {
                callback(0)
            }
    }

    fun getTotalTarget(
        callback: (Int) -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("tujuan")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->

                var totalTarget = 0

                for (document in documents) {
                    totalTarget += document.getLong("target")?.toInt() ?: 0
                }

                callback(totalTarget)
            }
            .addOnFailureListener {
                callback(0)
            }
    }

    fun getAverageFocus(
        callback: (String) -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("time_study")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->

                var totalScore = 0
                var count = 0

                for (document in documents) {

                    val time =
                        document.getLong("time")?.toInt() ?: 0

                    val focus =
                        ProgressUtils.hitungTingkatFokus(time)

                    totalScore += when (focus) {
                        "A" -> 5
                        "AB" -> 4
                        "B" -> 3
                        "BC" -> 2
                        "C" -> 1
                        else -> 0
                    }

                    count++
                }

                if (count == 0) {
                    callback("-")
                    return@addOnSuccessListener
                }

                val average = totalScore / count

                callback(
                    when (average) {
                        5 -> "A"
                        4 -> "AB"
                        3 -> "B"
                        2 -> "BC"
                        else -> "C"
                    }
                )
            }
            .addOnFailureListener {
                callback("-")
            }
    }

    fun getOverallProgress(
        callback: (String) -> Unit
    ) {

        getTodayStudyTime { totalStudy ->

            getTotalTarget { totalTarget ->

                callback(
                    ProgressUtils.hitungPersentase(
                        totalStudy,
                        totalTarget
                    )
                )
            }
        }
    }

    fun getAllStudyData(
        callback: (List<TimeStudy>) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("time_study")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { docs ->

                val list = mutableListOf<TimeStudy>()

                for (doc in docs) {
                    doc.toObject(TimeStudy::class.java)
                        .let { list.add(it) }
                }

                callback(list)
            }
    }

    fun getAllTypeStudy(
        callback: (List<TypeStudy>) -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("tujuan")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { docs ->

                val list = mutableListOf<TypeStudy>()

                for (doc in docs) {
                    val item =
                        doc.toObject(TypeStudy::class.java)

                    item.id = doc.id
                    list.add(item)
                }

                callback(list)
            }
    }

    fun getStudyStatistic(
        typeStudyId: String,
        callback: (Int, Int) -> Unit
    ) {

        db.collection("time_study")
            .whereEqualTo("typeStudyId", typeStudyId)
            .get()
            .addOnSuccessListener { documents ->

                var totalStudy = 0
                var totalBreak = 0

                for (doc in documents) {

                    totalStudy +=
                        doc.getLong("time")?.toInt() ?: 0

                    totalBreak +=
                        doc.getLong("breakTime")?.toInt() ?: 0
                }

                callback(
                    totalStudy,
                    totalBreak
                )
            }
            .addOnFailureListener {
                callback(0, 0)
            }
    }
}