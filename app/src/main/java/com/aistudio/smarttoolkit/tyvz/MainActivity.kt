package com.aistudio.smarttoolkit.tyvz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class MainActivity : ComponentActivity() {
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

