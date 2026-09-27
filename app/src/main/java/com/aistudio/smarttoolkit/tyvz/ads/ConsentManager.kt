package com.aistudio.smarttoolkit.tyvz.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google UMP consent manager.
 *
 * Consent information is refreshed on every app launch.
 * Ad requests should only be made after canRequestAds() is true.
 */
object ConsentManager {
    private const val TAG = "ConsentManager"

    private lateinit var consentInformation: ConsentInformation

    private val _canRequestAds = MutableStateFlow(false)
    val canRequestAds: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    fun requestConsent(activity: Activity, onComplete: (() -> Unit)? = null) {
        consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                _privacyOptionsRequired.value =
                    consentInformation.privacyOptionsRequirementStatus ==
                        ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.message}")
                    }
                    updateAdRequestState()
                    onComplete?.invoke()
                }
            },
            { requestConsentError ->
                Log.w(TAG, "Consent info update error: ${requestConsentError.message}")
                // If an error occurs, UMP may still have a previous valid consent state.
                updateAdRequestState()
                onComplete?.invoke()
            }
        )
    }

    fun showPrivacyOptions(activity: Activity, onComplete: (() -> Unit)? = null) {
        if (!::consentInformation.isInitialized) {
            Log.w(TAG, "Consent information is not initialized yet")
            return
        }

        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error: ${formError.message}")
            }
            updateAdRequestState()
            onComplete?.invoke()
        }
    }

    fun canRequestAdsNow(): Boolean {
        return ::consentInformation.isInitialized &&
            consentInformation.canRequestAds()
    }

    private fun updateAdRequestState() {
        _canRequestAds.value = canRequestAdsNow()
    }
}
