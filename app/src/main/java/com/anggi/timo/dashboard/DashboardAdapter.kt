package com.anggi.timo.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R

class DashboardAdapter(
    private val listDashboard: List<DashboardModel>
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAggregate: TextView = itemView.findViewById(R.id.aggregate)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvPercent: TextView = itemView.findViewById(R.id.tvPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jenis_belajar_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listDashboard[position]
        holder.tvAggregate.text = item.aggregate
        holder.tvTitle.text = item.title
        holder.tvDuration.text = item.duration
        holder.tvPercent.text = item.presentation
    }

    override fun getItemCount(): Int = listDashboard.size
}
