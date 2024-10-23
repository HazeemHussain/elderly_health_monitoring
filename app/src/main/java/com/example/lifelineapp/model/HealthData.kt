package com.example.lifelineapp.model

data class HealthData(
    val heartBeats: List<Int>,
    val bloodPressure: List<BloodPressure>,
    val sleepHrs: Int,
    val weight: Int,
    val steps: Int
)

data class BloodPressure(
    val systolic: Int,
    val diastolic: Int
)
