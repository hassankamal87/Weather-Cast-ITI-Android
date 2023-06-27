package com.example.noaa.setting.view

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
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.utilities.Constants
import com.google.android.gms.location.LocationServices


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

        val sharedPreferences = view.context.getSharedPreferences(
            Constants.SETTING,
            AppCompatActivity.MODE_PRIVATE
        )
        val factory = SharedViewModelFactory(Repo.getInstance(RemoteSource,
            LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(view.context))),
            sharedPreferences)
        sharedViewModel = ViewModelProvider(requireActivity(), factory)[SharedViewModel::class.java]


        if(sharedPreferences.getString(Constants.LOCATION, "null") == Constants.GPS){
            binding.radioGroupSettingLocation.check(R.id.radio_setting_gps)
        }else{
            binding.radioGroupSettingLocation.check(R.id.radio_setting_map)
        }

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
}