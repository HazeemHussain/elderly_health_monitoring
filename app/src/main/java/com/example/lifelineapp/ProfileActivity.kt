package com.example.lifelineapp
/**
 *Dashboard/Profile page where user can add or change the personal data
 */
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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
    private lateinit var changePass: Button
    private lateinit var addContacts: Button

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


        /**
         * Calls add emergency contact activity
         */

        addContacts.setOnClickListener() {
            val intent = Intent(this, AddEmergencyContacts::class.java)
            startActivity(intent)
        }


        /**
         * Logs out the user
         */
        logoutBtn.setOnClickListener {
            // Clear the data stored in PatientData
            PatientData.clearData()

            // Navigate back to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears the activity stack
            startActivity(intent)
            finish() // Ends the current activity
        }

        changePass.setOnClickListener() {
            showDialogChangePassword()
        }

        /**
         *Fetching and displaying patient data
         */
        displayPatientData()
    }

    /**
     *Patient data function that retrieves the data from the firebase and displays
     * it on the app
     */
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

    private fun updatePassword(userRef: DatabaseReference, newPassword: String) {
        userRef.child("Password").setValue(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password has been changed successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Password reset failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**
     *Shows password dialogue where user can add new password
     */
    private fun showDialogChangePassword() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Password")
        builder.setIcon(R.drawable.ic_passwordlogo)

        // Set up the input
        val input = EditText(this)
        input.hint = "Enter your new password"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Confirm") { _, _ ->
            val newPassword = input.text.toString().trim()

            if (newPassword.isNotEmpty()) {
                // Update password in Firebase
                val userRef = database.child("users").child("patients").child(patientId).child("patientDetails")
                userRef.child("Password").setValue(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password has been changed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    /**
     *Initializing UI components
     */
    private fun initializeUIComponents() {
        nameTextView = findViewById<TextView>(R.id.patient_name)
        dobTextView = findViewById<TextView>(R.id.patient_dob)
        phoneTextView = findViewById<TextView>(R.id.phoneNo)
        logoutBtn = findViewById<Button>(R.id.logoutBtn)
        changePass = findViewById<Button>(R.id.changePasswordBtn)
        addContacts = findViewById<Button>(R.id.add_contactsbtn)
    }

}
