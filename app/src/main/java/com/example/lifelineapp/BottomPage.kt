package com.example.lifelineapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lifelineapp.utils.FullScreenUtil
import nl.joery.animatedbottombar.AnimatedBottomBar

class BottomPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_page)

        // Set the activity to full-screen mode
        FullScreenUtil.setupFullScreenMode(this)

        val bottomBar = findViewById<AnimatedBottomBar>(R.id.navBar)

        // Use the utility function to set up the bottom bar
        BottomBarUtils.setupBottomBar(this, bottomBar)
    }
}
