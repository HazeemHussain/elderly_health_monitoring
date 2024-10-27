package com.example.lifelineapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifelineapp.model.PatientData
import com.example.lifelineapp.utils.FullScreenUtil
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var loadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the loading bar
        loadingBar = ProgressDialog(this)

        // Apply full-screen and immersive mode settings
        FullScreenUtil.setupFullScreenMode(this)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showPassword = findViewById<CheckBox>(R.id.showPassword)
        val forgotPassword = findViewById<Button>(R.id.forgot_password)

        // Show/hide password
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = null
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod()
            }
        }

        // Login button functionality
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                showLoadingBar("Logging In", "Verifying your details")
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        //ForgotPassword button functionality
        forgotPassword.setOnClickListener() {
            // Navigate back to the login screen
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun showLoadingBar(title: String, message: String) {
        loadingBar.setTitle(title)
        loadingBar.setMessage(message)
        loadingBar.setCanceledOnTouchOutside(false)
        loadingBar.show()
    }

    private fun dismissLoadingBar() {
        loadingBar.dismiss()
    }

    private fun loginUser(username: String, password: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users").child("patients").child(username).child("patientDetails")

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val storedPassword = snapshot.child("Password").value.toString()
                if (storedPassword == password) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // Set the patient ID in PatientData
                    PatientData.patientId = username

                    // Navigate to Dashboard
                    val intent = Intent(this, HealthActivity::class.java)
                    startActivity(intent)
                    finish() // Prevent back navigation to login
                } else {
                    dismissLoadingBar()
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            } else {
                dismissLoadingBar()
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            dismissLoadingBar()
            Log.e("FirebaseDatabase", "Error checking username", exception)
            Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
