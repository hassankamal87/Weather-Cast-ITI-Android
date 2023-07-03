package com.example.noaa.details.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noaa.R
import com.example.noaa.databinding.FragmentDetailsBinding
import com.example.noaa.home.view.DailyRecyclerAdapter
import com.example.noaa.home.view.HourlyRecyclerAdapter
import com.example.noaa.homeactivity.view.HomeActivity
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding
    lateinit var mapViewModel: SharedViewModel
    lateinit var hourlyRecyclerAdapter: HourlyRecyclerAdapter
    lateinit var dailyRecyclerAdapter: DailyRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences

    override fun onStart() {
        super.onStart()
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
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val place = DetailsFragmentArgs.fromBundle(requireArguments()).place


        sharedPreferences =
            view.context.getSharedPreferences(Constants.SETTING, AppCompatActivity.MODE_PRIVATE)
        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context),
                ),
                ConcreteLocalSource.getInstance()
            ), sharedPreferences
        )
        mapViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
        if (sharedPreferences.getString(Constants.LANGUAGE, "null") == Constants.ARABIC) {
            mapViewModel.getWeatherData(Coordinate(place.latitude, place.longitude), "ar")
        }else{
            mapViewModel.getWeatherData(Coordinate(place.latitude, place.longitude), "en")
        }

        dailyRecyclerAdapter = DailyRecyclerAdapter(sharedPreferences)
        hourlyRecyclerAdapter = HourlyRecyclerAdapter(sharedPreferences)
        binding.rvDaysDetails.adapter = dailyRecyclerAdapter
        binding.rvHoursDetails.adapter = hourlyRecyclerAdapter

        lifecycleScope.launch {
            mapViewModel.weatherResponseStateFlow.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.weatherResponse}")
                        withContext(Dispatchers.Main) {
                            setDataToViews(it.weatherResponse)
                        }
                    }

                    is ApiState.Loading -> {
                        // show progress bar
                        binding.loadingLottieDetails.visibility = View.VISIBLE
                    }

                    else -> {
                        Log.d(TAG, "onViewCreated: $it")

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToViews(weatherResponse: WeatherResponse) {
        setLocationNameByGeoCoder(weatherResponse)
        Functions.setIcon(weatherResponse.current.weather[0].icon, binding.ivWeatherDetails)

        makeViewsVisible()
        binding.apply {
            if (sharedPreferences.getString(Constants.LANGUAGE, "null") == Constants.ARABIC) {
                tvDateDetails.text = Functions.fromUnixToString(weatherResponse.current.dt, "ar")
            } else {
                tvDateDetails.text = Functions.fromUnixToString(weatherResponse.current.dt, "en")
            }
            when (sharedPreferences.getString(Constants.TEMPERATURE, "null")) {
                Constants.KELVIN -> tvCurrentDegreeDetails.text = String.format(
                    "%.1f°${getString(R.string.k)}",
                    weatherResponse.current.temp + 273.15
                )

                Constants.FAHRENHEIT -> tvCurrentDegreeDetails.text = String.format(
                    "%.1f°${getString(R.string.f)}",
                    weatherResponse.current.temp * 9 / 5 + 32
                )

                else -> tvCurrentDegreeDetails.text =
                    String.format("%.1f°${getString(R.string.c)}", weatherResponse.current.temp)
            }
            tvWeatherStatusDetails.text = weatherResponse.current.weather[0].description
            tvDynamicPressureDetails.text =
                String.format("%d %s", weatherResponse.current.pressure, getString(R.string.hpa))
            tvDynamicHumidityDetails.text = String.format(
                "%d %s",
                weatherResponse.current.humidity,
                getString(R.string.percentage)
            )
            when (sharedPreferences.getString(Constants.WIND_SPEED, "null")) {
                Constants.MILE_HOUR -> tvDynamicWindDetails.text = String.format(
                    "%.1f ${getString(R.string.mile_hour)}",
                    weatherResponse.current.wind_speed * 2.237
                )

                else -> tvDynamicWindDetails.text = String.format(
                    "%.1f ${getString(R.string.meter_sec)}",
                    weatherResponse.current.wind_speed
                )
            }
            tvDynamicCloudDetails.text = String.format(
                "%d %s",
                weatherResponse.current.clouds,
                getString(R.string.percentage)
            )
            tvDynamicVioletDetails.text = String.format("%.1f", weatherResponse.current.uvi)
            tvDynamicVisibilityDetails.text =
                String.format("%d %s", weatherResponse.current.visibility, getString(R.string.m))
            hourlyRecyclerAdapter.submitList(weatherResponse.hourly)
            dailyRecyclerAdapter.submitList(weatherResponse.daily.filterIndexed { index, _ -> index != 0 })
        }

    }

    private fun makeViewsVisible() {
        binding.apply {
            loadingLottieDetails.visibility = View.GONE
            tvLocationNameDetails.visibility = View.VISIBLE
            ivWeatherDetails.visibility = View.VISIBLE
            tvDateDetails.visibility = View.VISIBLE
            tvCurrentDegreeDetails.visibility = View.VISIBLE
            tvWeatherStatusDetails.visibility = View.VISIBLE
            cvDetailsDetails.visibility = View.VISIBLE
            rvDaysDetails.visibility = View.VISIBLE
            rvHoursDetails.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLocationNameByGeoCoder(weatherResponse: WeatherResponse) {
        try {
            val x =
                Geocoder(requireContext()).getFromLocation(
                    weatherResponse.lat,
                    weatherResponse.lon,
                    5
                )

            if (x != null && x[0].locality != null) {
                binding.tvLocationNameDetails.text = x[0].locality
                Log.d(TAG, "setLocationNameByGeoCoder: ${x[0].locality}")
            } else {
                binding.tvLocationNameDetails.text = weatherResponse.timezone
            }
        } catch (e: Exception) {
            binding.tvLocationNameDetails.text = weatherResponse.timezone
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.VISIBLE
    }
}