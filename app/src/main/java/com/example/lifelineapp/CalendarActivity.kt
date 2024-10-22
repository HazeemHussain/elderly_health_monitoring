package com.example.lifelineapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }
}