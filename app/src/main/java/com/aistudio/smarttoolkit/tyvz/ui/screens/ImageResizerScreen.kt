package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ads.AdManager
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.QuickPercentChip
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.SummaryOutputCard
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsBlueContainer
import com.aistudio.smarttoolkit.tyvz.ui.theme.CategoryImageBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.Slate100
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageResizerScreen(
    onBack: () -> Unit,
    billingManager: BillingManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var origWidth by remember { mutableStateOf(0) }
    var origHeight by remember { mutableStateOf(0) }

    var widthInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var keepAspectRatio by remember { mutableStateOf(true) }

    var isResizing by remember { mutableStateOf(false) }
    var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var savedFile by remember { mutableStateOf<File?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            resizedBitmap = null
            savedFile = null
            scope.launch(Dispatchers.IO) {
                val bmp = ImageUtils.loadBitmapFromUri(context, uri)
                withContext(Dispatchers.Main) {
                    originalBitmap = bmp
                    if (bmp != null) {
                        origWidth = bmp.width
                        origHeight = bmp.height
                        widthInput = bmp.width.toString()
                        heightInput = bmp.height.toString()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Image Resizer",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            originalBitmap = null
                            resizedBitmap = null
                            savedFile = null
                            widthInput = ""
                            heightInput = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("reset_resizer_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Selection / Preview Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (originalBitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Slate100),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = (resizedBitmap ?: originalBitmap)!!.asImageBitmap(),
                                contentDescription = "Image preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Original Dimensions: $origWidth × $origHeight px",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Photo")
                        }
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ApsBlueContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = ApsBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select an Image to Resize",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Scale resolution cleanly to custom pixel dimensions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("pick_resize_photo_button")
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choose from Gallery", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Dimension Controls (when image is selected)
            if (originalBitmap != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (keepAspectRatio) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = ApsBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Maintain Aspect Ratio",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Switch(
                                checked = keepAspectRatio,
                                onCheckedChange = { keepAspectRatio = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Width Input
                            Box(modifier = Modifier.weight(1f)) {
                                AppInputField(
                                    value = widthInput,
                                    onValueChange = { newVal ->
                                        if (newVal.all { it.isDigit() } && newVal.length <= 5) {
                                            widthInput = newVal
                                            if (keepAspectRatio && origWidth > 0) {
                                                val w = newVal.toIntOrNull()
                                                if (w != null && w > 0) {
                                                    val h = ((w.toDouble() / origWidth) * origHeight).toInt()
                                                    heightInput = h.toString()
                                                }
                                            }
                                        }
                                    },
                                    label = "Width (px)",
                                    placeholder = "e.g. 1080",
                                    testTag = "target_width_input",
                                    keyboardType = KeyboardType.Number
                                )
                            }

                            // Height Input
                            Box(modifier = Modifier.weight(1f)) {
                                AppInputField(
                                    value = heightInput,
                                    onValueChange = { newVal ->
                                        if (newVal.all { it.isDigit() } && newVal.length <= 5) {
                                            heightInput = newVal
                                            if (keepAspectRatio && origHeight > 0) {
                                                val h = newVal.toIntOrNull()
                                                if (h != null && h > 0) {
                                                    val w = ((h.toDouble() / origHeight) * origWidth).toInt()
                                                    widthInput = w.toString()
                                                }
                                            }
                                        }
                                    },
                                    label = "Height (px)",
                                    placeholder = "e.g. 1920",
                                    testTag = "target_height_input",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick scale chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("25%", "50%", "75%", "100%").forEach { pctStr ->
                                val pct = pctStr.replace("%", "").toDouble() / 100.0
                                QuickPercentChip(
                                    label = pctStr,
                                    isSelected = false,
                                    onClick = {
                                        widthInput = (origWidth * pct).toInt().toString()
                                        heightInput = (origHeight * pct).toInt().toString()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Resize Action Button
                val targetW = widthInput.toIntOrNull() ?: 0
                val targetH = heightInput.toIntOrNull() ?: 0
                val isValid = targetW > 0 && targetH > 0 && targetW <= 10000 && targetH <= 10000

                Button(
                    onClick = {
                        val bmp = originalBitmap ?: return@Button
                        isResizing = true
                        scope.launch(Dispatchers.IO) {
                            val scaled = ImageUtils.resizeBitmap(bmp, targetW, targetH)
                            val stream = java.io.ByteArrayOutputStream()
                            scaled.compress(Bitmap.CompressFormat.JPEG, 92, stream)
                            val file = ImageUtils.saveBytesToCacheFile(
                                context,
                                stream.toByteArray(),
                                "resized_${targetW}x${targetH}_${System.currentTimeMillis()}.jpg"
                            )
                            withContext(Dispatchers.Main) {
                                resizedBitmap = scaled
                                savedFile = file
                                isResizing = false

                                // Show interstitial ad
                                if (activity != null) {
                                    AdManager.showInterstitialAd(
                                        activity,
                                        billingManager.isPremium.value
                                    ) {}
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("resize_now_button"),
                    enabled = isValid && !isResizing
                ) {
                    if (isResizing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Resizing...")
                    } else {
                        Icon(imageVector = Icons.Default.AspectRatio, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resize Image Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Results Card
                if (resizedBitmap != null) {
                    SummaryOutputCard(
                        title = "Resized Image Ready",
                        accentColor = CategoryImageBlue
                    ) {
                        ResultValueRow(
                            label = "New Resolution",
                            value = "${resizedBitmap?.width} × ${resizedBitmap?.height} px",
                            isHighlighted = true,
                            highlightColor = ApsBlue
                        )
                        ResultValueRow(
                            label = "Original Resolution",
                            value = "$origWidth × $origHeight px"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (resizedBitmap != null) {
                            Button(
                                onClick = {
                                    HapticUtils.performClick(context)
                                    val fileName = "APS_Resized_${System.currentTimeMillis()}.jpg"
                                    ImageUtils.saveBitmapToGallery(
                                        context = context,
                                        bitmap = resizedBitmap!!,
                                        displayName = fileName,
                                        format = android.graphics.Bitmap.CompressFormat.JPEG,
                                        quality = 95,
                                        showToast = true
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_resized_button")
                            ) {
                                Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save to Gallery", fontWeight = FontWeight.Bold)
                            }

                            if (savedFile != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = {
                                        HapticUtils.performClick(context)
                                        ImageUtils.shareFile(
                                            context,
                                            savedFile!!,
                                            "image/jpeg",
                                            "Share Resized Photo"
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("share_resized_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Share Photo", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
