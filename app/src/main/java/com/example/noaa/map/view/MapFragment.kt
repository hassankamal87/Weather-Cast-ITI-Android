package com.example.noaa.map.view

import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.noaa.R
import com.example.noaa.databinding.FragmentMapBinding
import com.example.noaa.homeactivity.view.HomeActivity
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Place
import com.example.noaa.model.Repo
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment() {


    lateinit var binding: FragmentMapBinding
    private var marker: Marker? = null
    private var coordinate: Coordinate? = null
    lateinit var sharedViewModel: SharedViewModel
//    lateinit var sharedPreferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        super.onDestroy()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleMapHandler()

        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context),
                ),
                ConcreteLocalSource.getInstance()
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]
        val kind = MapFragmentArgs.fromBundle(requireArguments()).kind
        binding.btnSaveLocation.setOnClickListener {
            if (sharedViewModel.checkConnection(requireContext())) {
                if (coordinate != null) {
                    if (kind == Constants.REGULAR) {
                        if (sharedViewModel.readStringFromSettingSP(
                                Constants.LANGUAGE,
                                requireContext()
                            ) == Constants.ARABIC
                        ) {
                            sharedViewModel.getWeatherData(coordinate!!, "ar")
                        } else {
                            sharedViewModel.getWeatherData(coordinate!!, "en")
                        }
                        sharedViewModel.writeFloatToSettingSP(
                            Constants.LATITUDE,
                            coordinate!!.latitude.toFloat(),
                            requireContext()
                        )
                        sharedViewModel.writeFloatToSettingSP(
                            Constants.LONGITUDE,
                            coordinate!!.longitude.toFloat(),
                            requireContext()
                        )
                        navigateBack()
                    } else {
                        var cityName = "UnKnown Location"
                        try {
                            val x = Geocoder(requireContext()).getFromLocation(
                                coordinate!!.latitude,
                                coordinate!!.longitude,
                                5
                            )

                            if (x != null && x[0].locality != null) {
                                cityName = "${x[0].countryName} / ${x[0].locality}"
                            } else if (x != null) {
                                cityName = "${x[0].countryName} / ${x[0].adminArea}"
                            }
                        } catch (_: Exception) {
                        }
                        sharedViewModel.insertPlaceToFav(
                            requireContext(),
                            Place(
                                cityName = cityName,
                                latitude = coordinate!!.latitude,
                                longitude = coordinate!!.longitude
                            )
                        )
                        navigateBack()
                    }
                } else {
                    Toast.makeText(requireContext(), "please pick location", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun googleMapHandler() {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { map ->
            map.setOnMapClickListener {
                marker?.remove()
                coordinate = Coordinate(it.latitude, it.longitude)
                marker = map.addMarker(
                    MarkerOptions()
                        .position(it)
                )
            }
        }
    }

    private fun navigateBack() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().commit()
        fragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.VISIBLE
    }

}