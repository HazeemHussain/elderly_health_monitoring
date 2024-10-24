package com.example.lifelineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifelineapp.R
import com.example.lifelineapp.model.Appointment

/**
 * Adapter class for the appointments in the recycler view
 */

class AppointmentsAdapter(private var appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.dateTextView.text = appointment.date
        holder.timeTextView.text = appointment.time
        holder.addressTextView.text = appointment.address
    }

    override fun getItemCount() = appointments.size

    fun updateData(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.appointmentDate)
        val timeTextView: TextView = itemView.findViewById(R.id.appointmentTime)
        val addressTextView: TextView = itemView.findViewById(R.id.appointmentAddress)
    }
}
