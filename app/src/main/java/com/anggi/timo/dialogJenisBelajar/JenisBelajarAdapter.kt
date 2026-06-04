package com.anggi.timo.dialogJenisBelajar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.R

class JenisBelajarAdapter(
    private val items: List<Pair<String, String>>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<JenisBelajarAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvTitle: TextView =
            view.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_jenis_belajar,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = items[position]

        holder.tvTitle.text = item.second

        holder.itemView.setOnClickListener {
            onClick(item.first)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}