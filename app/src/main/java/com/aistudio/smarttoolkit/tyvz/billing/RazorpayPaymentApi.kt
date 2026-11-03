package com.aistudio.smarttoolkit.tyvz.billing

import com.aistudio.smarttoolkit.tyvz.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Authenticated client for the Cloudflare payment Worker.
 * The base URL stays blank until the Worker is configured and deployed.
 * Never put Razorpay Key Secret or Firebase service-account credentials in this app.
 */
object RazorpayPaymentApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(45, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    data class IntroOrder(val orderId: String, val amount: Int, val currency: String, val keyId: String)
    data class SubscriptionCheckout(val subscriptionId: String, val keyId: String, val shortUrl: String)

    private fun endpoint(path: String): String {
        val base = BuildConfig.PAYMENT_API_BASE_URL.trim().trimEnd('/')
        if (base.isBlank() || !base.startsWith("https://")) {
            throw IllegalStateException("ಪಾವತಿ ಸೇವೆ ಇನ್ನೂ ಸಿದ್ಧವಾಗಿಲ್ಲ. ದಯವಿಟ್ಟು ನಂತರ ಪ್ರಯತ್ನಿಸಿ.")
        }
        return "$base$path"
    }

    private suspend fun firebaseIdToken(): String = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            continuation.resumeWithException(IllegalStateException("ಪಾವತಿಗೆ ಮೊದಲು ನಿಮ್ಮ ಖಾತೆಗೆ ಪ್ರವೇಶಿಸಿ."))
            return@suspendCancellableCoroutine
        }
        user.getIdToken(false)
            .addOnSuccessListener { result ->
                val token = result.token
                if (token.isNullOrBlank()) {
                    if (continuation.isActive) continuation.resumeWithException(IOException("ಪ್ರವೇಶ ಪರಿಶೀಲನೆ ವಿಫಲವಾಗಿದೆ."))
                } else if (continuation.isActive) continuation.resume(token)
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
    }

    suspend fun createIntroOrder(): IntroOrder = withContext(Dispatchers.IO) {
        val token = firebaseIdToken()
        val response = execute("/orders", token, JSONObject().put("plan", "intro"))
        val orderId = response.optString("orderId")
        val keyId = response.optString("keyId")
        val amount = response.optInt("amount")
        val currency = response.optString("currency")
        if (orderId.isBlank() || keyId.isBlank() || amount != 900 || currency != "INR") {
            throw IOException("ಸರ್ವರ್‌ನಿಂದ ಬಂದ ಪಾವತಿ ವಿವರಗಳು ಸರಿಯಾಗಿಲ್ಲ.")
        }
        IntroOrder(orderId, amount, currency, keyId)
    }

    suspend fun createSubscription(plan: String): SubscriptionCheckout = withContext(Dispatchers.IO) {
        require(plan == "monthly" || plan == "yearly") { "ಅಮಾನ್ಯ ಚಂದಾದಾರಿಕೆ ಯೋಜನೆ." }
        val token = firebaseIdToken()
        val response = execute("/subscriptions/create", token, JSONObject().put("plan", plan))
        val subscriptionId = response.optString("subscriptionId")
        val keyId = response.optString("keyId")
        val shortUrl = response.optString("shortUrl")
        if (subscriptionId.isBlank() || keyId.isBlank()) {
            throw IOException("ಸರ್ವರ್‌ನಿಂದ ಚಂದಾದಾರಿಕೆ ವಿವರಗಳು ಸರಿಯಾಗಿ ಬಂದಿಲ್ಲ.")
        }
        SubscriptionCheckout(subscriptionId, keyId, shortUrl)
    }

    suspend fun verifyIntroPayment(orderId: String, paymentId: String, signature: String): Boolean =
        withContext(Dispatchers.IO) {
            if (orderId.isBlank() || paymentId.isBlank() || signature.isBlank()) {
                throw IllegalArgumentException("ಪಾವತಿ ಪರಿಶೀಲನೆಗೆ ಅಗತ್ಯವಿರುವ ಮಾಹಿತಿ ಕಾಣೆಯಾಗಿದೆ.")
            }
            val token = firebaseIdToken()
            val payload = JSONObject()
                .put("razorpay_order_id", orderId)
                .put("razorpay_payment_id", paymentId)
                .put("razorpay_signature", signature)
            val response = execute("/verify", token, payload)
            response.optBoolean("verified", false) && response.optString("status") == "active"
        }

    private fun execute(path: String, token: String, payload: JSONObject): JSONObject {
        val request = Request.Builder()
            .url(endpoint(path))
            .header("Authorization", "Bearer $token")
            .post(payload.toString().toRequestBody(jsonType))
            .build()
        client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val code = runCatching { JSONObject(raw).optString("error") }.getOrDefault("")
                val message = when (code) {
                    "INTRO_ALREADY_REDEEMED" -> "ನಿಮ್ಮ ಖಾತೆಯಲ್ಲಿ ಪರಿಚಯಾತ್ಮಕ ಕೊಡುಗೆ ಈಗಾಗಲೇ ಬಳಸಲಾಗಿದೆ."
                    "UNAUTHORIZED" -> "ನಿಮ್ಮ ಪ್ರವೇಶ ಅವಧಿ ಮುಗಿದಿದೆ. ಮತ್ತೆ ಪ್ರವೇಶಿಸಿ."
                    else -> "ಪಾವತಿ ವಿನಂತಿ ವಿಫಲವಾಗಿದೆ. (HTTP ${response.code})"
                }
                throw IOException(message)
            }
            return try { JSONObject(raw) }
            catch (_: Exception) { throw IOException("ಪಾವತಿ ಸೇವೆಯಿಂದ ಸರಿಯಾದ ಉತ್ತರ ಬಂದಿಲ್ಲ.") }
        }
    }
}