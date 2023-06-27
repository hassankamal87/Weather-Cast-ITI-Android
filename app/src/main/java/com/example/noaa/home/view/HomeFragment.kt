package com.example.noaa.home.view

import android.annotation.SuppressLint
import android.location.Geocoder
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
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Repo
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.ApiState
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedViewModel: SharedViewModel
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
        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context)
                )
            ), view.context.getSharedPreferences(
                Constants.SETTING,
                AppCompatActivity.MODE_PRIVATE
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]



        lifecycleScope.launch(Dispatchers.IO) {
            sharedViewModel.weatherResponseStateFlow.collect {
                when (it) {
                    is ApiState.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.weatherResponse}")
                        withContext(Dispatchers.Main) {
                            setDataToViews(it.weatherResponse)
                        }
                    }

                    is ApiState.Loading -> {
                        // show progress bar
                        binding.loadingLottie.visibility = View.VISIBLE
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


    @SuppressLint("SetTextI18n")
    private fun setDataToViews(weatherResponse: WeatherResponse) {
        setLocationNameByGeoCoder(weatherResponse)
        Functions.setIcon(weatherResponse.currentDay.weather[0].icon, binding.ivWeather)

        makeViewsVisible()
        binding.apply {
            tvDate.text = fromUnixToString(weatherResponse.currentDay.dt)
            tvCurrentDegree.text = String.format("%.1f°C", weatherResponse.currentDay.temp)
            tvWeatherStatus.text = weatherResponse.currentDay.weather[0].description
            tvDynamicPressure.text = "${weatherResponse.currentDay.pressure} hpa"
            tvDynamicHumidity.text = "${weatherResponse.currentDay.humidity} %"
            tvDynamicWind.text = "${weatherResponse.currentDay.wind_speed} %"
            tvDynamicCloud.text = "${weatherResponse.currentDay.clouds} %"
            tvDynamicViolet.text = "${weatherResponse.currentDay.uvi}"
            tvDynamicVisibility.text = "${weatherResponse.currentDay.visibility} m"
            hourlyRecyclerAdapter.submitList(weatherResponse.hours)
            dailyRecyclerAdapter.submitList(weatherResponse.days.filterIndexed { index, _ -> index != 0 })
        }

    }

    private fun fromUnixToString(time: Int): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date = Date(time * 1000L)
        return sdf.format(date).uppercase(Locale.ROOT)
    }

    private fun makeViewsVisible() {
        binding.apply {
            loadingLottie.visibility = View.GONE
            tvLocationName.visibility = View.VISIBLE
            ivWeather.visibility = View.VISIBLE
            tvDate.visibility = View.VISIBLE
            tvCurrentDegree.visibility = View.VISIBLE
            tvWeatherStatus.visibility = View.VISIBLE
            cvDetails.visibility = View.VISIBLE
            rvDays.visibility = View.VISIBLE
            rvHours.visibility = View.VISIBLE
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setLocationNameByGeoCoder(weatherResponse: WeatherResponse) {
        val x =
            Geocoder(requireContext()).getFromLocation(
                weatherResponse.latitude,
                weatherResponse.longitude,
                5
            )
        try {
            if (x != null) {
                binding.tvLocationName.text =
                    x[0].countryName + "/" + x[0].adminArea.replace("Governorate", "")
            }
        } catch (exception: Exception) {
            binding.tvLocationName.text = weatherResponse.zoneName
        }
    }
}