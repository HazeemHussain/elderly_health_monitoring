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
import java.util.*

class HealthActivity : AppCompatActivity() {

    private lateinit var heartRateText: TextView
    private lateinit var bloodPressureText: TextView
    private lateinit var sleepHrsText: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var weightTextView: TextView
    private val healthDataRepository = HealthDataRepository()
    val patientId = PatientData.patientId // Calling the patient Id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_health)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Initialize UI Components
        initializeUIComponents()

        // Load and display static patient data
        loadPatientData(patientId)

        // Start the heart rate and blood pressure simulations
        startHeartRateSimulation(patientId)
        startBloodPressureSimulation(patientId)

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }

    /**
     * Retrieve and display health data for the patient.
     */
    private fun loadPatientData(patientId: String) {
        healthDataRepository.getHealthData(patientId) { healthData ->
            if (healthData != null) {
                // Display sleep hours
                sleepHrsText.text = "Sleep Hours: ${healthData.sleepHrs} hrs"
                Log.d("HealthActivity", "Sleep Hours Data Loaded: ${healthData.sleepHrs}")

                // Display steps
                stepsTextView.text = "Steps: ${healthData.steps}"
                Log.d("HealthActivity", "Steps Data Loaded: ${healthData.steps}")

                // Display weight
                weightTextView.text = "Weight: ${healthData.weight} kg"
                Log.d("HealthActivity", "Weight Data Loaded: ${healthData.weight}")
            } else {
                // If health data is null, update all text views accordingly
                heartRateText.text = "No health data available"
                bloodPressureText.text = "No health data available"
                sleepHrsText.text = "No health data available"
                stepsTextView.text = "No health data available"
                weightTextView.text = "No health data available"
                Log.d("HealthActivity", "Health data retrieval failed or data is null.")
            }
        }
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
     * Initialize UI components and assign them to the corresponding views.
     */
    private fun initializeUIComponents() {
        // Initialize TextViews
        heartRateText = findViewById(R.id.heart_rate_text)
        bloodPressureText = findViewById(R.id.blood_pressure_text)
        sleepHrsText = findViewById(R.id.sleephrs_text)
        stepsTextView = findViewById(R.id.steps_text)
        weightTextView = findViewById(R.id.weight_text)
    }
}
