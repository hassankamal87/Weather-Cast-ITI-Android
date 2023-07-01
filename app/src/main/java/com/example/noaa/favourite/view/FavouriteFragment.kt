package com.example.noaa.favourite.view

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.FragmentFavouriteBinding
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Place
import com.example.noaa.model.Repo
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavouriteFragment : Fragment() {

    lateinit var binding: FragmentFavouriteBinding
    lateinit var favouriteRecyclerAdapter: FavouriteRecyclerAdapter
    lateinit var sharedViewModel: SharedViewModel
    lateinit var place: Place
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.fabAddFav.setOnClickListener {
            val action = FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment(Constants.FAVOURITE)
            view.findNavController().navigate(action)
        }

        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance()
            ), view.context.getSharedPreferences(
                Constants.SETTING,
                AppCompatActivity.MODE_PRIVATE
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]
        sharedViewModel.getAllFavouritePlaces(requireContext())



        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                place = favouriteRecyclerAdapter.currentList[position]
                sharedViewModel.deletePlaceFromFav(view.context, place)
                /*Snackbar.make(view, "deleting Location.... ", Snackbar.LENGTH_LONG).apply {
                    setAction("Confirm") {
                        sharedViewModel.insertPlaceToFav(view.context, place)
                    }
                    show()
                }*/
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvFavourite)

        favouriteRecyclerAdapter = FavouriteRecyclerAdapter(){
            // navigate to details fragment
            val action = FavouriteFragmentDirections.actionFavouriteFragmentToDetailsFragment(it)
            view.findNavController().navigate(action)
        }
        binding.rvFavourite.adapter = favouriteRecyclerAdapter

        lifecycleScope.launch {
            sharedViewModel.favouritePlacesStateFlow.collectLatest {
                favouriteRecyclerAdapter.submitList(it)
            }
        }
    }





}