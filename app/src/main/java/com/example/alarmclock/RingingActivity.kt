package com.example.alarmclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RingingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringing)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val alarm = intent.getSerializableExtra("alarm") as? Alarm
        val ampm = if ((alarm?.hour ?: 0) < 12) "AM" else "PM"
        val h12 = if ((alarm?.hour ?: 0) % 12 == 0) 12 else (alarm?.hour ?: 0) % 12

        findViewById<TextView>(R.id.tvRingTime).text =
            "%d:%02d %s".format(h12, alarm?.minute ?: 0, ampm)
        findViewById<TextView>(R.id.tvRingLabel).text = alarm?.label ?: "Alarm"

        findViewById<Button>(R.id.btnDismiss).setOnClickListener {
            stopService(Intent(this, AlarmService::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnSnooze).setOnClickListener {
            stopService(Intent(this, AlarmService::class.java))
            // Re-schedule 5 minutes from now
            alarm?.let {
                val cal = Calendar.getInstance().apply { add(Calendar.MINUTE, 5) }
                val snoozed = it.copy(hour = cal.get(Calendar.HOUR_OF_DAY), minute = cal.get(Calendar.MINUTE))
                AlarmScheduler.schedule(this, snoozed)
            }
            finish()
        }
    }
}