package com.example.noaa.setting.view


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
                ConcreteLocalSource.getInstance(requireContext()),
                SettingSharedPref.getInstance(requireContext())
            )
        )
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]

        setDefaultRadioButtons()

        binding.radioGroupSettingLocation.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_map) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LOCATION, Constants.MAP
                )
                val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
                view.findNavController().navigate(action)
            } else {
                sharedViewModel.writeStringToSettingSP(Constants.LOCATION, Constants.GPS)
                sharedViewModel.getLocation(requireContext())
            }
        }

        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_meter) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.WIND_SPEED, Constants.METER_SEC
                )
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.WIND_SPEED, Constants.MILE_HOUR
                )
            }
        }


        binding.radioGroupSettingLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_arabic) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LANGUAGE, Constants.ARABIC
                )
                Functions.changeLanguage(requireActivity(), "ar")
                restartApplication()
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.LANGUAGE, Constants.ENGLISH
                )
                Functions.changeLanguage(requireActivity(), "en")
                restartApplication()
            }
        }


        binding.radioGroupSettingNotification.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_setting_enable_notification) {
                sharedViewModel.writeStringToSettingSP(
                    Constants.NOTIFICATION, Constants.ENABLE
                )
            } else {
                sharedViewModel.writeStringToSettingSP(
                    Constants.NOTIFICATION, Constants.DISABLE
                )
            }
        }


        binding.radioGroupSettingTemp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_setting_celsius -> {
                    sharedViewModel.writeStringToSettingSP(
                        Constants.TEMPERATURE, Constants.CELSIUS
                    )
                }
                R.id.radio_setting_kelvin -> {
                    sharedViewModel.writeStringToSettingSP(
                        Constants.TEMPERATURE, Constants.KELVIN
                    )
                }
                else -> {
                    sharedViewModel.writeStringToSettingSP(
                        Constants.TEMPERATURE, Constants.FAHRENHEIT
                    )
                }
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
                Constants.LOCATION
            ) == Constants.GPS
        ) {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_gps)
        } else {
            binding.radioGroupSettingLocation.check(R.id.radio_setting_map)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.WIND_SPEED
            ) == Constants.MILE_HOUR
        ) {
            binding.radioGroupSettingWind.check(R.id.radio_setting_mile)
        } else {
            binding.radioGroupSettingWind.check(R.id.radio_setting_meter)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.LANGUAGE
            ) == Constants.ARABIC
        ) {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_arabic)
        } else {
            binding.radioGroupSettingLanguage.check(R.id.radio_setting_english)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.NOTIFICATION
            ) == Constants.DISABLE
        ) {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_disable_notification)
        } else {
            binding.radioGroupSettingNotification.check(R.id.radio_setting_enable_notification)
        }

        if (sharedViewModel.readStringFromSettingSP(
                Constants.TEMPERATURE
            ) == Constants.KELVIN
        ) {
            binding.radioGroupSettingTemp.check(R.id.radio_setting_kelvin)
        } else if (sharedViewModel.readStringFromSettingSP(
                Constants.TEMPERATURE
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