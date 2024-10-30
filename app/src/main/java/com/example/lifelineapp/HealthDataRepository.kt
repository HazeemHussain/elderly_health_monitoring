package com.example.lifelineapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.lifelineapp.model.BloodPressure
import com.example.lifelineapp.model.HealthData
import com.example.lifelineapp.model.PatientData
import com.google.firebase.database.*

class HealthDataRepository(private val appContext: Context) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 10000 // 10 seconds
    private val patientId = PatientData.patientId

    // Function to retrieve health data for a patient
    fun getHealthData(callback: (HealthData?) -> Unit) {
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

                        Log.d("HealthDataRepository", "Parsed health data: SleepHrs=$sleepHrs, Weight=$weight, Steps=$steps")

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


    // Function to simulate heart rate changes and detect abnormal values
    fun startHeartRateSimulation(context: HealthActivity, callback: (Int?) -> Unit) {
        getHeartBeats { heartBeats ->
            if (heartBeats != null && heartBeats.isNotEmpty()) {
                var index = 0
                val updateTask = object : Runnable {
                    override fun run() {
                        val heartRate = heartBeats[index]
                        callback(heartRate)
                        Log.d("HealthDataRepository", "Heart Rate Updated: $heartRate")

                        // Check for abnormal heart rate
                        if (heartRate < 60 || heartRate > 120) {
                            // Ask user for confirmation via activity
                            context.promptUserToNotifyEmergencyContact()
                        }

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

    // Function to notify emergency contacts
    fun notifyEmergencyContact() {
        database.child("users").child("patients").child(patientId).child("EmergencyContacts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (contactSnapshot in snapshot.children) {
                        val name = contactSnapshot.child("Name").getValue(String::class.java) ?: ""
                        val phoneNo = contactSnapshot.child("PhoneNo").getValue(String::class.java) ?: ""

                        Log.d("HealthDataRepository", "Notifying $name at $phoneNo")
                        Toast.makeText(appContext, "Notifying $name at $phoneNo", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HealthDataRepository", "Failed to retrieve emergency contacts", error.toException())
                }
            })
    }

    // Function to simulate blood pressure changes every 10 seconds
    fun startBloodPressureSimulation(callback: (BloodPressure?) -> Unit) {
        getHealthData { healthData ->
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

    // Retrieve heart beats for a patient
    fun getHeartBeats(callback: (List<Int>?) -> Unit) {
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
