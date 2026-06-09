package com.anggi.timo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.anggi.timo.ViewModel.StudyViewModel
import com.anggi.timo.utils.ProgressUtils

class TambahTujuanDialog(
    private val onSuccess: (() -> Unit)? = null
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_tambah_tujuan, null)

        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etWaktu = view.findViewById<EditText>(R.id.etWaktu)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        btnSimpan.setOnClickListener {
            val judul = etJudul.text.toString().trim()
            val waktu = etWaktu.text.toString().trim()
            if (judul.isEmpty()) {
                etJudul.error = "Judul tidak boleh kosong"
                return@setOnClickListener
            }

            if (waktu.isEmpty()) {
                etWaktu.error = "Waktu tidak boleh kosong"
                return@setOnClickListener
            }
            val waktuDetik = ProgressUtils.timeToSeconds(waktu)

            btnSimpan.isEnabled = false

            StudyViewModel().tambahJenisBelajar(
                judul = judul,
                waktu = waktuDetik,
                onSuccess = {
                    Toast.makeText(
                        requireContext(),
                        "Tujuan berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    onSuccess?.invoke()

                    dismiss()
                },
                onError = { error ->

                    btnSimpan.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        builder.setView(view)
        return builder.create()
    }
}
