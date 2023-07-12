package com.example.noaa.details.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noaa.R
import com.example.noaa.databinding.FragmentDetailsBinding
import com.example.noaa.details.viewmodel.DetailsViewModel
import com.example.noaa.details.viewmodel.DetailsViewModelFactory
import com.example.noaa.home.view.DailyRecyclerAdapter
import com.example.noaa.home.view.HourlyRecyclerAdapter
import com.example.noaa.homeactivity.view.HomeActivity
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.Coordinate
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var detailViewModel: DetailsViewModel
    private lateinit var hourlyRecyclerAdapter: HourlyRecyclerAdapter
    private lateinit var dailyRecyclerAdapter: DailyRecyclerAdapter

    override fun onStart() {
        super.onStart()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.GONE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val place = DetailsFragmentArgs.fromBundle(requireArguments()).place


        val factory = DetailsViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context),
                ),
                ConcreteLocalSource.getInstance(requireContext()),
                SettingSharedPref.getInstance(requireContext())
            )
        )
        detailViewModel = ViewModelProvider(this, factory)[DetailsViewModel::class.java]
        if (detailViewModel.readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC) {
            detailViewModel.getWeatherData(Coordinate(place.latitude, place.longitude), "ar")
        } else {
            detailViewModel.getWeatherData(Coordinate(place.latitude, place.longitude), "en")
        }

        dailyRecyclerAdapter = DailyRecyclerAdapter()
        hourlyRecyclerAdapter = HourlyRecyclerAdapter()
        binding.rvDaysDetails.adapter = dailyRecyclerAdapter
        binding.rvHoursDetails.adapter = hourlyRecyclerAdapter

        lifecycleScope.launch {
            detailViewModel.weatherResponseStateFlow.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.weatherResponse}")
                        withContext(Dispatchers.Main) {
                            setDataToViews(it.weatherResponse)
                        }
                    }

                    is ApiState.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.loadingLottieDetails.visibility = View.VISIBLE
                        }
                    }

                    else -> {
                        withContext(Dispatchers.Main) {
                            binding.loadingLottieDetails.visibility = View.GONE
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
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToViews(weatherResponse: WeatherResponse) {

        makeViewsVisible()
        Functions.setIcon(weatherResponse.current.weather[0].icon, binding.ivWeatherDetails)

        binding.apply {

            tvLocationNameDetails.text = Functions.setLocationNameByGeoCoder(weatherResponse, requireContext())
            tvWeatherStatusDetails.text = weatherResponse.current.weather[0].description
            tvDynamicPressureDetails.text =
                String.format("%d %s", weatherResponse.current.pressure, getString(R.string.hpa))
            tvDynamicHumidityDetails.text = String.format(
                "%d %s",
                weatherResponse.current.humidity,
                getString(R.string.percentage)
            )
            tvDynamicCloudDetails.text = String.format(
                "%d %s",
                weatherResponse.current.clouds,
                getString(R.string.percentage)
            )
            tvDynamicVioletDetails.text = String.format("%.1f", weatherResponse.current.uvi)
            tvDynamicVisibilityDetails.text =
                String.format("%d %s", weatherResponse.current.visibility, getString(R.string.m))

            if (detailViewModel.readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC) {
                tvDateDetails.text = Functions.fromUnixToString(weatherResponse.current.dt, "ar")
                tvSunRiseDetails.text = Functions.fromUnixToStringTime(weatherResponse.current.sunrise, "ar")
                tvSunSetDetails.text = Functions.fromUnixToStringTime(weatherResponse.current.sunset, "ar")
            } else {
                tvDateDetails.text = Functions.fromUnixToString(weatherResponse.current.dt, "en")
                tvSunRiseDetails.text = Functions.fromUnixToStringTime(weatherResponse.current.sunrise, "en")
                tvSunSetDetails.text = Functions.fromUnixToStringTime(weatherResponse.current.sunset, "en")
            }

            when (detailViewModel.readStringFromSettingSP(Constants.TEMPERATURE)) {
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

            when (detailViewModel.readStringFromSettingSP(Constants.WIND_SPEED)) {
                Constants.MILE_HOUR -> tvDynamicWindDetails.text = String.format(
                    "%.1f ${getString(R.string.mile_hour)}",
                    weatherResponse.current.wind_speed * 2.237
                )

                else -> tvDynamicWindDetails.text = String.format(
                    "%.1f ${getString(R.string.meter_sec)}",
                    weatherResponse.current.wind_speed
                )
            }

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
            sunRiseDetails.visibility = View.VISIBLE
            sunSetDetails.visibility = View.VISIBLE
            tvSunRiseDetails.visibility = View.VISIBLE
            tvSunSetDetails.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.VISIBLE
    }
}