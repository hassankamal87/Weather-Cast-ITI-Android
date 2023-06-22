package com.example.noaa.alert.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.example.noaa.R
import com.example.noaa.databinding.AlertDialogLayoutBinding
import com.example.noaa.databinding.FragmentAlertBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding
    private lateinit var bindingAlertLayout: AlertDialogLayoutBinding

    private var fromDateLong: Long? = null
    private var fromHourInt: Int? = null
    private var fromMinuteInt: Int? = null

    private var toDateLong: Long? = null
    private var toHourInt: Int? = null
    private var toMinuteInt: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddAlert.setOnClickListener {
            showDialog()

        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        bindingAlertLayout = AlertDialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(bindingAlertLayout.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingAlertLayout.cvFrom.setOnClickListener {
            showDatePicker(bindingAlertLayout.tvFromDateDialog, bindingAlertLayout.tvFromTimeDialog)
        }

        bindingAlertLayout.cvTo.setOnClickListener {
            showDatePicker(bindingAlertLayout.tvToDateDialog, bindingAlertLayout.tvToTimeDialog)
        }

        bindingAlertLayout.btnSaveDialog.setOnClickListener {
            Toast.makeText(requireContext(), "save", Toast.LENGTH_SHORT).show()
            Log.d("hassankamal", "$fromDateLong to $toDateLong")
            Log.d("hassankamal", "$fromHourInt to $toHourInt")
            Log.d("hassankamal", "$fromMinuteInt to $toMinuteInt")
        }

        dialog.show()

    }

    private fun showDatePicker(dateView: TextView, timeView: TextView) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .setTitleText("Select date")
                .build()

        datePicker.show(parentFragmentManager, "date")

        datePicker.addOnPositiveButtonClickListener { date ->

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val stringDate = Date(date)
            val formattedDate = dateFormat.format(stringDate)
            dateView.text = formattedDate
            showTimePicker(timeView)

            if(dateView == bindingAlertLayout.tvFromDateDialog){
                fromDateLong = date
            }else{
                toDateLong = date
            }
        }
    }

    private fun showTimePicker(timeView: TextView) {

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setMinute(currentMinute+1)
                .setTitleText("Select Appointment time")
                .build()

        timePicker.show(parentFragmentManager, "time")
        timePicker.addOnPositiveButtonClickListener {

            var selector = ""
            val minute = timePicker.minute.toString()
            val hour = when {
                timePicker.hour > 12 -> {
                    selector = "PM"
                    (timePicker.hour - 12).toString()
                }

                else -> {
                    selector = "AM"
                    timePicker.hour.toString()
                }
            }

            timeView.text = "$hour:$minute $selector"
            if(timeView == bindingAlertLayout.tvFromTimeDialog){
                fromHourInt = timePicker.hour
                fromMinuteInt = timePicker.minute
            }else{
                toHourInt = timePicker.hour
                toMinuteInt = timePicker.minute
            }

        }

        timePicker.addOnCancelListener {

            var selector = ""
            val minute = (currentMinute+1).toString()
            val hour = when {
                currentHour > 12 -> {
                    selector = "PM"
                    (currentHour - 12).toString()
                }

                else -> {
                    selector = "AM"
                    currentHour.toString()
                }
            }

            timeView.text = "$hour:$minute $selector"

            if(timeView == bindingAlertLayout.tvFromTimeDialog){
                fromHourInt = currentHour
                fromMinuteInt = currentMinute
            }else{
                toHourInt = currentHour
                toMinuteInt = currentMinute+1
            }

        }
    }
}