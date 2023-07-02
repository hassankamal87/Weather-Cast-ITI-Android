package com.example.noaa.home.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import com.example.noaa.services.db.ConcreteLocalSource
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
    private lateinit var sharedPreferences: SharedPreferences
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



        sharedPreferences =
            view.context.getSharedPreferences(Constants.SETTING, AppCompatActivity.MODE_PRIVATE)
        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance()
            ), sharedPreferences
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]

        hourlyRecyclerAdapter = HourlyRecyclerAdapter(sharedPreferences)
        binding.rvHours.adapter = hourlyRecyclerAdapter
        dailyRecyclerAdapter = DailyRecyclerAdapter(sharedPreferences)
        binding.rvDays.adapter = dailyRecyclerAdapter

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

        //for near me icon
        if (sharedPreferences.getString(Constants.LOCATION, "") == Constants.MAP
        ) {
            binding.ivNearMe.visibility = View.VISIBLE
        }
        binding.ivNearMe.setOnClickListener {
            binding.prProgress.visibility = View.VISIBLE
            sharedViewModel.setLocationChoice(Constants.GPS)
            sharedViewModel.getLocation(view.context)
            binding.ivNearMe.visibility = View.GONE
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
            if (sharedPreferences.getString(Constants.LANGUAGE, "null") == Constants.ARABIC) {
                tvDate.text = Functions.fromUnixToString(weatherResponse.currentDay.dt, "ar")
            }else{
                tvDate.text = Functions.fromUnixToString(weatherResponse.currentDay.dt, "en")
            }
            when (sharedPreferences.getString(Constants.TEMPERATURE, "null")) {
                Constants.KELVIN -> tvCurrentDegree.text = String.format(
                    "%.1f°${getString(R.string.k)}",
                    weatherResponse.currentDay.temp + 273.15
                )

                Constants.FAHRENHEIT -> tvCurrentDegree.text = String.format(
                    "%.1f°${getString(R.string.f)}",
                    weatherResponse.currentDay.temp * 9 / 5 + 32
                )

                else -> tvCurrentDegree.text =
                    String.format("%.1f°${getString(R.string.c)}", weatherResponse.currentDay.temp)
            }
            tvWeatherStatus.text = weatherResponse.currentDay.weather[0].description
            tvDynamicPressure.text =
                String.format("%d %s", weatherResponse.currentDay.pressure, getString(R.string.hpa))
            tvDynamicHumidity.text = String.format(
                "%d %s",
                weatherResponse.currentDay.humidity,
                getString(R.string.percentage)
            )
            when (sharedPreferences.getString(Constants.WIND_SPEED, "null")) {
                Constants.MILE_HOUR -> tvDynamicWind.text = String.format(
                    "%.1f ${getString(R.string.mile_hour)}",
                    weatherResponse.currentDay.wind_speed * 2.237
                )

                else -> tvDynamicWind.text = String.format(
                    "%.1f ${getString(R.string.meter_sec)}",
                    weatherResponse.currentDay.wind_speed
                )
            }

            tvDynamicCloud.text = String.format("%d %s", weatherResponse.currentDay.clouds, getString(R.string.percentage))
            tvDynamicViolet.text = String.format("%.1f", weatherResponse.currentDay.uvi)
            tvDynamicVisibility.text = String.format("%d %s", weatherResponse.currentDay.visibility, getString(R.string.m))
            hourlyRecyclerAdapter.submitList(weatherResponse.hours)
            dailyRecyclerAdapter.submitList(
                weatherResponse.days
                    .filterIndexed { index, _ -> index != 0 }
                    .sortedWith(compareBy { it.dt })
                    //.sortedBy { day -> weatherResponse.days.indexOf(day) }
            )
        }

    }

    private fun makeViewsVisible() {
        binding.apply {
            binding.prProgress.visibility = View.GONE
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
        try {
            val x =
                Geocoder(requireContext()).getFromLocation(
                    weatherResponse.latitude,
                    weatherResponse.longitude,
                    5
                )

            if (x != null && x[0].locality != null) {
                binding.tvLocationName.text = x[0].locality
                Log.d(TAG, "setLocationNameByGeoCoder: ${x[0].locality}")
            } else {
                binding.tvLocationName.text = weatherResponse.zoneName
            }
        } catch (e: Exception) {
            binding.tvLocationName.text = weatherResponse.zoneName
        }
    }
}