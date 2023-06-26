package com.example.noaa.home.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noaa.R
import com.example.noaa.databinding.FragmentHomeBinding
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModel
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModelFactory
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Repo
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.ApiState
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.material.slider.LabelFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var hourlyRecyclerAdapter: HourlyRecyclerAdapter
    private lateinit var dailyRecyclerAdapter: DailyRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iconAnimation()

        hourlyRecyclerAdapter = HourlyRecyclerAdapter()
        binding.rvHours.adapter = hourlyRecyclerAdapter

        dailyRecyclerAdapter = DailyRecyclerAdapter()
        binding.rvDays.adapter = dailyRecyclerAdapter
        val factory = HomeActivityViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context)
                )
            ), view.context.getSharedPreferences(
                Constants.SETTING,
                AppCompatActivity.MODE_PRIVATE
            )
        )

        homeActivityViewModel = ViewModelProvider(this, factory)[HomeActivityViewModel::class.java]

        homeActivityViewModel.coordinateLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: from fragment ${it.latitude}")
            Log.d(TAG, "onViewCreated: from fragment ${it.longitude}")
        }

        homeActivityViewModel.getWeatherData(Coordinate(28.0871, 30.7618), "en")

        lifecycleScope.launch(Dispatchers.IO) {
            homeActivityViewModel.weatherResponseStateFlow.collect {
                when (it) {
                    is ApiState.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.weatherResponse}")
                        withContext(Dispatchers.Main) {
                            setDataToViews(it.weatherResponse)
                        }
                    }

                    is ApiState.Loading -> {
                        // show progress bar

                    }

                    else -> {
                        Log.d(TAG, "onViewCreated: ${it}")

                    }
                }
            }
        }

    }

    private fun iconAnimation() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.place_changer)
        binding.ivWeather.startAnimation(animation)
    }

    /* fun onGetCoordinate(coordinate: Coordinate){

     }*/

    private fun setDataToViews(weatherResponse: WeatherResponse) {
        binding.apply {
            tvLocationName.text = weatherResponse.zoneName
            tvDate.text = fromUnixToString(weatherResponse.currentDay.dt)
            tvCurrentDegree.text = String.format("%.1f°C", weatherResponse.currentDay.temp)
            tvWeatherStatus.text = weatherResponse.currentDay.weather[0].description
            hourlyRecyclerAdapter.submitList(weatherResponse.hours)
            dailyRecyclerAdapter.submitList(weatherResponse.days.filterIndexed { index, _ -> index != 0 })
        }

    }

    private fun fromUnixToString(time: Int): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date = Date(time * 1000L)
        return sdf.format(date).uppercase(Locale.ROOT)
    }
}