package com.example.noaa.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.ItemFavouriteBinding
import com.example.noaa.model.Place
import com.google.android.material.snackbar.Snackbar

class FavouriteRecyclerAdapter(val onItemClick: (place: Place)-> Unit) : ListAdapter<Place, FavouriteRecyclerAdapter.FavouriteViewHolder>(RecyclerDiffUtilPlace()) {

    lateinit var binding: ItemFavouriteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return FavouriteViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentItem = getItem(position)

        binding.tvLocationNameFavourites.text = currentItem.cityName
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    inner class FavouriteViewHolder(private val binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root)



}

class RecyclerDiffUtilPlace : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}