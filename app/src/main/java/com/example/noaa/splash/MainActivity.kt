package com.example.noaa.splash
//the National Oceanic and Atmospheric Administration
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.postOnAnimationDelayed
import com.example.noaa.databinding.ActivityMainBinding
import com.example.noaa.homeactivity.HomeActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieAnimation.postOnAnimationDelayed(3000){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}