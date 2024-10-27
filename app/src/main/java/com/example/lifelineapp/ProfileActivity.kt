package com.example.lifelineapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lifelineapp.model.PatientData
import com.example.lifelineapp.utils.FullScreenUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nl.joery.animatedbottombar.AnimatedBottomBar

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    val patientId = PatientData.patientId
    private lateinit var nameTextView: TextView
    private lateinit var dobTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        //Initializing UI Components
        initializeUIComponents()

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)


        logoutBtn.setOnClickListener {
            // Clear the data stored in PatientData
            PatientData.clearData()

            // Navigate back to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears the activity stack
            startActivity(intent)
            finish() // Ends the current activity
        }



        // Fetch and display data
        displayPatientData()
    }

    private fun displayPatientData() {


        val patientRef = database.child("users").child("patients").child(patientId).child("patientDetails")

        // Use a ValueEventListener to continuously listen for changes in real-time
        patientRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Retrieve data
                val fullName = snapshot.child("Name").getValue(String::class.java)
                val dob = snapshot.child("DOB").getValue(String::class.java)
                val phoneNo = snapshot.child("PhoneNo").getValue(String::class.java)

                // Set data to TextViews
                nameTextView.text = "Hi $fullName!" ?: "N/A"
                dobTextView.text = "DOB: $dob" ?: "N/A"
                phoneTextView.text = "Phone No: $phoneNo" ?: "N/A"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun initializeUIComponents() {

        nameTextView = findViewById<TextView>(R.id.patient_name)
        dobTextView = findViewById<TextView>(R.id.patient_dob)
        phoneTextView = findViewById<TextView>(R.id.phoneNo)
        logoutBtn = findViewById<Button>(R.id.logoutBtn)
    }

}
