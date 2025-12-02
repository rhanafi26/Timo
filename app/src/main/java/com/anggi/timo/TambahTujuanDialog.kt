package com.anggi.timo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class TambahTujuanDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_tambah_tujuan, null)

        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etWaktu = view.findViewById<EditText>(R.id.etWaktu)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val judul = etJudul.text.toString()
            val waktu = etWaktu.text.toString()
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }
}
