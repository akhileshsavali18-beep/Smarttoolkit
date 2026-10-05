package com.aistudio.smarttoolkit.tyvz.analytics

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.aistudio.smarttoolkit.tyvz.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Firebase Analytics helper plus a small Firestore mirror used by the
 * APS TOOLS private admin dashboard.
 *
 * Firestore stores anonymous installation/event identifiers only.
 */
object AppAnalytics {
    private const val TAG = "AppAnalytics"
    private const val PREFS = "aps_tools_analytics"
    private const val INSTALL_ID = "installation_id"

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var firestore: FirebaseFirestore? = null
    private var installationId: String? = null

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        if (firebaseAnalytics == null) {
            try {
                firebaseAnalytics = FirebaseAnalytics.getInstance(appContext)
            } catch (e: Throwable) {
                Log.w(TAG, "Firebase Analytics init failed: " + e.message)
            }
        }
        if (firestore == null) {
            try {
                firestore = FirebaseFirestore.getInstance()
            } catch (e: Throwable) {
                Log.w(TAG, "Firestore init failed: " + e.message)
            }
        }
        if (installationId == null) {
            installationId = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(INSTALL_ID, null)
                ?: java.util.UUID.randomUUID().toString().also {
                    appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        .edit().putString(INSTALL_ID, it).apply()
                }
        }
    }

    fun logAppOpened(context: Context) {
        try {
            initialize(context)
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
            mirrorEvent(context, "app_open")
            upsertInstallation(context)
        } catch (e: Throwable) {
            Log.w(TAG, "Error logging app_open: " + e.message)
        }
    }

    fun logToolOpened(context: Context, toolName: String, category: String? = null) {
        try {
            initialize(context)
            val bundle = Bundle().apply {
                putString("tool_name", toolName)
                if (category != null) putString("category", category)
            }
            firebaseAnalytics?.logEvent("tool_opened", bundle)
            mirrorEvent(context, "tool_opened", toolName, category)
            Log.d(TAG, "Logged event tool_opened: " + toolName)
        } catch (e: Throwable) {
            Log.w(TAG, "Error logging tool_opened event: " + e.message)
        }
    }

    fun logToolAction(context: Context, toolName: String, actionName: String) {
        try {
            initialize(context)
            val bundle = Bundle().apply {
                putString("tool_name", toolName)
                putString("action", actionName)
            }
            firebaseAnalytics?.logEvent("tool_action", bundle)
            mirrorEvent(context, "tool_action", toolName, null, actionName)
            Log.d(TAG, "Logged event tool_action: " + toolName + " - " + actionName)
        } catch (e: Throwable) {
            Log.w(TAG, "Error logging tool_action event: " + e.message)
        }
    }

    private fun mirrorEvent(
        context: Context,
        eventName: String,
        toolName: String? = null,
        category: String? = null,
        action: String? = null
    ) {
        val db = firestore ?: return
        val id = installationId ?: return
        val data = hashMapOf<String, Any>(
            "event_name" to eventName,
            "install_id" to id,
            "app_version" to BuildConfig.VERSION_NAME,
            "version_code" to BuildConfig.VERSION_CODE,
            "device_model" to Build.MODEL,
            "device_manufacturer" to Build.MANUFACTURER,
            "android_version" to Build.VERSION.RELEASE,
            "timestamp" to FieldValue.serverTimestamp()
        )
        if (!toolName.isNullOrBlank()) data["tool_name"] = toolName
        if (!category.isNullOrBlank()) data["category"] = category
        if (!action.isNullOrBlank()) data["action"] = action
        db.collection("analytics_events").add(data)
    }

    private fun upsertInstallation(context: Context) {
        val db = firestore ?: return
        val id = installationId ?: return
        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (_: Throwable) {
            null
        }

        val data = hashMapOf<String, Any>(
            "install_id" to id,
            "app_version" to BuildConfig.VERSION_NAME,
            "version_code" to BuildConfig.VERSION_CODE,
            "device_model" to Build.MODEL,
            "device_manufacturer" to Build.MANUFACTURER,
            "android_version" to Build.VERSION.RELEASE,
            "last_seen" to FieldValue.serverTimestamp()
        )
        if (!androidId.isNullOrBlank()) {
            data["android_id_hash"] = androidId.hashCode().toString(16)
        }

        db.collection("analytics_users").document(id).set(data, SetOptions.merge())
    }
}
