package com.example.noaa.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noaa.R
import com.example.noaa.databinding.FragmentHomeBinding
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Repo
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.ApiState
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var hourlyRecyclerAdapter: HourlyRecyclerAdapter
    private lateinit var dailyRecyclerAdapter: DailyRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iconAnimation()


        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance(requireContext()),
                SettingSharedPref.getInstance(requireContext())
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]

        hourlyRecyclerAdapter = HourlyRecyclerAdapter()
        binding.rvHours.adapter = hourlyRecyclerAdapter
        dailyRecyclerAdapter = DailyRecyclerAdapter()
        binding.rvDays.adapter = dailyRecyclerAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            sharedViewModel.weatherResponseStateFlow.collect {
                when (it) {
                    is ApiState.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.weatherResponse}")
                        withContext(Dispatchers.Main) {
                            setDataToViews(it.weatherResponse)
                        }
                        if (sharedViewModel.checkConnection(requireContext())) {
                            sharedViewModel.insertCashedData(it.weatherResponse)
                        }
                    }

                    is ApiState.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.loadingLottie.visibility = View.VISIBLE
                        }
                    }

                    else -> {
                        withContext(Dispatchers.Main) {
                            binding.loadingLottie.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                (it as ApiState.Failure).errMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        //for near me iconBtn
        if (sharedViewModel.readStringFromSettingSP(Constants.LOCATION) == Constants.MAP) {
            binding.ivNearMe.visibility = View.VISIBLE
        }
        binding.ivNearMe.setOnClickListener {
            if (sharedViewModel.checkConnection(requireContext())) {
                binding.prProgress.visibility = View.VISIBLE
                sharedViewModel.writeStringToSettingSP(
                    Constants.LOCATION,
                    Constants.GPS
                )
                sharedViewModel.getLocation(view.context)
                binding.ivNearMe.visibility = View.GONE
            } else {
                Toast.makeText(
                    requireContext(),
                    "No Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun iconAnimation() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.place_changer)
        binding.ivWeather.startAnimation(animation)
    }


    @SuppressLint("SetTextI18n")
    private fun setDataToViews(weatherResponse: WeatherResponse) {
        makeViewsVisible()
        Functions.setIcon(weatherResponse.current.weather[0].icon, binding.ivWeather)

        binding.apply {

            tvLocationName.text = Functions.setLocationNameByGeoCoder(weatherResponse, requireContext())
            tvWeatherStatus.text = weatherResponse.current.weather[0].description
            tvDynamicPressure.text = String.format("%d %s", weatherResponse.current.pressure, getString(R.string.hpa))
            tvDynamicHumidity.text = String.format("%d %s", weatherResponse.current.humidity, getString(R.string.percentage))
            tvDynamicCloud.text = String.format("%d %s", weatherResponse.current.clouds, getString(R.string.percentage))
            tvDynamicViolet.text = String.format("%.1f", weatherResponse.current.uvi)
            tvDynamicVisibility.text = String.format("%d %s", weatherResponse.current.visibility, getString(R.string.m))

            if (sharedViewModel.readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC) {
                tvDate.text = Functions.fromUnixToString(weatherResponse.current.dt, "ar")
            } else {
                tvDate.text = Functions.fromUnixToString(weatherResponse.current.dt, "en")
            }

            when (sharedViewModel.readStringFromSettingSP(Constants.TEMPERATURE)) {
                Constants.KELVIN -> tvCurrentDegree.text = String.format(
                    "%.1f°${getString(R.string.k)}",
                    weatherResponse.current.temp + 273.15
                )

                Constants.FAHRENHEIT -> tvCurrentDegree.text = String.format(
                    "%.1f°${getString(R.string.f)}",
                    weatherResponse.current.temp * 9 / 5 + 32
                )

                else -> tvCurrentDegree.text =
                    String.format("%.1f°${getString(R.string.c)}", weatherResponse.current.temp)
            }

            when (sharedViewModel.readStringFromSettingSP(Constants.WIND_SPEED)) {
                Constants.MILE_HOUR -> tvDynamicWind.text = String.format(
                    "%.1f ${getString(R.string.mile_hour)}",
                    weatherResponse.current.wind_speed * 2.237
                )

                else -> tvDynamicWind.text = String.format(
                    "%.1f ${getString(R.string.meter_sec)}",
                    weatherResponse.current.wind_speed
                )
            }

            hourlyRecyclerAdapter.submitList(weatherResponse.hourly)
            dailyRecyclerAdapter.submitList(weatherResponse.daily.filterIndexed { index, _ -> index != 0 }
                .sortedWith(compareBy { it.dt }))
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
}