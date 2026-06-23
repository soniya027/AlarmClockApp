package com.example.alarmclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class AlarmAdapter(
    private val alarms: MutableList<Alarm>,
    private val onToggle: (Alarm, Boolean) -> Unit,
    private val onDelete: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvAlarmTime)
        val tvLabel: TextView = view.findViewById(R.id.tvAlarmLabel)
        val toggle: SwitchMaterial = view.findViewById(R.id.switchAlarm)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false))

    override fun getItemCount() = alarms.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms[position]
        val ampm = if (alarm.hour < 12) "AM" else "PM"
        val h12 = if (alarm.hour % 12 == 0) 12 else alarm.hour % 12
        holder.tvTime.text = "%d:%02d %s".format(h12, alarm.minute, ampm)
        holder.tvLabel.text = alarm.label
        holder.toggle.isChecked = alarm.isEnabled
        holder.toggle.setOnCheckedChangeListener { _, checked -> onToggle(alarm, checked) }
        holder.btnDelete.setOnClickListener { onDelete(alarm) }
    }
}