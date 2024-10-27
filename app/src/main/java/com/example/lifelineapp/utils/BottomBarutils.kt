package com.example.lifelineapp

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import nl.joery.animatedbottombar.AnimatedBottomBar

object BottomBarUtils {

    private const val PREFS_NAME = "bottomBarPrefs"
    private const val SELECTED_TAB_INDEX = "selectedTabIndex"

    fun setupBottomBar(context: Context, bottomBar: AnimatedBottomBar) {
        // Retrieve and set the last selected tab (if any)
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTabIndex = sharedPreferences.getInt(SELECTED_TAB_INDEX, -1)
        if (savedTabIndex != -1) {
            bottomBar.selectTabAt(savedTabIndex, animate = false)
        }

        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                Log.d("BottomBarUtils", "Tab ID: ${newTab.id}, Last Tab ID: ${lastTab?.id}")

                // Save the selected tab index
                sharedPreferences.edit().putInt(SELECTED_TAB_INDEX, newIndex).apply()

                // Allow the animation to complete before switching activities
                Handler(Looper.getMainLooper()).postDelayed({
                    when (newTab.id) {
                        R.id.nav_home -> context.startActivity(Intent(context, MainActivity::class.java))
                        R.id.nav_health -> context.startActivity(Intent(context, HealthActivity::class.java))
                        R.id.nav_chat -> context.startActivity(Intent(context, ProfileActivity::class.java))
                        R.id.nav_calendar -> context.startActivity(Intent(context, CalendarActivity::class.java))
                        R.id.nav_call -> context.startActivity(Intent(context, CallActivity::class.java))
                        else -> Log.e("BottomBarUtils", "Unknown tab selected: ${newTab.id}")
                    }
                }, 200) // 200 milliseconds delay (adjust if necessary)
            }

             fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                // Handle reselection if needed
            }
        })
    }
}
