package com.example.alarmclock

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "alarm_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarm = intent?.getSerializableExtra("alarm") as? Alarm ?: return START_NOT_STICKY

        // Launch ringing screen
        val ringIntent = Intent(this, RingingActivity::class.java).apply {
            putExtra("alarm", alarm)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(ringIntent)

        // Play tone
        val rawId = when (alarm.tone) {
            "beep"  -> R.raw.beep
            "pulse" -> R.raw.pulse
            "bell"  -> R.raw.bell
            else    -> R.raw.chime
        }
        mediaPlayer = MediaPlayer().apply {
            val afd = resources.openRawResourceFd(rawId)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            isLooping = true
            prepare()
            start()
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarm ringing")
            .setContentText(alarm.label)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        startForeground(alarm.id, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Alarms", NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}