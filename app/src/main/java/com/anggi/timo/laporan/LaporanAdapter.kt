package com.anggi.timo.laporan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R

class LaporanAdapter(private val listLaporan: List<LaporanModel>) :
    RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAggregate: TextView = itemView.findViewById(R.id.aggregate)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jenis_belajar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLaporan[position]
        holder.tvAggregate.text = item.aggregate
        holder.tvTitle.text = item.title
        holder.tvDuration.text = item.duration
    }

    override fun getItemCount(): Int = listLaporan.size
}