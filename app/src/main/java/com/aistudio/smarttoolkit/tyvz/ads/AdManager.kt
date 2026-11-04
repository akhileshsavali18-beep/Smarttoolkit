package com.aistudio.smarttoolkit.tyvz.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayAdSize
import com.unity3d.mediation.LevelPlayBannerAdView
import com.unity3d.mediation.LevelPlayBannerAdViewListener
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest
import com.unity3d.mediation.LevelPlayInterstitialAd
import com.unity3d.mediation.LevelPlayInterstitialAdListener
import com.unity3d.mediation.LevelPlayReward
import com.unity3d.mediation.LevelPlayRewardedAd
import com.unity3d.mediation.LevelPlayRewardedAdListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdManager {
    private const val TAG = "AdManager"

    @Volatile private var initialized = false
    @Volatile private var initializing = false
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    private var interstitialAd: LevelPlayInterstitialAd? = null
    private var rewardedAd: LevelPlayRewardedAd? = null
    private var interstitialLoading = false
    private var rewardedLoading = false
    private var pendingRewardCallback: ((Int, String) -> Unit)? = null
    private var pendingDismissCallback: (() -> Unit)? = null
    private var pendingFailureCallback: ((String) -> Unit)? = null

    fun isInitializedForBanner(): Boolean = initialized

    fun initialize(context: Context) {
        if (!ConsentManager.canRequestAdsNow()) {
            Log.d(TAG, "Waiting for privacy consent before LevelPlay initialization")
            return
        }
        if (initialized || initializing) return

        initializing = true
        try {
            val request = LevelPlayInitRequest.Builder(AdConfig.APP_KEY).build()
            LevelPlay.init(context.applicationContext, request, object : LevelPlayInitListener {
                override fun onInitSuccess(configuration: com.unity3d.mediation.LevelPlayConfiguration) {
                    initializing = false
                    initialized = true
                    _isInitialized.value = true
                    Log.d(TAG, "LevelPlay initialized")
                    createInterstitialAd()
                    createRewardedAd()
                }

                override fun onInitFailed(error: LevelPlayInitError) {
                    initializing = false
                    initialized = false
                    _isInitialized.value = false
                    Log.e(TAG, "LevelPlay initialization failed: $error")
                }
            })
        } catch (error: Throwable) {
            initializing = false
            Log.e(TAG, "LevelPlay initialization error", error)
        }
    }

    private fun createInterstitialAd() {
        if (!initialized || interstitialAd != null) return
        val ad = LevelPlayInterstitialAd(AdConfig.INTERSTITIAL_AD_ID)
        ad.setListener(object : LevelPlayInterstitialAdListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                interstitialLoading = false
                Log.d(TAG, "Interstitial loaded")
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                interstitialLoading = false
                Log.w(TAG, "Interstitial load failed: $error")
            }

            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) = Unit

            override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
                Log.w(TAG, "Interstitial display failed: $error")
                pendingFailureCallback?.invoke(error.toString())
                pendingFailureCallback = null
                pendingDismissCallback?.invoke()
                pendingDismissCallback = null
                loadInterstitialAd()
            }

            override fun onAdClicked(adInfo: LevelPlayAdInfo) = Unit

            override fun onAdClosed(adInfo: LevelPlayAdInfo) {
                pendingDismissCallback?.invoke()
                pendingDismissCallback = null
                pendingFailureCallback = null
                loadInterstitialAd()
            }

            override fun onAdInfoChanged(adInfo: LevelPlayAdInfo) = Unit
        })
        interstitialAd = ad
        loadInterstitialAd()
    }

    fun loadInterstitialAd(context: Context? = null) {
        if (!initialized) {
            context?.let(::initialize)
            return
        }
        val ad = interstitialAd ?: run {
            createInterstitialAd()
            return
        }
        if (ad.isAdReady() || interstitialLoading) return
        interstitialLoading = true
        try {
            ad.loadAd()
        } catch (error: Throwable) {
            interstitialLoading = false
            Log.w(TAG, "Could not load interstitial", error)
        }
    }

    fun showInterstitialAd(
        activity: Activity,
        isPremium: Boolean,
        onAdDismissed: () -> Unit = {},
        onAdFailed: ((String) -> Unit)? = null
    ) {
        if (isPremium) {
            onAdDismissed()
            return
        }
        val ad = interstitialAd
        if (ad == null || !ad.isAdReady()) {
            loadInterstitialAd(activity)
            onAdDismissed()
            return
        }
        pendingDismissCallback = onAdDismissed
        pendingFailureCallback = onAdFailed
        try {
            ad.showAd(activity)
        } catch (error: Throwable) {
            pendingDismissCallback = null
            pendingFailureCallback = null
            onAdFailed?.invoke(error.message ?: "Unable to show interstitial")
            onAdDismissed()
            loadInterstitialAd()
        }
    }

    private fun createRewardedAd() {
        if (!initialized || rewardedAd != null) return
        val ad = LevelPlayRewardedAd(AdConfig.REWARDED_AD_ID)
        ad.setListener(object : LevelPlayRewardedAdListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                rewardedLoading = false
                Log.d(TAG, "Rewarded ad loaded")
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                rewardedLoading = false
                Log.w(TAG, "Rewarded load failed: $error")
            }

            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) = Unit

            override fun onAdRewarded(reward: LevelPlayReward, adInfo: LevelPlayAdInfo) {
                pendingRewardCallback?.invoke(reward.amount, reward.name)
                pendingRewardCallback = null
            }

            override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
                pendingFailureCallback?.invoke(error.toString())
                pendingRewardCallback = null
                pendingFailureCallback = null
                pendingDismissCallback?.invoke()
                pendingDismissCallback = null
                loadRewardedAd()
            }

            override fun onAdClicked(adInfo: LevelPlayAdInfo) = Unit

            override fun onAdClosed(adInfo: LevelPlayAdInfo) {
                pendingDismissCallback?.invoke()
                pendingDismissCallback = null
                pendingFailureCallback = null
                loadRewardedAd()
            }

            override fun onAdInfoChanged(adInfo: LevelPlayAdInfo) = Unit
        })
        rewardedAd = ad
        loadRewardedAd()
    }

    fun isRewardedAdReady(): Boolean = rewardedAd?.isAdReady() == true

    fun loadRewardedAd(context: Context? = null, onLoaded: (() -> Unit)? = null) {
        if (!initialized) {
            context?.let(::initialize)
            return
        }
        val ad = rewardedAd ?: run {
            createRewardedAd()
            return
        }
        if (ad.isAdReady()) {
            onLoaded?.invoke()
            return
        }
        if (rewardedLoading) return
        rewardedLoading = true
        try {
            ad.loadAd()
        } catch (error: Throwable) {
            rewardedLoading = false
            Log.w(TAG, "Could not load rewarded ad", error)
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: (Int, String) -> Unit,
        onAdDismissed: () -> Unit = {},
        onAdFailed: ((String) -> Unit)? = null
    ) {
        if (!ConsentManager.canRequestAdsNow()) {
            onAdFailed?.invoke("Ads are unavailable until privacy consent is completed.")
            onAdDismissed()
            return
        }
        val ad = rewardedAd
        if (ad == null || !ad.isAdReady()) {
            loadRewardedAd(activity)
            onAdFailed?.invoke("Video ad is still loading. Please try again shortly.")
            return
        }
        pendingRewardCallback = onUserEarnedReward
        pendingDismissCallback = onAdDismissed
        pendingFailureCallback = onAdFailed
        try {
            ad.showAd(activity)
        } catch (error: Throwable) {
            pendingRewardCallback = null
            pendingDismissCallback = null
            pendingFailureCallback = null
            onAdFailed?.invoke(error.message ?: "Unable to show rewarded ad")
            onAdDismissed()
            loadRewardedAd()
        }
    }
}

