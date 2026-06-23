package com.example.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all enabled alarms after reboot
            val alarms = AlarmStorage.load(context)
            alarms.filter { it.isEnabled }.forEach { AlarmScheduler.schedule(context, it) }
            return
        }
        val alarm = intent.getSerializableExtra("alarm") as? Alarm ?: return
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarm", alarm)
        }
        context.startForegroundService(serviceIntent)
    }
}