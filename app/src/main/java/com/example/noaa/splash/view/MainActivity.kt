package com.example.noaa.splash.view
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.postOnAnimationDelayed
import com.example.noaa.databinding.ActivityMainBinding
import com.example.noaa.homeactivity.view.HomeActivity
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        forceLightMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDefaultLanguage()

        binding.lottieAnimation.postOnAnimationDelayed(3000){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setDefaultLanguage() {
        if (SettingSharedPref.getInstance(this).readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC) {
            Functions.changeLanguage(this, "ar")
        } else {
            Functions.changeLanguage(this, "en")
        }
    }

    private fun forceLightMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
