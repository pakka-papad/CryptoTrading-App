package com.example.cryptotrading.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotrading.R

class FavouritesAdapter(private val listener: IFavouritesAdapter): RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {
    private var favourites: ArrayList<String> = ArrayList()

    inner class FavouritesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cryptoName: TextView = itemView.findViewById(R.id.crypto_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_card, parent, false)
        val viewHolder = FavouritesViewHolder(view)
        viewHolder.cryptoName.setOnClickListener {
            //Open Details Activity(Main Activity) and pass String through an Intent
            listener.onItemClicked(favourites[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.cryptoName.text = favourites[position]
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    fun updateRecyclerView(newFavourites: ArrayList<String>) {
        favourites.clear()
        favourites.addAll(newFavourites)
        notifyDataSetChanged()
    }
}

interface IFavouritesAdapter{
    fun onItemClicked(cryptoName: String)
}