package com.anggi.timo.ViewModel

import androidx.lifecycle.ViewModel
import com.anggi.timo.Model.TypeStudy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudyViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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





//    fun loadTypeStudies(userId: String) {
//        db.collection("target_belajar")
//            .whereEqualTo("userId", uid)
//            .get()
//            .addOnSuccessListener { documents ->
//
//                listDashboard.clear()
//
//                for (document in documents) {
//
//                    val title = document.getString("title") ?: ""
//                    val target = document.getLong("target") ?: 0
//
//                    listDashboard.add(
//                        DashboardModel(
//                            "A",
//                            title,
//                            StudyViewModel().formatDuration(target.toInt()),
//                            "0%"
//                        )
//                    )
//                }
//
//            }
//    }

//    fun insertTimeStudy(time: Int, userId: Int, typeId: Int) {
//        val item = TimeStudy(
//            id = System.currentTimeMillis().toInt(),
//            time = time,
//            created = System.currentTimeMillis(),
//            userId = userId,
//            typeStudyId = typeId
//        )
//        InMemoryRepository.addTimeStudy(item)
//        loadTimeStudies(userId)
//    }
}
