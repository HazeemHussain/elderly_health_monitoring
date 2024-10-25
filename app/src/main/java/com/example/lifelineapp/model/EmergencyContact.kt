package com.example.lifelineapp.model

data class EmergencyContact(
    val name: String,
    val phoneNo: String,
    val address: String,
    val imageUrl: String = "",
    val relationship: String,
)
