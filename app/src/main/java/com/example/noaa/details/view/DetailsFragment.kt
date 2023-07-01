package com.example.noaa.details.view

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding
    lateinit var mapViewModel: SharedViewModel
    lateinit var hourlyRecyclerAdapter: HourlyRecyclerAdapter
    lateinit var dailyRecyclerAdapter: DailyRecyclerAdapter

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


        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context),
                ),
                ConcreteLocalSource.getInstance()
            ), view.context.getSharedPreferences(
                Constants.SETTING,
                AppCompatActivity.MODE_PRIVATE
            )
        )
        mapViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
        mapViewModel.getWeatherData(Coordinate(place.latitude, place.longitude), "en")

        dailyRecyclerAdapter = DailyRecyclerAdapter()
        hourlyRecyclerAdapter = HourlyRecyclerAdapter()
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
        Functions.setIcon(weatherResponse.currentDay.weather[0].icon, binding.ivWeatherDetails)

        makeViewsVisible()
        binding.apply {
            tvDateDetails.text = fromUnixToString(weatherResponse.currentDay.dt)
            tvCurrentDegreeDetails.text = String.format("%.1f°C", weatherResponse.currentDay.temp)
            tvWeatherStatusDetails.text = weatherResponse.currentDay.weather[0].description
            tvDynamicPressureDetails.text = "${weatherResponse.currentDay.pressure} hpa"
            tvDynamicHumidityDetails.text = "${weatherResponse.currentDay.humidity} %"
            tvDynamicWindDetails.text = "${weatherResponse.currentDay.wind_speed} %"
            tvDynamicCloudDetails.text = "${weatherResponse.currentDay.clouds} %"
            tvDynamicVioletDetails.text = "${weatherResponse.currentDay.uvi}"
            tvDynamicVisibilityDetails.text = "${weatherResponse.currentDay.visibility} m"
            hourlyRecyclerAdapter.submitList(weatherResponse.hours)
            dailyRecyclerAdapter.submitList(weatherResponse.days.filterIndexed { index, _ -> index != 0 })
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

    private fun fromUnixToString(time: Int): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date = Date(time * 1000L)
        return sdf.format(date).uppercase(Locale.ROOT)
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
                binding.tvLocationNameDetails.text = x[0].locality
                Log.d(TAG, "setLocationNameByGeoCoder: ${x[0].locality}")
            } else {
                binding.tvLocationNameDetails.text = weatherResponse.zoneName
            }
        }catch (e: Exception){
            binding.tvLocationNameDetails.text = weatherResponse.zoneName
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.binding.bottomNavigation.visibility = View.VISIBLE
    }
}