package com.example.alarmclock

import java.io.Serializable

data class Alarm(
    val id: Int,
    var hour: Int,
    var minute: Int,
    var label: String = "Alarm",
    var tone: String = "chime",   // "beep" | "chime" | "pulse" | "bell"
    var isEnabled: Boolean = true
) : Serializable