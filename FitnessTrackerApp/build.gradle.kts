buildscript {
    val kotlin_version by extra("1.8.21")
    repositories {
        mavenCentral()
        google()
        maven {url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.jd-alexander:LikeButton:0.2.3")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
       // classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application") version "8.3.2" apply false
    id ("com.android.library") version "8.3.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id ("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
}
