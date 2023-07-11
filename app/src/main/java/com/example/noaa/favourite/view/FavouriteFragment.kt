package com.example.noaa.favourite.view


import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.R
import com.example.noaa.databinding.FragmentFavouriteBinding
import com.example.noaa.favourite.viewmodel.FavouriteViewModel
import com.example.noaa.favourite.viewmodel.FavouriteViewModelFactory
import com.example.noaa.model.Place
import com.example.noaa.model.Repo
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavouriteFragment : Fragment() {

    lateinit var binding: FragmentFavouriteBinding
    lateinit var favouriteRecyclerAdapter: FavouriteRecyclerAdapter
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var place: Place

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.fabAddFav.setOnClickListener {
            val action =
                FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment(Constants.FAVOURITE)
            view.findNavController().navigate(action)
        }

        val factory = FavouriteViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance(requireContext()),
                SettingSharedPref.getInstance(requireContext())
            )
        )
        favouriteViewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
        favouriteViewModel.getAllFavouritePlaces()


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
                val mediaPlayer = MediaPlayer.create(context, R.raw.deleted)
                mediaPlayer.start()

                mediaPlayer.setOnCompletionListener { mp ->
                    mp.release()
                }
                favouriteViewModel.deletePlaceFromFav(place)
                Snackbar.make(view, "deleting Location.... ", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        favouriteViewModel.insertPlaceToFav(place)
                    }
                    show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvFavourite)

        favouriteRecyclerAdapter = FavouriteRecyclerAdapter {
            // navigate to details fragment
            if (favouriteViewModel.checkConnection(requireContext())) {
                val action =
                    FavouriteFragmentDirections.actionFavouriteFragmentToDetailsFragment(it)
                view.findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.rvFavourite.adapter = favouriteRecyclerAdapter

        lifecycleScope.launch {
            favouriteViewModel.favouritePlacesStateFlow.collectLatest {
                favouriteRecyclerAdapter.submitList(it)
            }
        }
    }


}