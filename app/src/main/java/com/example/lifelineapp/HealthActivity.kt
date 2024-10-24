package com.example.lifelineapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifelineapp.model.PatientData
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar

class HealthActivity : AppCompatActivity() {

    private lateinit var heartRateText: TextView
    private lateinit var bloodPressureText: TextView
    private lateinit var sleepHrsText: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var weightTextView: TextView
    private val healthDataRepository = HealthDataRepository()
    val patientId = PatientData.patientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_health)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        //Initialize UI Components
        initializeUIComponents()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Call function to load data using the patientId variable
        loadPatientData(patientId)

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
                // Display heart rate
                if (healthData.heartBeats.isNotEmpty()) {
                    val averageHeartRate = healthData.heartBeats.average().toInt()
                    heartRateText.text = "Heart Rate: $averageHeartRate bpm"
                    Log.d("HealthActivity", "Heart Rate Data Loaded: $averageHeartRate")
                } else {
                    heartRateText.text = "No heart rate data available"
                    Log.d("HealthActivity", "No heart rate data found or data is empty.")
                }

                // Display the latest blood pressure
                val latestBloodPressure = healthData.bloodPressure.lastOrNull()
                if (latestBloodPressure != null) {
                    bloodPressureText.text = "Blood Pressure: ${latestBloodPressure.systolic}/${latestBloodPressure.diastolic} mmHg"
                    Log.d("HealthActivity", "Blood Pressure Data Loaded: ${latestBloodPressure.systolic}/${latestBloodPressure.diastolic}")
                } else {
                    bloodPressureText.text = "No blood pressure data available"
                    Log.d("HealthActivity", "No blood pressure data found.")
                }

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