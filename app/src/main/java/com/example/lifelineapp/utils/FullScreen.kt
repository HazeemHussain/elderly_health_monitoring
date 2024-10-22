package com.example.lifelineapp.utils

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.Toast

object FullScreenUtil {

    // Function to hide the status bar and the navigation bar using immersive mode
    fun applyFullScreenMode(activity: Activity) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    // Function to set up the full-screen mode and keep it when the system UI changes
    fun setupFullScreenMode(activity: Activity) {
        applyFullScreenMode(activity)
        activity.window.decorView.setOnSystemUiVisibilityChangeListener {
            if (it and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                applyFullScreenMode(activity)
            }
        }
    }

    // Function to lock the screen with a transparent overlay
    fun lockScreen(activity: Activity, overlayView: View) {
        overlayView.visibility = View.VISIBLE
        overlayView.setOnTouchListener { _, _ -> true }  // This consumes all touch events
        Toast.makeText(activity, "Screen Locked", Toast.LENGTH_SHORT).show()
    }

    // Function to unlock the screen and remove the overlay
    fun unlockScreen(activity: Activity, overlayView: View) {
        overlayView.visibility = View.GONE
        overlayView.setOnTouchListener(null)  // Re-enable touch events
        Toast.makeText(activity, "Screen Unlocked", Toast.LENGTH_SHORT).show()
    }
}
