package com.example.noaa.setting.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.noaa.R
import com.example.noaa.databinding.FragmentSettingBinding
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Repo
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardsAnimation()

        sharedPreferences = view.context.getSharedPreferences(
            Constants.SETTING,
            AppCompatActivity.MODE_PRIVATE
        )
        setDefaultRadioButtons()
        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance()
            ),
            sharedPreferences
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]



        binding.radioGroupSettingLocation.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_map) {
                sharedViewModel.setLocationChoice(Constants.MAP)
                val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
                view.findNavController().navigate(action)
            } else {
                sharedViewModel.setLocationChoice(Constants.GPS)
                sharedViewModel.getLocation(requireContext())
            }
        }

        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_meter) {
                sharedPreferences.edit().putString(Constants.WIND_SPEED, Constants.METER_SEC).apply()
            } else {
                sharedPreferences.edit().putString(Constants.WIND_SPEED, Constants.MILE_HOUR).apply()
            }
        }


        binding.radioGroupSettingLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_english) {
                sharedPreferences.edit().putString(Constants.LANGUAGE, Constants.ENGLISH).apply()
            } else {
                sharedPreferences.edit().putString(Constants.LANGUAGE, Constants.ARABIC).apply()
            }
        }


        binding.radioGroupSettingNotification.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_enable_notification) {
                sharedPreferences.edit().putString(Constants.NOTIFICATION, Constants.ENABLE).apply()
            } else {
                sharedPreferences.edit().putString(Constants.NOTIFICATION, Constants.DISABLE).apply()
            }
        }


        binding.radioGroupSettingTemp.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_celsius) {
                sharedPreferences.edit().putString(Constants.TEMPERATURE, Constants.CELSIUS).apply()
            }else if(checkedId == R.id.radio_setting_kelvin){
                sharedPreferences.edit().putString(Constants.TEMPERATURE, Constants.KELVIN).apply()
            } else {
                sharedPreferences.edit().putString(Constants.TEMPERATURE, Constants.FAHRENHEIT).apply()
            }
        }
    }

    private fun cardsAnimation() {
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation)
        val animation2 =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation2)
        val animation3 =
            AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation3)
        binding.cvLocation.startAnimation(animation)
        binding.cvLanguage.startAnimation(animation)
        binding.cvWind.startAnimation(animation2)
        binding.cvNotification.startAnimation(animation2)
        binding.cvTemp.startAnimation(animation3)
    }

    private fun setDefaultRadioButtons(){
        if (sharedPreferences.getString(Constants.LOCATION, "null") == Constants.GPS) {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_gps)
        } else {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_map)
        }

        if (sharedPreferences.getString(Constants.WIND_SPEED, "null") == Constants.MILE_HOUR) {
            binding.radioGroupSettingWind.check(R.id.radio_setting_mile)
        } else {
            binding.radioGroupSettingWind.check(R.id.radio_setting_meter)
        }

        if (sharedPreferences.getString(Constants.LANGUAGE, Constants.ENGLISH) == Constants.ARABIC) {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_arabic)
        } else {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_english)
        }

        if (sharedPreferences.getString(Constants.NOTIFICATION, "null") == Constants.DISABLE) {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_disable_notification)
        } else {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_enable_notification)
        }

        if (sharedPreferences.getString(Constants.TEMPERATURE, "null") == Constants.KELVIN) {
            binding.radioGroupSettingTemp.check(R.id.radio_setting_kelvin)
        } else if(sharedPreferences.getString(Constants.TEMPERATURE, "null") == Constants.FAHRENHEIT){
            binding.radioGroupSettingTemp.check(R.id.radio_fahrenheit)
        }else {
            binding.radioGroupSettingTemp.check(R.id.radio_setting_celsius)
        }
    }
}