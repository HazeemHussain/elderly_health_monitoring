package com.example.lifelineapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifelineapp.model.PatientData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddAppointmentActivity : AppCompatActivity() {

    private lateinit var appointmentDateText: TextView
    private lateinit var appointmentTimeText: TextView
    private lateinit var addressEditText: EditText
    private lateinit var saveButton: Button
    private val calendar = Calendar.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val patientId = PatientData.patientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appointment)

        // Initialize views
        appointmentDateText = findViewById(R.id.appointmentDate)
        appointmentTimeText = findViewById(R.id.appointmentTime)
        addressEditText = findViewById(R.id.addressEditText)
        saveButton = findViewById(R.id.saveButton)

        // Set up date picker
        appointmentDateText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabel()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Set up time picker
        appointmentTimeText.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeLabel()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Set up save button
        saveButton.setOnClickListener {
            saveAppointment()
        }

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }

    private fun updateDateLabel() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        appointmentDateText.text = dateFormat.format(calendar.time)
    }

    private fun updateTimeLabel() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        appointmentTimeText.text = timeFormat.format(calendar.time)
    }

    private fun saveAppointment() {
        val date = appointmentDateText.text.toString()
        val time = appointmentTimeText.text.toString()
        val address = addressEditText.text.toString()

        if (date.isEmpty() || time.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newAppointmentRef = database.child("users").child("patients").child(patientId).child("appointments").push()
        newAppointmentRef.setValue(mapOf("Date" to date, "Time" to time, "Address" to address))
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment added successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after saving
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add appointment", Toast.LENGTH_SHORT).show()
            }
    }
}
