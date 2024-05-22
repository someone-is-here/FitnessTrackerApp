package com.example.fitnesstrackerapp.other

import android.graphics.Color

object Constants {
    const val RUNNING_DATABASE_NAME = "fitnesstrackerapp_db"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 1000L
    const val FASTEST_LOCATION_INTERVAL = 500L

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val POLYLINE_COLOR = Color.GREEN
    const val POLYLINE_WIDTH = 8f
    const val CIRCLE_RADIUS = 8
    const val MAP_ZOOM = 16f
    const val STROKE_WIDTH = 3f
    const val STROKE_COLOR = Color.BLUE

    const val TIME_UPDATE_INTERVAL = 1000L

    const val SHARED_PREFERENCES_NAME = "TEAM_SH_PREF"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_UID = "KEY_UID"
    const val KEY_USERNAME = "KEY_USERNAME"
    const val KEY_EMAIL = "KEY_EMAIL"
    const val KEY_BIO = "KEY_BIO"
    const val KEY_BIRTHDAY = "KEY_BIRTHDAY"
    const val KEY_COUNTRY = "KEY_COUNTRY"
    const val KEY_HEIGHT = "KEY_HEIGHT"
    const val KEY_WEIGHT = "KEY_WEIGHT"
    const val KEY_PROFILE_SAVED = "KEY_PROFILE_SAVED"
    const val KEY_BACKGROUND_SAVED = "KEY_BACKGROUND_SAVED"
}