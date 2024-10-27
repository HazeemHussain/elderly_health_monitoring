package com.example.lifelineapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.lifelineapp.model.PatientData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.example.lifelineapp.utils.FullScreenUtil

class AddEmergencyContacts : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var phoneNoEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var relationshipEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var backButton: ImageView
    private val patientId = PatientData.patientId

    private lateinit var relationshipAutoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_emergency_contact)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components
        initializeUIComponents()

        // Set up AutoCompleteTextView for relationships
        val relationshipAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.relationship_options,
            android.R.layout.simple_dropdown_item_1line
        )
        relationshipAutoCompleteTextView.setAdapter(relationshipAdapter)

        // Save button click listener
        saveButton.setOnClickListener {
            saveEmergencyContact()
        }
    }

    private fun saveEmergencyContact() {
        val name = nameEditText.text.toString().trim()
        val phoneNo = phoneNoEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val relationship = relationshipAutoCompleteTextView.text.toString().trim()

        // Check if all fields are filled
        if (name.isEmpty() || phoneNo.isEmpty() || address.isEmpty() || relationship.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare the data map to be saved
        val emergencyContactData = mapOf(
            "name" to name,
            "phoneNo" to phoneNo,
            "address" to address,
            "relationship" to relationship
        )

        // Save the data to Firebase
        val emergencyContactRef = database.child("users").child("patients").child(patientId).child("EmergencyContacts").push()
        emergencyContactRef.setValue(emergencyContactData).addOnSuccessListener {
            Toast.makeText(this, "Emergency Contact added successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the current activity
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add emergency contact", Toast.LENGTH_SHORT).show()
        }

        /**
         * Back to previous activity
         */
        backButton.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }

    /**
     *Initializing UI components
     */
    private fun initializeUIComponents() {
        // Initialize UI components
        nameEditText = findViewById(R.id.contactName)
        phoneNoEditText = findViewById(R.id.contactPhoneNo)
        addressEditText = findViewById(R.id.contactAddress)
        relationshipAutoCompleteTextView = findViewById(R.id.contactRelationship)
        saveButton = findViewById(R.id.saveEmergencyContactButton)
        backButton = findViewById<ImageView>(R.id.backButton)
    }
}