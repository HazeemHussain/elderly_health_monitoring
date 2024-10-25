package com.example.lifelineapp

import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifelineapp.adapters.ContactsAdapter
import com.example.lifelineapp.model.EmergencyContact
import com.example.lifelineapp.model.PatientData
import com.example.lifelineapp.utils.FullScreenUtil
import com.example.lifelineapp.utils.SpaceItemRecyclerView
import com.google.firebase.database.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class CallActivity : AppCompatActivity() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactsList = mutableListOf<EmergencyContact>()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val patientId = PatientData.patientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Set up RecyclerView
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter(contactsList, this) // Pass context to adapter
        contactsRecyclerView.adapter = contactsAdapter

        // Convert spacing from dp to pixels
        val spacingInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            13f, // 16dp spacing
            resources.displayMetrics
        ).toInt()

        // Add item decoration to RecyclerView
        contactsRecyclerView.addItemDecoration(SpaceItemRecyclerView(spacingInPixels))
        // Fetch contacts from Firebase
        fetchEmergencyContacts()

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }

    private fun fetchEmergencyContacts() {
        database.child("users").child("patients").child(patientId).child("EmergencyContacts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactsList.clear()
                    for (contactSnapshot in snapshot.children) {
                        val name = contactSnapshot.child("Name").getValue(String::class.java) ?: ""
                        val phoneNo = contactSnapshot.child("PhoneNo").getValue(String::class.java) ?: ""
                        val address = contactSnapshot.child("Address").getValue(String::class.java) ?: ""
                        val imageUrl = contactSnapshot.child("image").getValue(String::class.java) ?: ""
                        val relationship = contactSnapshot.child("Relationship").getValue(String::class.java)?:""
                        val contact = EmergencyContact(name, phoneNo, address, imageUrl, relationship)
                        contactsList.add(contact)
                    }
                    contactsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
    }
}
