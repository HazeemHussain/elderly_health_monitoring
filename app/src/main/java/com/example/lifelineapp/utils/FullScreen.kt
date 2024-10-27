package com.example.lifelineapp.utils
/**
 * The util class handles full screen functionality
 *      Functionality: Turns screen into full screen
 *                      Disables back button
 */
import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

object FullScreenUtil {

    fun setupFullScreenMode(activity: Activity, enableBackButton: Boolean = false) {
        // Apply full-screen mode
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Re-apply full-screen mode if the system UI visibility changes
        activity.window.decorView.setOnSystemUiVisibilityChangeListener {
            if (it and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                applyFullScreenMode(activity)
            }
        }

        // Disable back button unless it's explicitly enabled (e.g., on the login page)
        if (!enableBackButton) {
            (activity as? ComponentActivity)?.onBackPressedDispatcher?.addCallback(
                activity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Do nothing to disable the back button
                    }
                })
        }
    }

    private fun applyFullScreenMode(activity: Activity) {
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    fun lockScreen(activity: Activity, overlayView: View) {
        overlayView.visibility = View.VISIBLE
        overlayView.setOnTouchListener { _, _ -> true }
        Toast.makeText(activity, "Screen Locked", Toast.LENGTH_SHORT).show()
    }

    fun unlockScreen(activity: Activity, overlayView: View) {
        overlayView.visibility = View.GONE
        overlayView.setOnTouchListener(null)
        Toast.makeText(activity, "Screen Unlocked", Toast.LENGTH_SHORT).show()
    }
}