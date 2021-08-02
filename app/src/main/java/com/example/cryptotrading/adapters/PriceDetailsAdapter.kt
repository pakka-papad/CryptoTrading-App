package com.example.cryptotrading.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotrading.R
import com.example.cryptotrading.models.DayStats

class PriceDetailsAdapter: RecyclerView.Adapter<PriceDetailsAdapter.PriceDetailsViewHolder>() {

    private var detailsList: ArrayList<DayStats> = ArrayList()

    inner class PriceDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.date)
        val highest: TextView = itemView.findViewById(R.id.highest)
        val lowest: TextView = itemView.findViewById(R.id.lowest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stats_of_day,parent,false)
        return PriceDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PriceDetailsViewHolder, position: Int) {
        holder.date.text = detailsList[position].date
        holder.highest.text = detailsList[position].highest
        holder.lowest.text = detailsList[position].lowest
    }

    override fun getItemCount(): Int {
        return detailsList.size
    }

    fun updateRecyclerView(newList: ArrayList<DayStats>){
        detailsList.clear()
        detailsList.addAll(newList)
        notifyDataSetChanged()
    }
}