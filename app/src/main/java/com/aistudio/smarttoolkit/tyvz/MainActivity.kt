package com.aistudio.smarttoolkit.tyvz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import android.widget.Toast
import com.aistudio.smarttoolkit.tyvz.billing.RazorpayPaymentApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.aistudio.smarttoolkit.tyvz.ads.AdManager
import com.aistudio.smarttoolkit.tyvz.ads.ConsentManager
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager
import com.aistudio.smarttoolkit.tyvz.model.ThemeMode
import com.aistudio.smarttoolkit.tyvz.ui.ApsToolsApp
import com.aistudio.smarttoolkit.tyvz.ui.screens.MainDashboardScreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsToolsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
  fun startIntroPayment() {
    lifecycleScope.launch {
      try {
        val order = RazorpayPaymentApi.createIntroOrder()
        Checkout.preload(applicationContext)
        Checkout.setKeyID(order.keyId)
        val options = JSONObject().apply {
          put("name", "APS TOOLS")
          put("description", "APS TOOLS PRO - 30 days")
          put("order_id", order.orderId)
          put("currency", order.currency)
          put("amount", order.amount)
          put("theme", JSONObject().put("color", "#2563EB"))
          put("prefill", JSONObject())
        }
        Checkout.open(this@MainActivity, options)
      } catch (error: Throwable) {
        Toast.makeText(this@MainActivity, error.message ?: "ಪಾವತಿ ಆರಂಭಿಸಲು ಸಾಧ್ಯವಾಗಲಿಲ್ಲ.", Toast.LENGTH_LONG).show()
      }
    }
  }

  override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
    lifecycleScope.launch {
      try {
        val orderId = paymentData?.orderId.orEmpty()
        val signature = paymentData?.signature.orEmpty()
        val verified = RazorpayPaymentApi.verifyIntroPayment(orderId, paymentId.orEmpty(), signature)
        if (!verified) throw IllegalStateException("ಪಾವತಿ ಸರ್ವರ್‌ನಲ್ಲಿ ದೃಢೀಕರಿಸಲ್ಪಟ್ಟಿಲ್ಲ.")
        BillingManager.getInstance(applicationContext).startServerEntitlementSync()
        Toast.makeText(this@MainActivity, "ಪಾವತಿ ದೃಢೀಕರಣ ಪೂರ್ಣಗೊಂಡಿದೆ. PRO ಸ್ಥಿತಿ ನವೀಕರಿಸಲಾಗುತ್ತಿದೆ.", Toast.LENGTH_LONG).show()
      } catch (error: Throwable) {
        Toast.makeText(this@MainActivity, error.message ?: "ಪಾವತಿ ಪರಿಶೀಲನೆ ವಿಫಲವಾಗಿದೆ.", Toast.LENGTH_LONG).show()
      }
    }
  }

  override fun onPaymentError(code: Int, description: String?, paymentData: PaymentData?) {
    Toast.makeText(this, description ?: "ಪಾವತಿ ರದ್ದಾಗಿದೆ ಅಥವಾ ವಿಫಲವಾಗಿದೆ.", Toast.LENGTH_LONG).show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      ApsToolsTheme {
        ApsToolsApp()
      }
    }

    // Refresh privacy/consent information before initializing AdMob.
    ConsentManager.requestConsent(this) {
      runOnUiThread {
        if (ConsentManager.canRequestAdsNow()) {
          AdManager.initialize(applicationContext)
        }
      }
    }

    // PRO access is read from the server-owned Firebase entitlement document.
    BillingManager.getInstance(applicationContext).startServerEntitlementSync()

    lifecycleScope.launch(Dispatchers.IO) {
      initBillingClient()
    }
  }

  override fun onResume() {
    super.onResume()
    // Re-query Google Play whenever the app returns to the foreground so that
    // subscription expiry/cancellation is reflected promptly.
    try {
      BillingManager.getInstance(applicationContext).refreshSubscriptionEntitlement()
    } catch (e: Throwable) {
      // Non-blocking billing refresh.
    }
  }

  private fun initBillingClient() {
    try {
      BillingManager.getInstance(applicationContext).startConnection()
    } catch (e: Throwable) {
      // Background non-blocking initialization
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ApsToolsTheme { MainDashboardScreen() }
}

