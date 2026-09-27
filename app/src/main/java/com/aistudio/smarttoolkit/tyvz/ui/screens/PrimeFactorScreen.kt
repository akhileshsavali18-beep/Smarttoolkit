package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.PrimeGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.PrimeGreenBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import kotlin.math.sqrt

data class PrimeFactorResult(
    val number: Long,
    val isPrime: Boolean,
    val primeFactors: Map<Long, Int>,
    val allFactors: List<Long>,
    val nextPrime: Long,
    val prevPrime: Long?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeFactorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var numberInput by remember { mutableStateOf("84") }

    fun checkPrime(n: Long): Boolean {
        if (n <= 1) return false
        if (n <= 3) return true
        if (n % 2 == 0L || n % 3 == 0L) return false
        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2) == 0L) return false
            i += 6
        }
        return true
    }

    val result by remember(numberInput) {
        derivedStateOf {
            val n = numberInput.trim().toLongOrNull()
            if (n == null || n <= 0L || n > 10_000_000_000L) {
                null
            } else {
                val isPrime = checkPrime(n)

                // Prime factorization
                val factorsMap = mutableMapOf<Long, Int>()
                var temp = n
                var divisor = 2L
                while (divisor * divisor <= temp) {
                    while (temp % divisor == 0L) {
                        factorsMap[divisor] = (factorsMap[divisor] ?: 0) + 1
                        temp /= divisor
                    }
                    divisor = if (divisor == 2L) 3L else divisor + 2L
                }
                if (temp > 1) {
                    factorsMap[temp] = (factorsMap[temp] ?: 0) + 1
                }

                // All factors
                val factorsList = mutableListOf<Long>()
                val limit = sqrt(n.toDouble()).toLong()
                for (i in 1L..limit) {
                    if (n % i == 0L) {
                        factorsList.add(i)
                        if (i * i != n) {
                            factorsList.add(n / i)
                        }
                    }
                }
                factorsList.sort()

                // Next prime
                var nextP = n + 1
                while (!checkPrime(nextP) && nextP < n + 1000) {
                    nextP++
                }

                // Previous prime
                var prevP: Long? = null
                if (n > 2) {
                    var p = n - 1
                    while (p >= 2) {
                        if (checkPrime(p)) {
                            prevP = p
                            break
                        }
                        p--
                    }
                }

                PrimeFactorResult(
                    number = n,
                    isPrime = isPrime,
                    primeFactors = factorsMap,
                    allFactors = factorsList,
                    nextPrime = nextP,
                    prevPrime = prevP
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_prime_factor"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Prime Number & Factors",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("prime_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            numberInput = ""
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimeGreenBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Prime & Factor Finder",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimeGreen
                            )
                        )
                        Text(
                            text = "Check primality, compute prime factorization and view all divisors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Preset Samples
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val samples = listOf("17", "84", "1001", "9973")
                samples.forEach { s ->
                    FilterChip(
                        selected = numberInput == s,
                        onClick = {
                            HapticUtils.performClick(context)
                            numberInput = s
                        },
                        label = { Text(s, fontSize = 11.sp) }
                    )
                }
            }

            // Input Field
            AppInputField(
                value = numberInput,
                onValueChange = { numberInput = it },
                label = "Enter Positive Integer",
                placeholder = "e.g. 84",
                testTag = "input_prime_number"
            )

            // Results Card
            if (result != null) {
                val r = result!!
                val primeColor = if (r.isPrime) PrimeGreen else Color(0xFF2563EB)
                val statusText = if (r.number == 1L) "Neither Prime nor Composite"
                else if (r.isPrime) "Prime Number"
                else "Composite Number"

                val factorizationStr = if (r.primeFactors.isEmpty()) "None"
                else r.primeFactors.entries.joinToString(" × ") { (factor, count) ->
                    if (count > 1) "$factor${toSuperscript(count)}" else "$factor"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prime_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Classification",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = primeColor.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (r.isPrime) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = primeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = primeColor
                                        )
                                    )
                                }
                            }
                        }

                        // Prime Factorization String (e.g., 2² × 3 × 7)
                        ResultValueRow(
                            label = "Prime Factorization",
                            value = "${r.number} = $factorizationStr",
                            isHighlighted = true
                        )

                        ResultValueRow(
                            label = "Total Divisors / Factors",
                            value = "${r.allFactors.size} factors"
                        )

                        ResultValueRow(
                            label = "Next Prime Number",
                            value = r.nextPrime.toString()
                        )

                        if (r.prevPrime != null) {
                            ResultValueRow(
                                label = "Previous Prime Number",
                                value = r.prevPrime.toString()
                            )
                        }

                        // Divisors listing
                        Text(
                            text = "All Divisors (${r.allFactors.size}):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = r.allFactors.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Action Buttons
                val shareSummary = "${r.number} is a $statusText. Prime factors: $factorizationStr. Divisors: ${r.allFactors.joinToString(", ")}"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, shareSummary, "Prime Factorization")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, shareSummary, "Share Factors")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun toSuperscript(num: Int): String {
    val map = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
    )
    return num.toString().map { map[it] ?: it }.joinToString("")
}
