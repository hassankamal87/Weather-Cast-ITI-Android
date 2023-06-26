package com.example.noaa.utilities

import android.widget.ImageView
import com.example.noaa.R

object Functions {

    fun setIcon(id: String, iv: ImageView){
        when (id) {
            "01d" -> iv.setImageResource(R.drawable.sun)
            "02d" -> iv.setImageResource(R.drawable._02d)
            "03d" -> iv.setImageResource(R.drawable._03d)
            "04d" -> iv.setImageResource(R.drawable._04n)
            "09d" -> iv.setImageResource(R.drawable._09n)
            "10d" -> iv.setImageResource(R.drawable._10d)
            "11d" -> iv.setImageResource(R.drawable._11d)
            "13d" -> iv.setImageResource(R.drawable._13d)
            "50d" -> iv.setImageResource(R.drawable._50d)
            "01n" -> iv.setImageResource(R.drawable._01n)
            "02n" -> iv.setImageResource(R.drawable._02n)
            "03n" -> iv.setImageResource(R.drawable._03d)
            "04n" -> iv.setImageResource(R.drawable._04n)
            "09n" -> iv.setImageResource(R.drawable._09n)
            "10n" -> iv.setImageResource(R.drawable._10n)
            "11n" -> iv.setImageResource(R.drawable._11d)
            "13n" -> iv.setImageResource(R.drawable._13d)
            "50n" -> iv.setImageResource(R.drawable._50d)
            else -> iv.setImageResource(R.drawable._load)
        }
    }
}