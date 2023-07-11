package com.example.noaa.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.ItemFavouriteBinding
import com.example.noaa.model.Place

class FavouriteRecyclerAdapter(val onItemClick: (place: Place) -> Unit) :
    ListAdapter<Place, FavouriteRecyclerAdapter.FavouriteViewHolder>(RecyclerDiffUtilPlace()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return FavouriteViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class FavouriteViewHolder(private val binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(currentItem: Place) {

            binding.tvLocationNameFavourites.text = currentItem.cityName
            itemView.setOnClickListener {
                onItemClick(currentItem)
            }
        }
    }


}

class RecyclerDiffUtilPlace : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}