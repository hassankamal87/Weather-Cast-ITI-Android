package com.example.noaa.home.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.noaa.R
import com.example.noaa.databinding.FragmentHomeBinding
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.Coordinate
import com.google.android.material.slider.LabelFormatter


class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding

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
    }

    private fun iconAnimation(){
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.place_changer)
        binding.ivWeather.startAnimation(animation)
    }
}