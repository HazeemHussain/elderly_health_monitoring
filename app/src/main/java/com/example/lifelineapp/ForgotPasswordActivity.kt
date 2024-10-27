package com.example.lifelineapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var usernameEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var confirmButton: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        initializeUIComponents()

        // Show/hide password
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                newPasswordEditText.transformationMethod = null
            } else {
                newPasswordEditText.transformationMethod = PasswordTransformationMethod()
            }
        }
        // Set date picker for DOB field
        dobEditText.setOnClickListener { showDatePicker() }

        // Set confirm button click listener
        confirmButton.setOnClickListener {
            validateAndResetPassword()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDOBField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDOBField() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dobEditText.setText(dateFormat.format(calendar.time))
    }

    private fun validateAndResetPassword() {
        val username = usernameEditText.text.toString().trim()
        val dob = dobEditText.text.toString().trim()
        val contact = contactEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()

        // Check if all fields are filled
        when {
            username.isEmpty() -> usernameEditText.error = "Please enter your username"
            dob.isEmpty() -> dobEditText.error = "Please enter your DOB"
            contact.isEmpty() -> contactEditText.error = "Please enter your contact number"
            newPassword.isEmpty() -> newPasswordEditText.error = "Please enter your new password"
            else -> checkUserDetails(username, dob, contact, newPassword)
        }
    }

    private fun checkUserDetails(username: String, dob: String, contact: String, newPassword: String) {
        val userRef = database.child("users").child("patients").child(username).child("patientDetails")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Retrieve stored details
                    val storedDob = snapshot.child("DOB").getValue(String::class.java)
                    val storedContact = snapshot.child("PhoneNo").getValue(String::class.java)

                    // Check if DOB and contact match
                    if (storedDob == dob && storedContact == contact) {
                        // Update password if details match
                        updatePassword(userRef, newPassword)
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Details do not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Username does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForgotPasswordActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
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

    private fun initializeUIComponents() {
        usernameEditText = findViewById(R.id.patient_username)
        dobEditText = findViewById(R.id.patient_dob)
        contactEditText = findViewById(R.id.patient_contact)
        newPasswordEditText = findViewById(R.id.new_password)
        showPasswordCheckBox = findViewById(R.id.showPassword)
        confirmButton = findViewById(R.id.confirm_button)
    }
}
