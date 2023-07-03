package com.example.noaa.setting.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
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
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import com.google.android.gms.location.LocationServices
import java.util.Locale


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel

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

        val factory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context)),
                ConcreteLocalSource.getInstance()
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]

        setDefaultRadioButtons()

        binding.radioGroupSettingLocation.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_map) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LOCATION,
                    Constants.MAP,
                    requireContext()
                )
                val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
                view.findNavController().navigate(action)
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LOCATION,
                    Constants.GPS,
                    requireContext()
                )
                sharedViewModel.getLocation(requireContext())
            }
        }

        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_meter) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.WIND_SPEED,
                    Constants.METER_SEC,
                    requireContext()
                )
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.WIND_SPEED,
                    Constants.MILE_HOUR,
                    requireContext()
                )
            }
        }


        binding.radioGroupSettingLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_arabic) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LANGUAGE,
                    Constants.ARABIC,
                    requireContext()
                )
                Functions.changeLanguage(requireActivity(), "ar")
                restartApplication()
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LANGUAGE,
                    Constants.ENGLISH,
                    requireContext()
                )
                Functions.changeLanguage(requireActivity(), "en")
                restartApplication()
            }
        }


        binding.radioGroupSettingNotification.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_enable_notification) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.NOTIFICATION,
                    Constants.ENABLE,
                    requireContext()
                )
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.NOTIFICATION,
                    Constants.DISABLE,
                    requireContext()
                )
            }
        }


        binding.radioGroupSettingTemp.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_celsius) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.TEMPERATURE,
                    Constants.CELSIUS,
                    requireContext()
                )
            } else if (checkedId == R.id.radio_setting_kelvin) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.TEMPERATURE,
                    Constants.KELVIN,
                    requireContext()
                )
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.TEMPERATURE,
                    Constants.FAHRENHEIT,
                    requireContext()
                )
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

    private fun setDefaultRadioButtons() {
        if (sharedViewModel.readStringFromSettingSP(
                Constants.LOCATION,
                requireContext()
            ) == Constants.GPS
        ) {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_gps)
        } else {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_map)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.WIND_SPEED,
                requireContext()
            ) == Constants.MILE_HOUR
        ) {
            binding.radioGroupSettingWind.check(R.id.radio_setting_mile)
        } else {
            binding.radioGroupSettingWind.check(R.id.radio_setting_meter)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.LANGUAGE,
                requireContext()
            ) == Constants.ARABIC
        ) {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_arabic)
        } else {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_english)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.NOTIFICATION,
                requireContext()
            ) == Constants.DISABLE
        ) {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_disable_notification)
        } else {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_enable_notification)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.TEMPERATURE,
                requireContext()
            ) == Constants.KELVIN
        ) {
            binding.radioGroupSettingTemp.check(R.id.radio_setting_kelvin)
        } else if (sharedViewModel.readStringFromSettingSP(
                Constants.TEMPERATURE,
                requireContext()
            ) == Constants.FAHRENHEIT
        ) {
            binding.radioGroupSettingTemp.check(R.id.radio_fahrenheit)
        } else {
            binding.radioGroupSettingTemp.check(R.id.radio_setting_celsius)
        }
    }

    private fun restartApplication() {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(
            requireActivity().packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().finish()
        if (intent != null) {
            startActivity(intent)
        }
    }

}