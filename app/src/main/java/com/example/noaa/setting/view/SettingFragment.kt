package com.example.noaa.setting.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.noaa.R
import com.example.noaa.databinding.FragmentSettingBinding


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardsAnimation()
    }

    private fun cardsAnimation(){
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation)
        val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation2)
        val animation3 = AnimationUtils.loadAnimation(requireContext(), R.anim.card_setting_translation3)
        binding.cvLocation.startAnimation(animation)
        binding.cvLanguage.startAnimation(animation)
        binding.cvWind.startAnimation(animation2)
        binding.cvNotification.startAnimation(animation2)
        binding.cvTemp.startAnimation(animation3)
    }
}