@Composable
fun StickyBannerAd(
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val canRequestAds by ConsentManager.canRequestAds.collectAsState()
    val levelPlayReady by AdManager.isInitialized.collectAsState()
    if (isPremium || !canRequestAds || !levelPlayReady) return

    var adFailed by remember { mutableStateOf(false) }

    if (!adFailed) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    val config = LevelPlayBannerAdView.Config.Builder()
                        .setAdSize(LevelPlayAdSize.BANNER)
                        .build()
                    LevelPlayBannerAdView(context, AdConfig.BANNER_AD_ID, config).apply {
                        setBannerListener(object : LevelPlayBannerAdViewListener {
                            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                                adFailed = false
                            }

                            override fun onAdLoadFailed(error: LevelPlayAdError) {
                                Log.w("StickyBannerAd", "Banner load failed: $error")
                                adFailed = true
                            }

                            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) = Unit

                            override fun onAdDisplayFailed(adInfo: LevelPlayAdInfo, error: LevelPlayAdError) {
                                Log.w("StickyBannerAd", "Banner display failed: $error")
                                adFailed = true
                            }

                            override fun onAdClicked(adInfo: LevelPlayAdInfo) = Unit
                            override fun onAdExpanded(adInfo: LevelPlayAdInfo) = Unit
                            override fun onAdCollapsed(adInfo: LevelPlayAdInfo) = Unit
                            override fun onAdLeftApplication(adInfo: LevelPlayAdInfo) = Unit
                        })
                        loadAd()
                    }
                },
                onRelease = { banner ->
                    try {
                        banner.destroy()
                    } catch (error: Throwable) {
                        Log.w("StickyBannerAd", "Banner destroy failed", error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}
