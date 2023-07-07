package com.example.noaa.alert.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.R
import com.example.noaa.alert.viewmodel.AlertViewModel
import com.example.noaa.alert.viewmodel.AlertViewModelFactory
import com.example.noaa.databinding.AlertDialogLayoutBinding
import com.example.noaa.databinding.FragmentAlertBinding
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Repo
import com.example.noaa.services.alarm.AlarmScheduler
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.notification.NotificationChannelHelper
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import com.example.noaa.utilities.PermissionUtility
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding
    private lateinit var bindingAlertLayout: AlertDialogLayoutBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertRecyclerAdapter: AlertRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationChannelHelper.createNotificationChannel(requireContext())
        val factory = AlertViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(view.context),
                ),
                ConcreteLocalSource.getInstance(requireContext()),
                SettingSharedPref.getInstance(requireContext())
            )
        )

        alertViewModel = ViewModelProvider(this, factory)[AlertViewModel::class.java]
        alertViewModel.getAllAlarms()
        alertRecyclerAdapter = AlertRecyclerAdapter()
        binding.rvAlerts.adapter = alertRecyclerAdapter
        deleteBySwipe()

        lifecycleScope.launch {
            alertViewModel.alarmsStateFlow.collectLatest {
                alertRecyclerAdapter.submitList(it)
            }
        }
        binding.fabAddAlert.setOnClickListener {

            if (PermissionUtility.notificationPermission(requireContext())) {
                showTimeDialog()
            } else {
                showSettingDialog()
            }


        }

        binding.btnEnable.setOnClickListener {
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:com.example.noaa")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtility.notificationPermission(requireContext())) {
            binding.tvAlertUser.visibility = View.GONE
            binding.btnEnable.visibility = View.GONE
        } else {
            binding.tvAlertUser.visibility = View.VISIBLE
            binding.btnEnable.visibility = View.VISIBLE
        }
    }

    private fun showTimeDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        bindingAlertLayout = AlertDialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(bindingAlertLayout.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingAlertLayout.cvFrom.setOnClickListener {
            showDatePicker()
        }


        bindingAlertLayout.btnSaveDialog.setOnClickListener {

            val kindId = bindingAlertLayout.radioGroupAlertDialog.checkedRadioButtonId
            var kind: String = Constants.ALERT
            if (kindId == bindingAlertLayout.radioNotification.id) {
                kind = Constants.NOTIFICATION
            }
            val time = Functions.formatFromStringToLong(
                bindingAlertLayout.tvFromDateDialog.text.toString(),
                bindingAlertLayout.tvFromTimeDialog.text.toString()
            )
            val currentTimeInMillis = System.currentTimeMillis()

            val alarmItem = AlarmItem(time = time, kind = kind)
            if (time > currentTimeInMillis) {
                alertViewModel.insertAlarm(alarmItem)
                startAlarmManager(alarmItem)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "please select a time in the future",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()

    }


    private fun startAlarmManager(alarmItem: AlarmItem) {
        AlarmScheduler.getInstance(
            requireContext()).createAlarm(alarmItem, requireContext()
        )
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:com.example.noaa")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteBySwipe() {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alarmItem = alertRecyclerAdapter.currentList[position]
                alertViewModel.deleteAlarm(alarmItem)
                AlarmScheduler.getInstance(requireContext())
                    .cancelAlarm(alarmItem, requireContext())

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
    }

    private fun showDatePicker() {
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
            bindingAlertLayout.tvFromDateDialog.text = formattedDate
            showTimePicker()


        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setMinute(currentMinute + 1)
                .setTitleText("Select Appointment time")
                .build()

        timePicker.show(parentFragmentManager, "time")
        timePicker.addOnPositiveButtonClickListener {

            bindingAlertLayout.tvFromTimeDialog.text =
                Functions.formatHourMinuteToString(timePicker.hour, timePicker.minute)

        }

        timePicker.addOnCancelListener {

            bindingAlertLayout.tvFromTimeDialog.text =
                Functions.formatHourMinuteToString(timePicker.hour, timePicker.minute)

        }
    }
}