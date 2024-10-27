package com.example.lifelineapp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.text.SimpleDateFormat
import java.util.*

class HealthActivity : AppCompatActivity() {

    private lateinit var heartRateText: TextView
    private lateinit var bloodPressureText: TextView
    private lateinit var sleepHrsText: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var statusIndicator: ImageView
    private lateinit var statusText: TextView
    private val healthDataRepository = HealthDataRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_health)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Initialize UI Components
        initializeUIComponents()

        // Load and display static patient data
        loadPatientData()

        // Start the heart rate and blood pressure simulations
        startHeartRateSimulation()
        startBloodPressureSimulation()

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }

    private fun loadPatientData() {
        healthDataRepository.getHealthData { healthData ->
            if (healthData != null) {
                sleepHrsText.text = "Sleep Hours: ${healthData.sleepHrs} hrs"
                stepsTextView.text = "Steps: ${healthData.steps}"
                weightTextView.text = "Weight: ${healthData.weight} kg"
            } else {
                heartRateText.text = "No health data available"
                bloodPressureText.text = "No health data available"
                sleepHrsText.text = "No health data available"
                stepsTextView.text = "No health data available"
                weightTextView.text = "No health data available"
            }
        }
    }

    private fun startHeartRateSimulation() {
        healthDataRepository.startHeartRateSimulation(this) { heartRate ->
            heartRate?.let {
                val timestamp = getCurrentTime()
                val text = "Heart Rate: $it bpm\nLast Updated: $timestamp secs ago"
                heartRateText.text = formatText(text, "Last Updated: $timestamp secs ago")
            }
        }
    }

    fun promptUserToNotifyEmergencyContact() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Emergency Detected")
        builder.setMessage("An abnormal heart rate was detected. Do you want to notify your emergency contact?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            healthDataRepository.notifyEmergencyContact()
            Toast.makeText(this, "Emergency contact notified", Toast.LENGTH_SHORT).show()
            statusIndicator.setImageResource(R.drawable.ic_emergency_alert)
            statusText.text = "Status: Emergency"

            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            Toast.makeText(this, "Notification cancelled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun startBloodPressureSimulation() {
        healthDataRepository.startBloodPressureSimulation { bloodPressure ->
            bloodPressure?.let {
                val timestamp = getCurrentTime()
                val text = "Blood Pressure: ${it.systolic}/${it.diastolic} mmHg\nLast Updated: $timestamp secs ago"
                bloodPressureText.text = formatText(text, "Last Updated: $timestamp secs ago")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        healthDataRepository.stopSimulation()
    }

    private fun formatText(fullText: String, updateTimeText: String): SpannableString {
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf(updateTimeText)
        val endIndex = startIndex + updateTimeText.length
        spannable.setSpan(RelativeSizeSpan(0.6f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(android.graphics.Typeface.ITALIC), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun initializeUIComponents() {
        heartRateText = findViewById(R.id.heart_rate_text)
        bloodPressureText = findViewById(R.id.blood_pressure_text)
        sleepHrsText = findViewById(R.id.sleephrs_text)
        stepsTextView = findViewById(R.id.steps_text)
        weightTextView = findViewById(R.id.weight_text)
        statusIndicator = findViewById(R.id.status_indicator)
        statusText = findViewById(R.id.status_text)
    }
}
