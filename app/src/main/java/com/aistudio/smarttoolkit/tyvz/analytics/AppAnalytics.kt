package com.aistudio.smarttoolkit.tyvz.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Helper to log Firebase Analytics events safely throughout APS TOOLS.
 */
object AppAnalytics {
    private const val TAG = "AppAnalytics"

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize(context: Context) {
        if (firebaseAnalytics == null) {
            try {
                firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
                Log.d(TAG, "FirebaseAnalytics initialized")
            } catch (e: Throwable) {
                Log.w(TAG, "Failed to initialize FirebaseAnalytics: ${e.message}")
            }
        }
    }

    /**
     * Log when any tool screen is opened.
     */
    fun logToolOpened(context: Context, toolName: String, category: String? = null) {
        try {
            if (firebaseAnalytics == null) {
                initialize(context)
            }
            val bundle = Bundle().apply {
                putString("tool_name", toolName)
                if (category != null) {
                    putString("category", category)
                }
            }
            firebaseAnalytics?.logEvent("tool_opened", bundle)
            Log.d(TAG, "Logged event tool_opened: $toolName")
        } catch (e: Throwable) {
            Log.w(TAG, "Error logging tool_opened event: ${e.message}")
        }
    }

    /**
     * Log user actions such as saving an image, copying text, etc.
     */
    fun logToolAction(context: Context, toolName: String, actionName: String) {
        try {
            if (firebaseAnalytics == null) {
                initialize(context)
            }
            val bundle = Bundle().apply {
                putString("tool_name", toolName)
                putString("action", actionName)
            }
            firebaseAnalytics?.logEvent("tool_action", bundle)
            Log.d(TAG, "Logged event tool_action: $toolName - $actionName")
        } catch (e: Throwable) {
            Log.w(TAG, "Error logging tool_action event: ${e.message}")
        }
    }
}
