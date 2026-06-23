package com.example.alarmclock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AlarmStorage {
    private const val PREFS = "alarms_prefs"
    private const val KEY = "alarm_list"
    private val gson = Gson()

    fun save(context: Context, alarms: List<Alarm>) {
        val json = gson.toJson(alarms)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, json).apply()
    }

    fun load(context: Context): MutableList<Alarm> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Alarm>>() {}.type
        return gson.fromJson(json, type)
    }
}