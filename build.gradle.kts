// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // Add the Google Services classpath for Firebase
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

allprojects {


    dependencies{

        modules {
            module("com.google.android:flexbox") {
                replacedBy("com.google.android.flexbox:flexbox")
            }
        }
    }
}