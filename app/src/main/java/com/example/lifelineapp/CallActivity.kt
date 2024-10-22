package com.example.lifelineapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar

class CallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)


        // Find the bottom bar in the layout and set it up using the utility function
        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }
}
