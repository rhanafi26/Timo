package com.anggi.timo.dialogJenisBelajar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PilihJenisBelajarDialog(
    private val onSelected: (documentId: String) -> Unit
) : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val listJenisBelajar = mutableListOf<Pair<String, String>>()
    private lateinit var adapter: JenisBelajarAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val view: View = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_pilih_jenis_belajar, null)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.recyclerJenisBelajar)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        adapter = JenisBelajarAdapter(listJenisBelajar) { documentId ->
            onSelected(documentId)
            dismiss()
        }

        recyclerView.adapter = adapter

        loadData()

        return androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun loadData() {

        val uid = auth.currentUser?.uid ?: return

        db.collection("tujuan")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->

                listJenisBelajar.clear()

                for (document in documents) {

                    val title =
                        document.getString("title") ?: ""

                    listJenisBelajar.add(
                        Pair(document.id, title)
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }
}