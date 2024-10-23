package com.example.lifelineapp

import com.example.lifelineapp.model.BloodPressure
import com.example.lifelineapp.model.HealthData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class HealthDataRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

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
