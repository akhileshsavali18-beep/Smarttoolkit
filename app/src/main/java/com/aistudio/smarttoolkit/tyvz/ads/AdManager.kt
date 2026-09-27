package com.aistudio.smarttoolkit.tyvz.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!ConsentManager.canRequestAdsNow()) {
            Log.d(TAG, "AdMob initialization skipped until UMP consent allows ads")
            return
        }
        if (!isInitialized) {
            try {
                com.google.android.gms.ads.MobileAds.initialize(context.applicationContext) { status ->
                    Log.d(TAG, "AdMob MobileAds initialized: $status")
                    isInitialized = true
                    loadInterstitialAd(context.applicationContext)
                    loadRewardedAd(context.applicationContext)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing MobileAds", e)
            }
        }
    }

    fun loadInterstitialAd(context: Context) {
        if (!ConsentManager.canRequestAdsNow()) return
        if (interstitialAd != null || isAdLoading) return

        isAdLoading = true
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context.applicationContext,
                AdConfig.INTERSTITIAL_AD_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isAdLoading = false
                        Log.d(TAG, "Interstitial ad successfully loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isAdLoading = false
                        Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to load interstitial ad", e)
            isAdLoading = false
        }
    }

    fun showInterstitialAd(
        activity: Activity,
        isPremium: Boolean,
        onAdDismissed: () -> Unit = {},
        onAdFailed: ((String) -> Unit)? = null
    ) {
        if (isPremium) {
            // Premium users do not see ads
            onAdDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdFailed?.invoke(adError.message)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed fullscreen")
                }
            }
            try {
                ad.show(activity)
            } catch (e: Throwable) {
                Log.w(TAG, "Error calling ad.show", e)
                interstitialAd = null
                loadInterstitialAd(activity)
                onAdFailed?.invoke(e.message ?: "Unknown show error")
                onAdDismissed()
            }
        } else {
            // If ad is not ready, proceed smoothly without blocking the user
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null

    fun loadRewardedAd(context: Context, onLoaded: (() -> Unit)? = null) {
        if (!ConsentManager.canRequestAdsNow()) return
        if (rewardedAd != null) {
            onLoaded?.invoke()
            return
        }
        if (isRewardedLoading) return

        isRewardedLoading = true
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context.applicationContext,
                AdConfig.REWARDED_AD_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isRewardedLoading = false
                        Log.d(TAG, "Rewarded ad successfully loaded")
                        onLoaded?.invoke()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        isRewardedLoading = false
                        Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to initiate loadRewardedAd", e)
            isRewardedLoading = false
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
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdFailed?.invoke(adError.message)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad showed fullscreen")
                }
            }
            try {
                ad.show(activity) { rewardItem ->
                    Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                    onUserEarnedReward(rewardItem.amount, rewardItem.type)
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Error showing rewarded ad", e)
                rewardedAd = null
                loadRewardedAd(activity)
                onAdFailed?.invoke(e.message ?: "Failed to display ad")
                onAdDismissed()
            }
        } else {
            loadRewardedAd(activity)
            onAdFailed?.invoke("Video ad is still loading. Please try again in a few seconds!")
        }
    }
}

@Composable
fun StickyBannerAd(
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val canRequestAds by ConsentManager.canRequestAds.collectAsState()
    if (isPremium || !canRequestAds) return

    var isAdFailed by remember { mutableStateOf(false) }

    if (!isAdFailed) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = AdConfig.BANNER_AD_ID
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adListener = object : com.google.android.gms.ads.AdListener() {
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                Log.w("StickyBannerAd", "Banner ad failed to load: ${error.message}")
                                isAdFailed = true
                            }

                            override fun onAdLoaded() {
                                isAdFailed = false
                            }
                        }
                        try {
                            loadAd(AdRequest.Builder().build())
                        } catch (e: Throwable) {
                            Log.w("StickyBannerAd", "Failed to load banner ad request", e)
                            isAdFailed = true
                        }
                    }
                },
                update = { },
                onRelease = { adView ->
                    try {
                        adView.destroy()
                    } catch (e: Throwable) {
                        Log.w("StickyBannerAd", "Failed to destroy adView on release", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}
