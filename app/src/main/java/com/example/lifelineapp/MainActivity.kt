package com.example.lifelineapp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifelineapp.model.PatientData
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var heartRateText: TextView
    private lateinit var bloodPressureText: TextView
    private val healthDataRepository = HealthDataRepository()
    private val patientId = PatientData.patientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Start the heart rate and blood pressure simulations
        startHeartRateSimulation(patientId)
        startBloodPressureSimulation(patientId)

        // Initialize UI Components
        initializeUIComponents()


        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }


    /**
     * Start heart rate simulation and update the TextView in real-time.
     */
    private fun startHeartRateSimulation(patientId: String) {
        healthDataRepository.startHeartRateSimulation(patientId) { heartRate ->
            heartRate?.let {
                val timestamp = getCurrentTime()
                val text = "Heart Rate: $it bpm\nLast Updated: $timestamp secs ago"
                heartRateText.text = formatText(text, "Last Updated: $timestamp secs ago")
                Log.d("HealthActivity", "Heart Rate Updated: $it at $timestamp")
            } ?: run {
                heartRateText.text = "No heart rate data available"
                Log.d("HealthActivity", "No heart rate data found.")
            }
        }
    }

    /**
     * Start blood pressure simulation and update the TextView in real-time.
     */
    private fun startBloodPressureSimulation(patientId: String) {
        healthDataRepository.startBloodPressureSimulation(patientId) { bloodPressure ->
            bloodPressure?.let {
                val timestamp = getCurrentTime()
                val text = "Blood Pressure: ${it.systolic}/${it.diastolic} mmHg\nLast Updated: $timestamp secs ago"
                bloodPressureText.text = formatText(text, "Last Updated: $timestamp secs ago")
                Log.d("HealthActivity", "Blood Pressure Updated: ${it.systolic}/${it.diastolic} at $timestamp")
            } ?: run {
                bloodPressureText.text = "No blood pressure data available"
                Log.d("HealthActivity", "No blood pressure data found.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the simulation when the activity is destroyed to prevent memory leaks
        healthDataRepository.stopSimulation()
    }

    /**
     * Format the text for the "Last Updated" part to be smaller and different style.
     */
    private fun formatText(fullText: String, updateTimeText: String): SpannableString {
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf(updateTimeText)
        val endIndex = startIndex + updateTimeText.length

        // Make the "Last Updated" part smaller
        spannable.setSpan(RelativeSizeSpan(0.6f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Apply italic style
        spannable.setSpan(StyleSpan(android.graphics.Typeface.ITALIC), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    /**
     * Get the current time formatted as "ss".
     */
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Initialize UI components
     */
    private fun initializeUIComponents() {
        // Initialize TextViews
        heartRateText = findViewById(R.id.heart_rate_text)
        bloodPressureText = findViewById(R.id.blood_pressure_text)

    }

}
