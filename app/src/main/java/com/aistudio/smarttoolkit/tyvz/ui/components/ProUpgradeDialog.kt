package com.aistudio.smarttoolkit.tyvz.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager.SubscriptionPlan
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProGold
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProGoldBg

@Composable
fun ProUpgradeDialog(
    billingManager: BillingManager,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isPremium by billingManager.isPremium.collectAsState()
    val productDetails by billingManager.premiumProductDetails.collectAsState()
    val billingStatus by billingManager.billingStatusMessage.collectAsState()
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.MONTHLY) }

    val monthlyPrice = billingManager.getPlanPriceText(SubscriptionPlan.MONTHLY)
    val yearlyPrice = billingManager.getPlanPriceText(SubscriptionPlan.YEARLY)
    val monthlyIntroPrice = billingManager.getMonthlyIntroPriceText()

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .testTag("pro_upgrade_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Pro",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isPremium) "APS TOOLS PRO ACTIVE" else "UPGRADE TO PRO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = if (isPremium) ProGold else MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = if (isPremium) {
                        "Ads removed and PRO tools unlocked"
                    } else {
                        "Go ad-free and unlock selected PRO tools with a monthly or yearly subscription"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProFeatureRow(Icons.Default.Block, "Zero Advertisements", "Remove banner & interstitial ads entirely")
                    ProFeatureRow(Icons.Default.PictureAsPdf, "Unlimited Multi-Page PDF", "Convert unlimited images with high DPI")
                    ProFeatureRow(Icons.Default.Star, "Selected PRO Tools", "Unlock tools marked with the PRO badge")
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (!isPremium) {
                    Text(
                        text = "Choose your plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PlanCard(
                        title = "Monthly",
                        price = monthlyPrice ?: "Available in Google Play",
                        detail = monthlyIntroPrice?.let { "$it for the first month • then ${monthlyPrice ?: "regular price"}/month" }
                            ?: "Monthly subscription",
                        selected = selectedPlan == SubscriptionPlan.MONTHLY,
                        onClick = { selectedPlan = SubscriptionPlan.MONTHLY },
                        testTag = "monthly_plan"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PlanCard(
                        title = "Yearly",
                        price = yearlyPrice ?: "Available in Google Play",
                        detail = "Yearly subscription",
                        selected = selectedPlan == SubscriptionPlan.YEARLY,
                        onClick = { selectedPlan = SubscriptionPlan.YEARLY },
                        testTag = "yearly_plan"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            activity?.let { billingManager.launchBillingFlow(it, selectedPlan) }
                        },
                        enabled = productDetails != null && activity != null,
                        colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("buy_pro_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPlan == SubscriptionPlan.MONTHLY) "Continue with Monthly" else "Continue with Yearly",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (billingStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = billingStatus.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ProGoldBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ProGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Premium Active", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF92400E))
                                Text("Subscription: ${BillingManager.PRODUCT_PRO}", fontSize = 11.sp, color = Color(0xFFB45309))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onDismissRequest, modifier = Modifier.testTag("close_pro_dialog")) {
                    Text("Close", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    detail: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ApsBlue.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, ApsBlue) else null,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(price, fontWeight = FontWeight.Black, fontSize = 20.sp, color = ApsBlue)
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = if (selected) "✓" else "",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = ApsBlue
            )
        }
    }
}

@Composable
private fun ProFeatureRow(icon: ImageVector, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFFEF3C7))
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
