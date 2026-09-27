package com.aistudio.smarttoolkit.tyvz.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(private val context: Context) : PurchasesUpdatedListener {

    enum class SubscriptionPlan {
        MONTHLY,
        YEARLY
    }

    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_PRO = "aps_tools_pro"

        // These are Google Play Console base-plan IDs.
        // Configure the same IDs in the subscription product.
        const val BASE_PLAN_MONTHLY = "monthly"
        const val BASE_PLAN_YEARLY = "yearly"

        @Volatile
        private var instance: BillingManager? = null

        fun getInstance(context: Context): BillingManager {
            return instance ?: synchronized(this) {
                instance ?: BillingManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // Play Billing is the source of truth. Premium starts as false until
    // Google Play confirms an active PURCHASED subscription.
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _premiumProductDetails = MutableStateFlow<ProductDetails?>(null)
    val premiumProductDetails: StateFlow<ProductDetails?> = _premiumProductDetails.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    init {
        startConnection()
    }

    fun startConnection() {
        scope.launch {
            try {
                if (billingClient.isReady) {
                    queryPurchases()
                    queryProductDetails()
                    return@launch
                }

                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "BillingClient connected successfully")
                            queryPurchases()
                            queryProductDetails()
                        } else {
                            Log.w(TAG, "BillingClient setup failed: ${billingResult.debugMessage}")
                            _billingStatusMessage.value =
                                "Google Play Billing unavailable: ${billingResult.debugMessage}"
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Log.w(TAG, "Billing service disconnected.")
                    }
                })
            } catch (e: Throwable) {
                Log.w(TAG, "Error starting billing client connection", e)
            }
        }
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PRO)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val details = queryProductDetailsList.firstOrNull {
                    it.productId == PRODUCT_PRO
                }
                _premiumProductDetails.value = details

                Log.d(
                    TAG,
                    "Subscription ProductDetails loaded: ${details?.title}, " +
                            "offers=${details?.subscriptionOfferDetails?.size ?: 0}"
                )
            } else {
                Log.w(TAG, "Failed to query subscription details: ${billingResult.debugMessage}")
            }
        }
    }

    fun getSubscriptionOffer(plan: SubscriptionPlan): ProductDetails.SubscriptionOfferDetails? {
        val details = _premiumProductDetails.value ?: return null
        val basePlanId = when (plan) {
            SubscriptionPlan.MONTHLY -> BASE_PLAN_MONTHLY
            SubscriptionPlan.YEARLY -> BASE_PLAN_YEARLY
        }
        val offers = details.subscriptionOfferDetails
            ?.filter { it.basePlanId == basePlanId }
            .orEmpty()

        // For monthly, prefer an eligible introductory offer when Play returns one.
        return when (plan) {
            SubscriptionPlan.MONTHLY -> offers.firstOrNull {
                it.pricingPhases.pricingPhaseList.size > 1
            } ?: offers.firstOrNull()
            SubscriptionPlan.YEARLY -> offers.firstOrNull { it.offerId == null }
                ?: offers.firstOrNull()
        }
    }

    fun getPlanPriceText(plan: SubscriptionPlan): String? {
        val offer = getSubscriptionOffer(plan) ?: return null
        return offer.pricingPhases.pricingPhaseList.lastOrNull()?.formattedPrice
    }

    fun getMonthlyIntroPriceText(): String? {
        val offer = getSubscriptionOffer(SubscriptionPlan.MONTHLY) ?: return null
        val phases = offer.pricingPhases.pricingPhaseList
        return if (phases.size > 1) phases.firstOrNull()?.formattedPrice else null
    }

    /**
     * Re-check the user's current subscription with Google Play.
     * This should be called when the app returns to the foreground as well as
     * after BillingClient connects, so expiry/cancellation is reflected without
     * relying on a locally persisted premium flag.
     */
    fun refreshSubscriptionEntitlement() {
        if (billingClient.isReady) {
            queryPurchases()
        } else {
            startConnection()
        }
    }

    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.d(TAG, "Skipping subscription query: BillingClient is not ready")
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // queryPurchasesAsync returns currently owned Play subscriptions.
                // A cancelled subscription remains PURCHASED until its expiry, so
                // it correctly stays premium until Play stops returning it.
                val activePremium = purchases.any { purchase ->
                    purchase.products.contains(PRODUCT_PRO) &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }

                setPremiumState(activePremium)

                // Acknowledge any purchased subscription that has not been acknowledged.
                purchases
                    .filter {
                        it.products.contains(PRODUCT_PRO) &&
                                it.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    .forEach(::acknowledgeIfNeeded)

                Log.d(TAG, "Subscription entitlement refreshed. premium=$activePremium")
            } else {
                // Do not manufacture an entitlement when Play cannot be queried.
                // Keep the current in-memory state and retry on the next foreground
                // refresh/reconnection rather than treating a transient error as expiry.
                Log.w(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Backward-compatible entry point for the existing UI.
     * Step 12 will call the explicit MONTHLY/YEARLY overload.
     */
    fun launchBillingFlow(activity: Activity): Boolean {
        return launchBillingFlow(activity, SubscriptionPlan.MONTHLY)
    }

    fun launchBillingFlow(
        activity: Activity,
        plan: SubscriptionPlan
    ): Boolean {
        val details = _premiumProductDetails.value
        if (details == null) {
            _billingStatusMessage.value = "Connecting to Google Play..."
            startConnection()
            return false
        }

        val basePlanId = when (plan) {
            SubscriptionPlan.MONTHLY -> BASE_PLAN_MONTHLY
            SubscriptionPlan.YEARLY -> BASE_PLAN_YEARLY
        }

        val selectedOffer = getSubscriptionOffer(plan)
        if (selectedOffer == null) {
            _billingStatusMessage.value =
                "Selected subscription plan is not available yet."
            Log.w(TAG, "No offer found for base plan: $basePlanId")
            return false
        }

        val offerToken = selectedOffer.offerToken
        if (offerToken.isBlank()) {
            _billingStatusMessage.value = "Subscription offer is unavailable."
            return false
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _billingStatusMessage.value = "Purchase error: ${result.debugMessage}"
        }
        return result.responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _billingStatusMessage.value = "Purchase cancelled"
        } else {
            _billingStatusMessage.value = "Purchase error: ${billingResult.debugMessage}"
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.products.contains(PRODUCT_PRO)) return

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            setPremiumState(true)
            acknowledgeIfNeeded(purchase)
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        scope.launch {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { ackResult ->
                if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Subscription purchase acknowledged.")
                } else {
                    Log.w(
                        TAG,
                        "Subscription acknowledgement failed: ${ackResult.debugMessage}"
                    )
                }
            }
        }
    }

    private fun setPremiumState(isPremium: Boolean) {
        _isPremium.value = isPremium
    }

    fun clearStatusMessage() {
        _billingStatusMessage.value = null
    }
}
