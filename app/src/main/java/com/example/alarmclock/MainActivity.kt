package com.example.alarmclock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView
    private lateinit var rvAlarms: RecyclerView
    private lateinit var adapter: AlarmAdapter
    private val alarms = mutableListOf<Alarm>()
    private val handler = Handler(Looper.getMainLooper())
    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTime = findViewById(R.id.tvTime)
        tvDate = findViewById(R.id.tvDate)
        rvAlarms = findViewById(R.id.rvAlarms)

        alarms.addAll(AlarmStorage.load(this))

        adapter = AlarmAdapter(alarms,
            onToggle = { alarm, enabled ->
                alarm.isEnabled = enabled
                if (enabled) AlarmScheduler.schedule(this, alarm)
                else AlarmScheduler.cancel(this, alarm.id)
                AlarmStorage.save(this, alarms)
            },
            onDelete = { alarm ->
                AlarmScheduler.cancel(this, alarm.id)
                alarms.remove(alarm)
                adapter.notifyDataSetChanged()
                AlarmStorage.save(this, alarms)
            }
        )
        rvAlarms.layoutManager = LinearLayoutManager(this)
        rvAlarms.adapter = adapter

        findViewById<Button>(R.id.btnAddAlarm).setOnClickListener { showAddAlarmDialog() }

        handler.post(clockRunnable)
    }

    private fun updateClock() {
        val now = Calendar.getInstance()
        tvTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now.time)
        tvDate.text = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(now.time)
    }

    private fun showAddAlarmDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_alarm, null)
        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)
        val etLabel = view.findViewById<EditText>(R.id.etLabel)
        val rgTones = view.findViewById<RadioGroup>(R.id.rgTones)
        timePicker.setIs24HourView(false)

        AlertDialog.Builder(this)
            .setTitle("Set alarm")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                // Get selected tone from the checked radio button's tag
                val selectedId = rgTones.checkedRadioButtonId
                val selectedTone = view.findViewById<RadioButton>(selectedId)?.tag as? String ?: "chime"

                val alarm = Alarm(
                    id = System.currentTimeMillis().toInt(),
                    hour = timePicker.hour,
                    minute = timePicker.minute,
                    label = etLabel.text.toString().ifBlank { "Alarm" },
                    tone = selectedTone
                )
                alarms.add(alarm)
                AlarmScheduler.schedule(this, alarm)
                AlarmStorage.save(this, alarms)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(clockRunnable)
    }
}