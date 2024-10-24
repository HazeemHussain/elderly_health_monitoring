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

class MainActivity : AppCompatActivity() {

    private lateinit var heartRateText: TextView
    private lateinit var bloodPressureText: TextView
    private val healthDataRepository = HealthDataRepository()
    val patientId = PatientData.patientId

    // Class-level variable for patient ID
   // private var patientId: String = "patient_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Set padding for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize TextViews
        heartRateText = findViewById(R.id.heart_rate_text)
        bloodPressureText = findViewById(R.id.blood_pressure_text)

        // Call function to load data using the patientId variable
        loadPatientData(patientId)

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }

    private fun loadPatientData(patientId: String) {
        // Retrieve heart beats
        healthDataRepository.getHeartBeats(patientId) { heartBeats ->
            if (heartBeats != null && heartBeats.isNotEmpty()) {
                val averageHeartRate = heartBeats.average().toInt()
                heartRateText.text = "Heart Rate: $averageHeartRate bpm"
                Log.d("MainActivity", "Heart Rate Data Loaded: $averageHeartRate")
            } else {
                heartRateText.text = "No heart rate data available"
                Log.d("MainActivity", "No heart rate data found or data is empty.")
            }
        }

        // Retrieve blood pressure
        healthDataRepository.getHealthData(patientId) { healthData ->
            if (healthData != null) {
                val latestBloodPressure = healthData.bloodPressure.lastOrNull()
                if (latestBloodPressure != null) {
                    bloodPressureText.text = "Blood Pressure: ${latestBloodPressure.systolic}/${latestBloodPressure.diastolic} mmHg"
                    Log.d("MainActivity", "Blood Pressure Data Loaded: ${latestBloodPressure.systolic}/${latestBloodPressure.diastolic}")
                } else {
                    bloodPressureText.text = "No blood pressure data available"
                    Log.d("MainActivity", "No blood pressure data found.")
                }
            } else {
                bloodPressureText.text = "No health data available"
                Log.d("MainActivity", "Health data retrieval failed or data is null.")
            }
        }
    }
}
