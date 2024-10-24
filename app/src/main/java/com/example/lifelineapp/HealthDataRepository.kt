package com.example.lifelineapp

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.lifelineapp.model.BloodPressure
import com.example.lifelineapp.model.HealthData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HealthDataRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 10000 // 10 seconds

    // Function to retrieve health data for a patient
    fun getHealthData(patientId: String, callback: (HealthData?) -> Unit) {
        database.child("users").child("patients").child(patientId).child("healthData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val heartBeats = snapshot.child("HeartBeats").children.mapNotNull {
                            it.getValue(Int::class.java)
                        }
                        val bloodPressureList = snapshot.child("bloodPressure").children.mapNotNull {
                            val systolic = it.child("systolic").getValue(Int::class.java)
                            val diastolic = it.child("diastolic").getValue(Int::class.java)
                            if (systolic != null && diastolic != null) {
                                BloodPressure(systolic, diastolic)
                            } else {
                                null
                            }
                        }
                        val sleepHrs = snapshot.child("SleepHrs").getValue(Int::class.java) ?: 0
                        val weight = snapshot.child("Weight").getValue(Int::class.java) ?: 0
                        val steps = snapshot.child("Steps").getValue(Int::class.java) ?: 0

                        val healthData = HealthData(heartBeats, bloodPressureList, sleepHrs, weight, steps)
                        callback(healthData)
                    } catch (e: Exception) {
                        Log.e("HealthDataRepository", "Error parsing health data", e)
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HealthDataRepository", "Firebase data retrieval cancelled", error.toException())
                    callback(null)
                }
            })
    }

    // Function to simulate heart rate changes every 10 seconds
    fun startHeartRateSimulation(patientId: String, callback: (Int?) -> Unit) {
        getHeartBeats(patientId) { heartBeats ->
            if (heartBeats != null && heartBeats.isNotEmpty()) {
                var index = 0
                val updateTask = object : Runnable {
                    override fun run() {
                        val heartRate = heartBeats[index]
                        callback(heartRate)
                        Log.d("HealthDataRepository", "Heart Rate Updated: $heartRate")

                        // Cycle through the heart rates
                        index = (index + 1) % heartBeats.size
                        handler.postDelayed(this, updateInterval)
                    }
                }
                handler.post(updateTask)
            } else {
                Log.d("HealthDataRepository", "No heart rate data available")
                callback(null)
            }
        }
    }

    // Function to simulate blood pressure changes every 10 seconds
    fun startBloodPressureSimulation(patientId: String, callback: (BloodPressure?) -> Unit) {
        getHealthData(patientId) { healthData ->
            if (healthData != null && healthData.bloodPressure.isNotEmpty()) {
                var index = 0
                val updateTask = object : Runnable {
                    override fun run() {
                        val bloodPressure = healthData.bloodPressure[index]
                        callback(bloodPressure)
                        Log.d("HealthDataRepository", "Blood Pressure Updated: ${bloodPressure.systolic}/${bloodPressure.diastolic}")

                        // Cycle through the blood pressure values
                        index = (index + 1) % healthData.bloodPressure.size
                        handler.postDelayed(this, updateInterval)
                    }
                }
                handler.post(updateTask)
            } else {
                Log.d("HealthDataRepository", "No blood pressure data available")
                callback(null)
            }
        }
    }

    // Call this method to stop the simulation
    fun stopSimulation() {
        handler.removeCallbacksAndMessages(null)
    }

    fun getHeartBeats(patientId: String, callback: (List<Int>?) -> Unit) {
        database.child("users").child("patients").child(patientId).child("healthData").child("HeartBeats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val heartBeats = snapshot.children.mapNotNull { it.getValue(Int::class.java) }
                        callback(heartBeats)
                    } catch (e: Exception) {
                        Log.e("HealthDataRepository", "Error retrieving heart beats", e)
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HealthDataRepository", "Firebase data retrieval cancelled", error.toException())
                    callback(null)
                }
            })
    }
}
