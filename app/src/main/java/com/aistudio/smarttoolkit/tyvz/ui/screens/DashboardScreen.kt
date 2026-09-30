package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.aistudio.smarttoolkit.tyvz.ads.AdManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.aistudio.smarttoolkit.tyvz.ads.ConsentManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.R
import com.aistudio.smarttoolkit.tyvz.ads.StickyBannerAd
import com.aistudio.smarttoolkit.tyvz.BuildConfig
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager
import com.aistudio.smarttoolkit.tyvz.model.AppScreen
import com.aistudio.smarttoolkit.tyvz.model.ThemeMode
import com.aistudio.smarttoolkit.tyvz.model.ToolCategory
import com.aistudio.smarttoolkit.tyvz.ui.components.ProUpgradeDialog
import com.aistudio.smarttoolkit.tyvz.update.UpdateInfo
import com.aistudio.smarttoolkit.tyvz.update.UpdateManager
import com.aistudio.smarttoolkit.tyvz.ui.theme.AgeOrange
import com.aistudio.smarttoolkit.tyvz.ui.theme.AgeOrangeBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.AreaEmerald
import com.aistudio.smarttoolkit.tyvz.ui.theme.AreaEmeraldBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.AspectRatioBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.AspectRatioBlueBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.Base64Slate
import com.aistudio.smarttoolkit.tyvz.ui.theme.Base64SlateBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.BmiTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.BmiTealBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.BodyFatRose
import com.aistudio.smarttoolkit.tyvz.ui.theme.BodyFatRoseBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.BreathingCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.BreathingCyanBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.CategoryImageBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.CategoryImageBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.CgpaIndigo
import com.aistudio.smarttoolkit.tyvz.ui.theme.CgpaIndigoBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.CompassTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.CompassTealBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.CountdownIndigo
import com.aistudio.smarttoolkit.tyvz.ui.theme.CountdownIndigoBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DataBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.DataBlueBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DayBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.DayBlueBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DeadPixelDark
import com.aistudio.smarttoolkit.tyvz.ui.theme.DeadPixelDarkBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DecisionPink
import com.aistudio.smarttoolkit.tyvz.ui.theme.DecisionPinkBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DiscountPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.DiscountPurpleBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.DuplicateCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.DuplicateCyanBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.EmiGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.EmiGreenBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.FactorialTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.FactorialTealBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.FuelAmber
import com.aistudio.smarttoolkit.tyvz.ui.theme.FuelAmberBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.GstBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.GstBlueBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.HeartRateRed
import com.aistudio.smarttoolkit.tyvz.ui.theme.HeartRateRedBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.IdealWeightViolet
import com.aistudio.smarttoolkit.tyvz.ui.theme.IdealWeightVioletBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.InflationRed
import com.aistudio.smarttoolkit.tyvz.ui.theme.InflationRedBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.InverterViolet
import com.aistudio.smarttoolkit.tyvz.ui.theme.InverterVioletBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.LeapYearGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.LeapYearGreenBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.LevelLime
import com.aistudio.smarttoolkit.tyvz.ui.theme.LevelLimeBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.MorseCodeAmber
import com.aistudio.smarttoolkit.tyvz.ui.theme.MorseCodeAmberBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.MultiplyAmber
import com.aistudio.smarttoolkit.tyvz.ui.theme.MultiplyAmberBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.NotesSlate
import com.aistudio.smarttoolkit.tyvz.ui.theme.NotesSlateBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.NumberBasePurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.NumberBasePurpleBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.PercentOrange
import com.aistudio.smarttoolkit.tyvz.ui.theme.PercentOrangeBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.PrimeGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.PrimeGreenBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProGold
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProGoldBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProfitGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProfitGreenBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.QrIndigo
import com.aistudio.smarttoolkit.tyvz.ui.theme.QrIndigoBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.RomanGold
import com.aistudio.smarttoolkit.tyvz.ui.theme.RomanGoldBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.ScreenLightYellow
import com.aistudio.smarttoolkit.tyvz.ui.theme.ScreenLightYellowBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.SleepCyclePurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.SleepCyclePurpleBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.SlugIndigo
import com.aistudio.smarttoolkit.tyvz.ui.theme.SlugIndigoBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.SoundMeterCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.SoundMeterCyanBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.SpeedOrange
import com.aistudio.smarttoolkit.tyvz.ui.theme.SpeedOrangeBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.SplitTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.SplitTealBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.TallyPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.TallyPurpleBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.TipEmerald
import com.aistudio.smarttoolkit.tyvz.ui.theme.TipEmeraldBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.UnitCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.UnitCyanBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.UrlTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.UrlTealBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.WhiteboardBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.WhiteboardBlueBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.WorldClockPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.WorldClockPurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ToolDefinition(
    val screen: AppScreen,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: ToolCategory,
    val accentColor: Color,
    val backgroundColor: Color,
    val testTag: String
)

enum class NavTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    CATEGORIES("Categories", Icons.Filled.Category, Icons.Outlined.Category),
    FAVOURITES("Favourites", Icons.Filled.Star, Icons.Outlined.StarBorder),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    billingManager: BillingManager,
    onNavigateToTool: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesManager = remember { AppPreferencesManager.getInstance(context) }
    val favoriteScreens by preferencesManager.favoriteTools.collectAsState()
    val recentScreenNames by preferencesManager.recentTools.collectAsState()
    val themeMode by preferencesManager.themeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isCurrentlyDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val isPremium by billingManager.isPremium.collectAsState()

    // Preserve the selected bottom tab and each tab's scroll position when returning from tools.
    var activeTab by rememberSaveable { mutableStateOf(NavTab.HOME) }
    val homeListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val categoriesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val favouritesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val profileListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    var selectedCategoryFilter by rememberSaveable { mutableStateOf(ToolCategory.ALL) }
    var showProDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackSubmitting by remember { mutableStateOf(false) }
    var feedbackSubmitted by remember { mutableStateOf(false) }
    var feedbackAttempt by remember { mutableStateOf(0) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showCoffeeDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isDownloadingUpdate by remember { mutableStateOf(false) }
    var updateProgress by remember { mutableStateOf(0) }
    val updateScope = rememberCoroutineScope()

    fun checkForUpdate(showNoUpdateMessage: Boolean = false) {
        updateScope.launch {
            val result = UpdateManager.checkForUpdate()
            if (result != null) {
                updateInfo = result
            } else if (showNoUpdateMessage) {
                Toast.makeText(context, "You are using the latest APS TOOLS version.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        checkForUpdate()
    }

    // Search query on Home / Categories
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val allTools = remember {
        listOf(
            // Category 1: Media & Documents (4 tools)
            ToolDefinition(
                screen = AppScreen.PHOTO_COMPRESSOR,
                title = "Photo Compressor",
                description = "Compress KB/MB file size up to 90% while keeping sharp clarity.",
                icon = Icons.Default.Compress,
                category = ToolCategory.IMAGE_TOOLS,
                accentColor = CategoryImageBlue,
                backgroundColor = CategoryImageBg,
                testTag = "tool_card_compressor"
            ),
            ToolDefinition(
                screen = AppScreen.IMAGE_RESIZER,
                title = "Image Resizer",
                description = "Custom dimensions, pixels, aspect ratio lock, and quick presets.",
                icon = Icons.Default.AspectRatio,
                category = ToolCategory.IMAGE_TOOLS,
                accentColor = CategoryImageBlue,
                backgroundColor = CategoryImageBg,
                testTag = "tool_card_resizer"
            ),
            ToolDefinition(
                screen = AppScreen.IMAGE_TO_PDF,
                title = "Image to PDF Converter",
                description = "Multi-image PDF creation with custom quality & A4 sizing.",
                icon = Icons.Default.PictureAsPdf,
                category = ToolCategory.PDF_TOOLS,
                accentColor = CategoryImageBlue,
                backgroundColor = CategoryImageBg,
                testTag = "tool_card_image_to_pdf"
            ),
            ToolDefinition(
                screen = AppScreen.QR_CODE_TOOL,
                title = "QR Code Generator & Scanner",
                description = "Generate crisp custom QR codes and scan instantly from camera or gallery.",
                icon = Icons.Default.QrCode,
                category = ToolCategory.QR_SCANNER,
                accentColor = QrIndigo,
                backgroundColor = QrIndigoBg,
                testTag = "tool_card_qr"
            ),

            // Category 2: Finance & Shopping (4 tools)
            ToolDefinition(
                screen = AppScreen.GST_CALCULATOR,
                title = "GST Calculator",
                description = "Inclusive and exclusive modes with 5%, 12%, 18%, 28% slabs and CGST/SGST split.",
                icon = Icons.Default.ReceiptLong,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = GstBlue,
                backgroundColor = GstBlueBg,
                testTag = "tool_card_gst"
            ),
            ToolDefinition(
                screen = AppScreen.LOAN_EMI_CALCULATOR,
                title = "Loan / EMI Calculator",
                description = "Principal, interest rate, and tenure monthly EMI & payment amortization.",
                icon = Icons.Default.AccountBalance,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = EmiGreen,
                backgroundColor = EmiGreenBg,
                testTag = "tool_card_emi"
            ),
            ToolDefinition(
                screen = AppScreen.DISCOUNT_CALCULATOR,
                title = "Discount Calculator",
                description = "Original price, discount %, and final sale price with savings breakdown.",
                icon = Icons.Default.LocalOffer,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = DiscountPurple,
                backgroundColor = DiscountPurpleBg,
                testTag = "tool_card_discount"
            ),
            ToolDefinition(
                screen = AppScreen.SPLIT_BILL,
                title = "Bill Splitter & Tip Calculator",
                description = "Split restaurant bills, custom tip %, and round off share per person.",
                icon = Icons.Default.Payments,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = SplitTeal,
                backgroundColor = SplitTealBg,
                testTag = "tool_card_split_bill"
            ),

            // Category 3: Daily Utilities & Health (4 tools)
            ToolDefinition(
                screen = AppScreen.AGE_CALCULATOR,
                title = "Age Calculator",
                description = "Exact age in years, months, days, and next birthday countdown.",
                icon = Icons.Default.Cake,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = AgeOrange,
                backgroundColor = AgeOrangeBg,
                testTag = "tool_card_age"
            ),
            ToolDefinition(
                screen = AppScreen.BMI_CALCULATOR,
                title = "BMI & Health Metric Calculator",
                description = "Height and weight BMI status, category gauge, and ideal weight range.",
                icon = Icons.Default.FitnessCenter,
                category = ToolCategory.HEALTH_FITNESS,
                accentColor = BmiTeal,
                backgroundColor = BmiTealBg,
                testTag = "tool_card_bmi"
            ),
            ToolDefinition(
                screen = AppScreen.UNIT_CONVERTER,
                title = "Unit Converter",
                description = "Convert Length, Weight, and Temperature units with multi-unit breakdown.",
                icon = Icons.Default.Straighten,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = UnitCyan,
                backgroundColor = UnitCyanBg,
                testTag = "tool_card_unit_converter"
            ),
            ToolDefinition(
                screen = AppScreen.TALLY_COUNTER,
                title = "Digital Tally Counter",
                description = "Vibration tap count, customizable increments, and reset controls.",
                icon = Icons.Default.TouchApp,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = TallyPurple,
                backgroundColor = TallyPurpleBg,
                testTag = "tool_card_tally"
            ),

            // Batch 2: Finance & Math (4 tools)
            ToolDefinition(
                screen = AppScreen.PERCENTAGE_CALCULATOR,
                title = "Percentage Calculator",
                description = "Quick % of value, increase/decrease %, and ratio percentage calculator.",
                icon = Icons.Default.Percent,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = PercentOrange,
                backgroundColor = PercentOrangeBg,
                testTag = "tool_card_percentage"
            ),
            ToolDefinition(
                screen = AppScreen.PROFIT_LOSS_CALCULATOR,
                title = "Profit & Loss Calculator",
                description = "Cost price, selling price, margin %, profit/loss breakdown with tax option.",
                icon = Icons.Default.TrendingUp,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = ProfitGreen,
                backgroundColor = ProfitGreenBg,
                testTag = "tool_card_profit_loss"
            ),
            ToolDefinition(
                screen = AppScreen.CGPA_PERCENTAGE_CALCULATOR,
                title = "CGPA to Percentage Calculator",
                description = "Convert GPA/CGPA to percentage with custom university formula multiplier.",
                icon = Icons.Default.School,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = CgpaIndigo,
                backgroundColor = CgpaIndigoBg,
                testTag = "tool_card_cgpa"
            ),
            ToolDefinition(
                screen = AppScreen.FUEL_COST_PLANNER,
                title = "Fuel Cost & Mileage Planner",
                description = "Trip distance, fuel mileage economy, total liters and passenger split cost.",
                icon = Icons.Default.LocalGasStation,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = FuelAmber,
                backgroundColor = FuelAmberBg,
                testTag = "tool_card_fuel_cost"
            ),

            // Batch 2: Daily Utilities & Device Sensors (4 tools)
            ToolDefinition(
                screen = AppScreen.COMPASS_TOOL,
                title = "Compass & Direction Finder",
                description = "Precise magnetic azimuth heading dial, cardinal directions and sensor readout.",
                icon = Icons.Default.Explore,
                category = ToolCategory.DEVICE_SENSORS,
                accentColor = CompassTeal,
                backgroundColor = CompassTealBg,
                testTag = "tool_card_compass"
            ),
            ToolDefinition(
                screen = AppScreen.BUBBLE_LEVEL,
                title = "Bubble Level (Spirit Level)",
                description = "Dual-axis accelerometer surface tilt level with calibration & haptic lock.",
                icon = Icons.Default.Speed,
                category = ToolCategory.DEVICE_SENSORS,
                accentColor = LevelLime,
                backgroundColor = LevelLimeBg,
                testTag = "tool_card_bubble_level"
            ),
            ToolDefinition(
                screen = AppScreen.DAY_FINDER,
                title = "Day Finder",
                description = "Pick any past or future date for exact day of week, leap year & facts.",
                icon = Icons.Default.Event,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = DayBlue,
                backgroundColor = DayBlueBg,
                testTag = "tool_card_day_finder"
            ),
            ToolDefinition(
                screen = AppScreen.WORLD_CLOCK,
                title = "World Clock / Timezone Difference",
                description = "Interactive timezone slider, global cities comparison, and day/night clocks.",
                icon = Icons.Default.Public,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = WorldClockPurple,
                backgroundColor = WorldClockPurpleBg,
                testTag = "tool_card_world_clock"
            ),

            // Batch 2: Health & Fitness (4 tools)
            ToolDefinition(
                screen = AppScreen.IDEAL_BODY_WEIGHT,
                title = "Ideal Body Weight Calculator",
                description = "Devine, Robinson, and Miller clinical formulas by gender and body frame.",
                icon = Icons.Default.FitnessCenter,
                category = ToolCategory.HEALTH_FITNESS,
                accentColor = IdealWeightViolet,
                backgroundColor = IdealWeightVioletBg,
                testTag = "tool_card_ideal_weight"
            ),
            ToolDefinition(
                screen = AppScreen.BODY_FAT_ESTIMATOR,
                title = "Body Fat Percentage Estimator",
                description = "U.S. Navy circumference standard calculation with lean vs fat mass breakdown.",
                icon = Icons.Default.MonitorWeight,
                category = ToolCategory.HEALTH_FITNESS,
                accentColor = BodyFatRose,
                backgroundColor = BodyFatRoseBg,
                testTag = "tool_card_body_fat"
            ),
            ToolDefinition(
                screen = AppScreen.BREATHING_EXERCISE,
                title = "Breathing Exercise Guide",
                description = "Pulsating visual guide for 4-7-8 relaxation and stress-relief rhythm.",
                icon = Icons.Default.Air,
                category = ToolCategory.HEALTH_FITNESS,
                accentColor = BreathingCyan,
                backgroundColor = BreathingCyanBg,
                testTag = "tool_card_breathing"
            ),
            ToolDefinition(
                screen = AppScreen.HEART_RATE_ZONES,
                title = "Target Heart Rate Zones",
                description = "5 cardio training intensity zones, Tanaka formula, and maximum heart rate.",
                icon = Icons.Default.Favorite,
                category = ToolCategory.HEALTH_FITNESS,
                accentColor = HeartRateRed,
                backgroundColor = HeartRateRedBg,
                testTag = "tool_card_heart_rate"
            ),

            // Batch 2: Science & Converters (3 tools)
            ToolDefinition(
                screen = AppScreen.AREA_CONVERTER,
                title = "Area & Land Converter",
                description = "Sq ft, sq m, acres, guntha, hectares, bigha, and full unit breakdown.",
                icon = Icons.Default.SquareFoot,
                category = ToolCategory.SCIENCE_CONVERTERS,
                accentColor = AreaEmerald,
                backgroundColor = AreaEmeraldBg,
                testTag = "tool_card_area_converter"
            ),
            ToolDefinition(
                screen = AppScreen.DATA_STORAGE_CONVERTER,
                title = "Data & Storage Converter",
                description = "Binary (1024) vs Decimal (1000) storage conversion and download speed estimator.",
                icon = Icons.Default.SdStorage,
                category = ToolCategory.SCIENCE_CONVERTERS,
                accentColor = DataBlue,
                backgroundColor = DataBlueBg,
                testTag = "tool_card_data_converter"
            ),
            ToolDefinition(
                screen = AppScreen.ROMAN_NUMERAL_CONVERTER,
                title = "Roman Numeral Converter",
                description = "Bi-directional numbers to Roman numerals conversion with quick keypad.",
                icon = Icons.Default.FormatListNumbered,
                category = ToolCategory.SCIENCE_CONVERTERS,
                accentColor = RomanGold,
                backgroundColor = RomanGoldBg,
                testTag = "tool_card_roman_numerals"
            ),

            // Cash Notes Counter
            ToolDefinition(
                screen = AppScreen.NOTES_COUNTER,
                title = "Cash Denomination Counter",
                description = "Count Indian currency notes (₹2000 to ₹10) with instant total and verbal words.",
                icon = Icons.Default.Notes,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = NotesSlate,
                backgroundColor = NotesSlateBg,
                testTag = "tool_card_notes_counter"
            ),

            // Batch 3: Text & Developer Utilities (4 tools)
            ToolDefinition(
                screen = AppScreen.BASE64_TOOL,
                title = "Base64 Encoder / Decoder",
                description = "Convert plain text to Base64 and decode Base64 back to plain text offline.",
                icon = Icons.Default.Code,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = Base64Slate,
                backgroundColor = Base64SlateBg,
                testTag = "tool_card_base64"
            ),
            ToolDefinition(
                screen = AppScreen.URL_ENCODER_TOOL,
                title = "URL Encoder / Decoder",
                description = "Encode URLs to safe query percent-encoding and decode back with parameter breakdown.",
                icon = Icons.Default.Link,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = UrlTeal,
                backgroundColor = UrlTealBg,
                testTag = "tool_card_url_encoder"
            ),
            ToolDefinition(
                screen = AppScreen.NUMBER_BASE_CONVERTER,
                title = "Number Base Converter",
                description = "Convert numbers dynamically across Binary (2), Octal (8), Decimal (10), and Hex (16).",
                icon = Icons.Default.Memory,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = NumberBasePurple,
                backgroundColor = NumberBasePurpleBg,
                testTag = "tool_card_number_base"
            ),
            ToolDefinition(
                screen = AppScreen.MORSE_CODE_TOOL,
                title = "Morse Code Converter",
                description = "Convert alphanumeric text to international Morse code dots/dashes with audio/vibration pulse.",
                icon = Icons.Default.GraphicEq,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = MorseCodeAmber,
                backgroundColor = MorseCodeAmberBg,
                testTag = "tool_card_morse_code"
            ),

            // Batch 3: Daily Utilities & Decision Makers (4 tools)
            ToolDefinition(
                screen = AppScreen.DECISION_MAKER,
                title = "Decision Maker & Choice Picker",
                description = "Enter options separated by commas or lines, tap to pick a random choice with animated selector.",
                icon = Icons.Default.Casino,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = DecisionPink,
                backgroundColor = DecisionPinkBg,
                testTag = "tool_card_decision_maker"
            ),
            ToolDefinition(
                screen = AppScreen.EVENT_COUNTDOWN,
                title = "Event Countdown Timer",
                description = "Pick an event title and target future date to show live ticking days, hours, and minutes remaining.",
                icon = Icons.Default.HourglassTop,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = CountdownIndigo,
                backgroundColor = CountdownIndigoBg,
                testTag = "tool_card_event_countdown"
            ),
            ToolDefinition(
                screen = AppScreen.SCREEN_LIGHT,
                title = "Screen Ambient Color Light",
                description = "Fullscreen night light with warm white, amber, calm blue, red and brightness slider.",
                icon = Icons.Default.ColorLens,
                category = ToolCategory.DEVICE_SENSORS,
                accentColor = ScreenLightYellow,
                backgroundColor = ScreenLightYellowBg,
                testTag = "tool_card_screen_light"
            ),
            ToolDefinition(
                screen = AppScreen.SOUND_METER,
                title = "Sound Level Decibel Meter",
                description = "Clean decibel meter display showing approximate ambient noise level with quiet/moderate/loud gauge.",
                icon = Icons.Default.GraphicEq,
                category = ToolCategory.DEVICE_SENSORS,
                accentColor = SoundMeterCyan,
                backgroundColor = SoundMeterCyanBg,
                testTag = "tool_card_sound_meter"
            ),

            // Batch 3: Math & Academic Utilities (4 tools)
            ToolDefinition(
                screen = AppScreen.PRIME_FACTOR_FINDER,
                title = "Prime Number & Factor Finder",
                description = "Enter any integer to instantly check primality, view prime factorization, and all divisors.",
                icon = Icons.Default.Calculate,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = PrimeGreen,
                backgroundColor = PrimeGreenBg,
                testTag = "tool_card_prime_factor"
            ),
            ToolDefinition(
                screen = AppScreen.SPEED_CONVERTER,
                title = "Speed & Velocity Converter",
                description = "Real-time conversion across km/h, mph, m/s, knots, ft/s, and Mach speed.",
                icon = Icons.Default.Speed,
                category = ToolCategory.SCIENCE_CONVERTERS,
                accentColor = SpeedOrange,
                backgroundColor = SpeedOrangeBg,
                testTag = "tool_card_speed_converter"
            ),
            ToolDefinition(
                screen = AppScreen.INFLATION_CALCULATOR,
                title = "Inflation & Purchasing Power",
                description = "Current cost, annual inflation rate %, and years to compute future price and purchasing loss.",
                icon = Icons.Default.TrendingUp,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = InflationRed,
                backgroundColor = InflationRedBg,
                testTag = "tool_card_inflation_calc"
            ),
            ToolDefinition(
                screen = AppScreen.TIP_CALCULATOR,
                title = "Simple Tip & Round-Up Calculator",
                description = "Bill amount, tip slider (%), split count, and one-tap round-up total calculation.",
                icon = Icons.Default.Payments,
                category = ToolCategory.FINANCE_BUSINESS,
                accentColor = TipEmerald,
                backgroundColor = TipEmeraldBg,
                testTag = "tool_card_tip_calculator"
            ),

            // Batch 4: Text & Utility (Media & Documents) (3 tools)
            ToolDefinition(
                screen = AppScreen.REVERSE_TEXT_TOOL,
                title = "Reverse Text & Inverter",
                description = "Flip characters backwards, reverse word order, or generate upside-down text.",
                icon = Icons.Default.SwapVert,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = InverterViolet,
                backgroundColor = InverterVioletBg,
                testTag = "tool_card_reverse_text"
            ),
            ToolDefinition(
                screen = AppScreen.DUPLICATE_LINE_REMOVER,
                title = "Duplicate Line / Word Remover",
                description = "Clean duplicate items, repetitive lists, and words with case and whitespace trimming.",
                icon = Icons.Default.DeleteSweep,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = DuplicateCyan,
                backgroundColor = DuplicateCyanBg,
                testTag = "tool_card_duplicate_remover"
            ),
            ToolDefinition(
                screen = AppScreen.URL_SLUG_GENERATOR,
                title = "Text to URL Slug Generator",
                description = "Convert blog titles and sentences into clean kebab-case or snake_case URL slugs.",
                icon = Icons.Default.Link,
                category = ToolCategory.TEXT_DEVELOPER,
                accentColor = SlugIndigo,
                backgroundColor = SlugIndigoBg,
                testTag = "tool_card_url_slug"
            ),

            // Batch 4: Math & Daily Utility (Finance & Shopping) (3 tools)
            ToolDefinition(
                screen = AppScreen.ASPECT_RATIO_CALCULATOR,
                title = "Aspect Ratio Calculator",
                description = "Calculate aspect ratios for 16:9, 4:3, 1:1, 9:16 and scale custom image resolutions.",
                icon = Icons.Default.AspectRatio,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = AspectRatioBlue,
                backgroundColor = AspectRatioBlueBg,
                testTag = "tool_card_aspect_ratio"
            ),
            ToolDefinition(
                screen = AppScreen.MULTIPLICATION_TABLE,
                title = "Multiplication Table Generator",
                description = "Instant times tables for any number 1-100 up to ×30 with one-tap copy.",
                icon = Icons.Default.Calculate,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = MultiplyAmber,
                backgroundColor = MultiplyAmberBg,
                testTag = "tool_card_multiplication_table"
            ),
            ToolDefinition(
                screen = AppScreen.FACTORIAL_EVEN_ODD,
                title = "Factorial & Even/Odd Checker",
                description = "Instant parity check (even/odd) and fast n! computation with exact digit count.",
                icon = Icons.Default.Calculate,
                category = ToolCategory.MATH_EDUCATION,
                accentColor = FactorialTeal,
                backgroundColor = FactorialTealBg,
                testTag = "tool_card_factorial_even_odd"
            ),

            // Batch 4: Daily Utilities & Sensors (Daily Utilities) (3 tools)
            ToolDefinition(
                screen = AppScreen.LEAP_YEAR_CHECKER,
                title = "Leap Year Checker",
                description = "Check whether any year is a leap year with rule details (400, 100, 4 divisibility).",
                icon = Icons.Default.Event,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = LeapYearGreen,
                backgroundColor = LeapYearGreenBg,
                testTag = "tool_card_leap_year"
            ),
            ToolDefinition(
                screen = AppScreen.DEAD_PIXEL_TESTER,
                title = "Screen Dead-Pixel & Color Tester",
                description = "Fullscreen pure Red, Green, Blue, White, and Black test tiles for display defect inspection.",
                icon = Icons.Default.Tv,
                category = ToolCategory.DEVICE_SENSORS,
                accentColor = DeadPixelDark,
                backgroundColor = DeadPixelDarkBg,
                testTag = "tool_card_dead_pixel"
            ),
            ToolDefinition(
                screen = AppScreen.SLEEP_CYCLE_CALCULATOR,
                title = "Sleep Cycle Calculator",
                description = "Calculates optimal bedtimes and wake-up times based on 90-minute REM sleep cycles.",
                icon = Icons.Default.Bedtime,
                category = ToolCategory.DAILY_UTILITIES,
                accentColor = SleepCyclePurple,
                backgroundColor = SleepCyclePurpleBg,
                testTag = "tool_card_sleep_cycle"
            ),
            ToolDefinition(
                screen = AppScreen.WHITEBOARD_SIGNATURE_PAD,
                title = "Simple Whiteboard / Quick Signature Pad",
                description = "Smooth canvas sketching, finger signature creator, undo/redo, and direct PNG export.",
                icon = Icons.Default.Brush,
                category = ToolCategory.CREATIVE_WHITEBOARD,
                accentColor = WhiteboardBlue,
                backgroundColor = WhiteboardBlueBg,
                testTag = "tool_card_whiteboard"
            )
        )
    }

    // Tool of the Day (dynamically rotates daily)
    val toolOfTheDay = remember(allTools) {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        allTools[day % allTools.size]
    }

    // Resolve recent tool definitions
    val recentTools = remember(recentScreenNames, allTools) {
        recentScreenNames.mapNotNull { name -> allTools.find { it.screen.name == name } }
    }

    // Favorite tool definitions
    val favoriteTools = remember(favoriteScreens, allTools) {
        allTools.filter { favoriteScreens.contains(it.screen.name) }
    }

    val apsToolsWebsite = "https://akhileshsavali18-beep.github.io/Smarttoolkit/"

    // APS TOOLS is distributed from its website, not Google Play.
    fun shareAppAction() {
        HapticUtils.performClick(context)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Try APS TOOLS — 50+ smart utility tools in one app. Download: $apsToolsWebsite"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Refer APS TOOLS"))
    }

    fun rateAppAction() {
        HapticUtils.performClick(context)
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apsToolsWebsite)))
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open APS TOOLS website", Toast.LENGTH_SHORT).show()
        }
    }

    if (showProDialog) {
        ProUpgradeDialog(
            billingManager = billingManager,
            onDismissRequest = { showProDialog = false }
        )
    }

    if (showFeedbackDialog) {
        InAppFeedbackDialog(
            isSubmitting = feedbackSubmitting,
            isSubmitted = feedbackSubmitted,
            onDismiss = {
                if (!feedbackSubmitting) {
                    showFeedbackDialog = false
                    feedbackSubmitted = false
                }
            },
            onSubmit = { rating, comment ->
                val cleanComment = comment.trim()
                if (cleanComment.isBlank()) {
                    Toast.makeText(context, "Please enter your feedback.", Toast.LENGTH_SHORT).show()
                } else {
                    feedbackSubmitting = true
                    feedbackSubmitted = false
                    feedbackAttempt += 1
                    val currentAttempt = feedbackAttempt

                    val feedback = hashMapOf<String, Any>(
                        "rating" to rating,
                        "comment" to cleanComment,
                        "appVersion" to BuildConfig.VERSION_NAME,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    FirebaseFirestore.getInstance()
                        .collection("feedback")
                        .add(feedback)
                        .addOnSuccessListener {
                            if (feedbackAttempt == currentAttempt) {
                                feedbackSubmitting = false
                                feedbackSubmitted = true
                                HapticUtils.performSuccess(context)
                                Toast.makeText(
                                    context,
                                    "Feedback submitted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            if (feedbackAttempt == currentAttempt) {
                                feedbackSubmitting = false
                                Toast.makeText(
                                    context,
                                    "Couldn't submit feedback. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    updateScope.launch {
                        delay(15000)
                        if (feedbackSubmitting && feedbackAttempt == currentAttempt) {
                            feedbackSubmitting = false
                            Toast.makeText(
                                context,
                                "Feedback submission timed out. Check your internet and try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "APS TOOLS operates 100% offline on your device.\n\n" +
                            "• No personal data, photos, documents, or calculations are ever uploaded to any cloud server.\n" +
                            "• Camera and storage access are used exclusively on-device for QR scanning and saving your converted files to gallery.\n" +
                            "• All features remain fast, private, and secure.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showCoffeeDialog) {
        AlertDialog(
            onDismissRequest = { showCoffeeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Support APS Tools ☕", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Help us keep APS TOOLS free and improving.\n\nChoose your preferred support amount on the official support page: ₹10, ₹50, ₹100 or a custom amount.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Option A: Watch Rewarded Ad
                    Button(
                        onClick = {
                            showCoffeeDialog = false
                            val activity = context as? Activity
                            if (activity != null) {
                                if (AdManager.isRewardedAdReady()) {
                                    AdManager.showRewardedAd(
                                        activity = activity,
                                        onUserEarnedReward = { amount, type ->
                                            Toast.makeText(context, "Thank you! Rewarded $amount $type for supporting developer.", Toast.LENGTH_LONG).show()
                                        },
                                        onAdDismissed = {
                                            AdManager.loadRewardedAd(activity)
                                        },
                                        onAdFailed = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Rewarded video ad is loading. Please try again in 3 seconds!", Toast.LENGTH_SHORT).show()
                                    AdManager.loadRewardedAd(activity)
                                }
                            } else {
                                Toast.makeText(context, "Thank you for supporting APS TOOLS!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Watch Short Video Ad", fontWeight = FontWeight.Bold)
                    }

                    // Option B: Official Website Link
                    Button(
                        onClick = {
                            showCoffeeDialog = false
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://akhileshsavali18-beep.github.io/Smarttoolkit/donate.html"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open official website", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Open Support APS Tools", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Choose ₹10, ₹50, ₹100 or a custom amount on the support page", fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCoffeeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (updateInfo != null) {
        UpdateAvailableDialog(
            updateInfo = updateInfo!!,
            isDownloading = isDownloadingUpdate,
            downloadProgress = updateProgress,
            onDismiss = { if (!isDownloadingUpdate) updateInfo = null },
            onUpdate = {
                isDownloadingUpdate = true
                updateProgress = 0
                updateScope.launch {
                    val info = updateInfo ?: return@launch
                    val result = UpdateManager.downloadAndInstall(
                        context = context,
                        updateInfo = info,
                        onProgress = { progress -> updateProgress = progress }
                    )
                    isDownloadingUpdate = false
                    if (result.isFailure) {
                        updateProgress = 0
                        Toast.makeText(
                            context,
                            "Update download failed. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher),
                            contentDescription = "APS Tools Banner Logo",
                            modifier = Modifier
                                .height(38.dp)
                                .width(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "APS TOOLS",
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                                ),
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Simple Tools • Smart Results",
                                fontSize = 11.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    // Dark / Light Mode Switch
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            val nextMode = if (isCurrentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
                            preferencesManager.setThemeMode(nextMode)
                        },
                        modifier = Modifier
                            .testTag("theme_toggle_button")
                            .size(40.dp)
                    ) {
                        AnimatedContent(
                            targetState = isCurrentlyDark,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.75f))
                                    .togetherWith(fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.75f))
                            },
                            label = "theme_mode_switch"
                        ) { inDark ->
                            Icon(
                                imageVector = if (inDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (inDark) "Switch to Light Mode" else "Switch to Dark Mode",
                                tint = if (inDark) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // PRO Subscription Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isPremium) {
                                        listOf(Color(0xFFFDE68A), Color(0xFFFBBF24))
                                    } else {
                                        listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                    }
                                )
                            )
                            .clickable {
                                HapticUtils.performClick(context)
                                showProDialog = true
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("open_pro_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "PRO Subscription",
                                tint = if (isPremium) Color(0xFF78350F) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPremium) "PRO" else "GO PRO",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp,
                                color = if (isPremium) Color(0xFF78350F) else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Sticky Banner Ad (hidden for PRO users)
                StickyBannerAd(
                    isPremium = isPremium,
                    modifier = Modifier.testTag("sticky_banner_ad")
                )

                // Modern 4-Tab Bottom Navigation Bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                HapticUtils.performClick(context)
                                activeTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ApsBlue,
                                selectedTextColor = ApsBlue,
                                indicatorColor = ApsBlue.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("bottom_nav_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (activeTab == NavTab.HOME) {
                ExtendedFloatingActionButton(
                    onClick = { shareAppAction() },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Share App") },
                    text = { Text("Refer & Download", fontWeight = FontWeight.Bold) },
                    containerColor = ApsBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.testTag("floating_share_button")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                NavTab.HOME -> HomeTabContent(
                    listState = homeListState,
                    allTools = allTools,
                    toolOfTheDay = toolOfTheDay,
                    recentTools = recentTools,
                    favoriteScreens = favoriteScreens,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onToggleFavorite = { screenName ->
                        HapticUtils.performClick(context)
                        preferencesManager.toggleFavorite(screenName)
                    },
                    onNavigateToTool = onNavigateToTool,
                    onExploreCategories = { activeTab = NavTab.CATEGORIES }
                )

                NavTab.CATEGORIES -> CategoriesTabContent(
                    listState = categoriesListState,
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategoryFilterChange = { selectedCategoryFilter = it },
                    allTools = allTools,
                    favoriteScreens = favoriteScreens,
                    onToggleFavorite = { screenName ->
                        HapticUtils.performClick(context)
                        preferencesManager.toggleFavorite(screenName)
                    },
                    onNavigateToTool = onNavigateToTool
                )

                NavTab.FAVOURITES -> FavoritesTabContent(
                    listState = favouritesListState,
                    favoriteTools = favoriteTools,
                    onToggleFavorite = { screenName ->
                        HapticUtils.performClick(context)
                        preferencesManager.toggleFavorite(screenName)
                    },
                    onNavigateToTool = onNavigateToTool,
                    onBrowseTools = { activeTab = NavTab.CATEGORIES }
                )

                NavTab.PROFILE -> ProfileTabContent(
                    listState = profileListState,
                    isPremium = isPremium,
                    themeMode = themeMode,
                    onSetThemeMode = { mode ->
                        HapticUtils.performClick(context)
                        preferencesManager.setThemeMode(mode)
                    },
                    onGoPro = {
                        HapticUtils.performClick(context)
                        showProDialog = true
                    },
                    onShareApp = { shareAppAction() },
                    onRateUs = { rateAppAction() },
                    onPrivacyPolicy = {
                        HapticUtils.performClick(context)
                        showPrivacyDialog = true
                    },
                    onBuyCoffee = {
                        HapticUtils.performClick(context)
                        showCoffeeDialog = true
                    },
                    onFeedback = {
                        HapticUtils.performClick(context)
                        showFeedbackDialog = true
                    },
                    onCheckForUpdate = {
                        HapticUtils.performClick(context)
                        checkForUpdate(showNoUpdateMessage = true)
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// TAB 1: HOME TAB CONTENT
// -----------------------------------------------------------------------------------------
@Composable
private fun HomeTabContent(
    listState: LazyListState,
    allTools: List<ToolDefinition>,
    toolOfTheDay: ToolDefinition,
    recentTools: List<ToolDefinition>,
    favoriteScreens: Set<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onNavigateToTool: (AppScreen) -> Unit,
    onExploreCategories: () -> Unit
) {
    val context = LocalContext.current

    val searchFilteredTools = remember(searchQuery, allTools) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allTools.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.category.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search tools (e.g. compress, BMI, GST, QR)...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ApsBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ApsBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar")
            )
        }

        // If actively searching, show search results
        if (searchQuery.isNotBlank()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SEARCH RESULTS (${searchFilteredTools.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (searchFilteredTools.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "No matching tools found",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching with keywords like photo, pdf, emi, age, bmi, or unit.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(searchFilteredTools) { tool ->
                    val isFav = favoriteScreens.contains(tool.screen.name)
                    DashboardToolCard(
                        tool = tool,
                        isFavorite = isFav,
                        onToggleFavorite = { onToggleFavorite(tool.screen.name) },
                        onClick = {
                            HapticUtils.performClick(context)
                            onNavigateToTool(tool.screen)
                        }
                    )
                }
            }
        } else {
            // Tool of the Day Banner Card
            item {
                ToolOfTheDayCard(
                    tool = toolOfTheDay,
                    onOpen = {
                        HapticUtils.performClick(context)
                        onNavigateToTool(toolOfTheDay.screen)
                    }
                )
            }

            // Recent Tools Section (if any have been used)
            if (recentTools.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "RECENT TOOLS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(recentTools.take(6)) { tool ->
                                RecentToolChipCard(
                                    tool = tool,
                                    onClick = {
                                        HapticUtils.performClick(context)
                                        onNavigateToTool(tool.screen)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Access Grid Section (Top 6 tools)
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "QUICK ACCESS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = onExploreCategories) {
                        Text("View All (${allTools.size})", fontSize = 12.sp, color = ApsBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 6 Top Tools Cards
            items(allTools.take(6)) { tool ->
                val isFav = favoriteScreens.contains(tool.screen.name)
                DashboardToolCard(
                    tool = tool,
                    isFavorite = isFav,
                    onToggleFavorite = { onToggleFavorite(tool.screen.name) },
                    onClick = {
                        HapticUtils.performClick(context)
                        onNavigateToTool(tool.screen)
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// TAB 2: CATEGORIES TAB CONTENT
// -----------------------------------------------------------------------------------------
@Composable
private fun CategoriesTabContent(
    listState: LazyListState,
    selectedCategoryFilter: ToolCategory,
    onCategoryFilterChange: (ToolCategory) -> Unit,
    allTools: List<ToolDefinition>,
    favoriteScreens: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onNavigateToTool: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    // Show every real category (11) in the Categories tab.
    // ALL is rendered separately as the first "All Categories" chip.
    val categories = remember {
        ToolCategory.values().filter { it != ToolCategory.ALL }
    }
    val displayedTools = remember(selectedCategoryFilter, allTools) {
        if (selectedCategoryFilter == ToolCategory.ALL) {
            allTools
        } else {
            allTools.filter { it.category == selectedCategoryFilter }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        // Filter Chips Bar
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == ToolCategory.ALL,
                        onClick = {
                            HapticUtils.performClick(context)
                            onCategoryFilterChange(ToolCategory.ALL)
                        },
                        label = { Text("All Categories", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ApsBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            HapticUtils.performClick(context)
                            onCategoryFilterChange(cat)
                        },
                        label = { Text(cat.title, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ApsBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Sectioned or Filtered Display
        if (selectedCategoryFilter != ToolCategory.ALL) {
            item {
                Text(
                    text = "${selectedCategoryFilter.title.uppercase()} (${displayedTools.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            items(displayedTools) { tool ->
                val isFav = favoriteScreens.contains(tool.screen.name)
                DashboardToolCard(
                    tool = tool,
                    isFavorite = isFav,
                    onToggleFavorite = { onToggleFavorite(tool.screen.name) },
                    onClick = {
                        HapticUtils.performClick(context)
                        onNavigateToTool(tool.screen)
                    }
                )
            }
        } else {
            item {
                Text(
                    text = "ALL TOOLS (${allTools.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            // Grouped By Categories without inner banner card
            categories.forEach { cat ->
                val groupTools = allTools.filter { it.category == cat }

                items(groupTools) { tool ->
                    val isFav = favoriteScreens.contains(tool.screen.name)
                    DashboardToolCard(
                        tool = tool,
                        isFavorite = isFav,
                        onToggleFavorite = { onToggleFavorite(tool.screen.name) },
                        onClick = {
                            HapticUtils.performClick(context)
                            onNavigateToTool(tool.screen)
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// TAB 3: FAVOURITES TAB CONTENT
// -----------------------------------------------------------------------------------------
@Composable
private fun FavoritesTabContent(
    listState: LazyListState,
    favoriteTools: List<ToolDefinition>,
    onToggleFavorite: (String) -> Unit,
    onNavigateToTool: (AppScreen) -> Unit,
    onBrowseTools: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "YOUR STARRED TOOLS (${favoriteTools.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }
        }

        if (favoriteTools.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ProGoldBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = ProGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Starred Tools Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Star any tool on Home or Categories to create your customized quick-access dashboard.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                HapticUtils.performClick(context)
                                onBrowseTools()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ApsBlue)
                        ) {
                            Text("Browse All Tools", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(favoriteTools) { tool ->
                DashboardToolCard(
                    tool = tool,
                    isFavorite = true,
                    onToggleFavorite = { onToggleFavorite(tool.screen.name) },
                    onClick = {
                        HapticUtils.performClick(context)
                        onNavigateToTool(tool.screen)
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// TAB 4: PROFILE / SETTINGS TAB CONTENT
// -----------------------------------------------------------------------------------------
@Composable
private fun ProfileTabContent(
    listState: LazyListState,
    isPremium: Boolean,
    themeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit,
    onGoPro: () -> Unit,
    onShareApp: () -> Unit,
    onRateUs: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onBuyCoffee: () -> Unit,
    onFeedback: () -> Unit,
    onCheckForUpdate: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        // App Profile Card Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "APS TOOLS",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ApsBlue.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "100% Offline & Private • Zero Cloud Tracking",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ApsBlue
                        )
                    }
                }
            }
        }

        // PRO Upgrade Banner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoPro)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (isPremium) {
                                    listOf(Color(0xFF065F46), Color(0xFF059669))
                                } else {
                                    listOf(Color(0xFFB45309), Color(0xFFF59E0B))
                                }
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPremium) "PRO Subscription Active" else "Unlock APS TOOLS PRO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isPremium) "Ad-free experience • PRO tools unlocked" else "Go ad-free & unlock PRO tools",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Preferences Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PREFERENCES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dark Theme Mode Selector
                    Text("Theme Appearance", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetThemeMode(mode) },
                                label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ApsBlue,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Support & Community Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SUPPORT & COMMUNITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SettingsActionRow(
                        icon = Icons.Default.Share,
                        title = "Refer & Download",
                        subtitle = "Open the APS TOOLS website and download the app",
                        onClick = onShareApp
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsActionRow(
                        icon = Icons.Default.Feedback,
                        title = "Send Feedback",
                        subtitle = "Rate APS TOOLS and send feedback directly",
                        onClick = onFeedback
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsActionRow(
                        icon = Icons.Default.SystemUpdate,
                        title = "Check for Updates",
                        subtitle = "Check and install the latest APS TOOLS APK",
                        onClick = onCheckForUpdate
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsActionRow(
                        icon = Icons.Default.LocalCafe,
                        title = "Support APS Tools ☕",
                        subtitle = "Help us keep APS TOOLS free and improving",
                        onClick = onBuyCoffee
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsActionRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "Offline-first data security guarantee",
                        onClick = onPrivacyPolicy
                    )

                    val context = LocalContext.current
                    val privacyOptionsRequired by ConsentManager.privacyOptionsRequired.collectAsState()

                    if (privacyOptionsRequired) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SettingsActionRow(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Choices",
                            subtitle = "Manage your ad privacy and consent choices",
                            onClick = {
                                (context as? Activity)?.let { activity ->
                                    ConsentManager.showPrivacyOptions(activity)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = ApsBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// -----------------------------------------------------------------------------------------
// REUSABLE SUB-COMPONENTS
// -----------------------------------------------------------------------------------------
@Composable
private fun ToolOfTheDayCard(
    tool: ToolDefinition,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .testTag("tool_of_the_day_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A), // Deep Blue
                            Color(0xFF2563EB)  // Vibrant Aps Blue
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFBBF24))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "TOOL OF THE DAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            letterSpacing = 0.8.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tool.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tool.description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Open Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentToolChipCard(
    tool: ToolDefinition,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(130.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tool.backgroundColor)
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = tool.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tool.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryHeaderCard(
    category: ToolCategory,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = category.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ApsBlue.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count Tools",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ApsBlue
            )
        }
    }
}

@Composable
private fun DashboardToolCard(
    tool: ToolDefinition,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(tool.testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tool.backgroundColor)
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = tool.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tool.backgroundColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tool.category.title.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tool.accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Favorite Pin Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("pin_favorite_${tool.screen.name}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Pin to favorites",
                            tint = if (isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Launch Tool",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tool.accentColor
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(tool.backgroundColor)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = tool.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// In-App Feedback Dialog
@Composable
private fun InAppFeedbackDialog(
    isSubmitting: Boolean,
    isSubmitted: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Share Your Feedback",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isSubmitted) {
                    Text(
                        text = "✓ Feedback submitted successfully",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApsBlue
                    )
                    Text(
                        text = "Thank you for helping us improve APS TOOLS.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "How has your experience been with APS Tools?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { if (!isSubmitting) rating = i },
                                enabled = !isSubmitting
                            ) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= rating) ProGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (!isSubmitting) comment = it },
                        placeholder = { Text("Tell us what you like or what we should improve...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 5,
                        enabled = !isSubmitting
                    )
                }
            }
        },
        confirmButton = {
            if (isSubmitted) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ApsBlue)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onSubmit(rating, comment) },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ApsBlue)
                ) {
                    Text(
                        if (isSubmitting) "Submitting..." else "Submit Feedback",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun UpdateAvailableDialog(
    updateInfo: UpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Int,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    val sizeLabel = formatFileSize(updateInfo.sizeBytes)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Available", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (isDownloading) {
                        "Downloading APS TOOLS " + updateInfo.versionName + "…"
                    } else {
                        "APS TOOLS " + updateInfo.versionName + " is available."
                    },
                    fontSize = 15.sp
                )

                Text(
                    "APK size: " + sizeLabel,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isDownloading) {
                    LinearProgressIndicator(
                        progress = { downloadProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        downloadProgress.toString() + "% downloaded",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApsBlue
                    )
                    Text(
                        if (downloadProgress >= 100) {
                            "Download complete. Opening installer…"
                        } else {
                            "Please keep the app open until the download reaches 100%."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        "Tap Download to download the APK. The Android install screen will open automatically after 100%.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdate,
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = ApsBlue)
            ) {
                Text(
                    if (isDownloading) "Downloading " + downloadProgress + "%" else "Download",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDownloading) {
                Text("Later")
            }
        }
    )
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "Unknown size"
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1.0) {
        String.format(java.util.Locale.US, "%.1f MB", mb)
    } else {
        val kb = bytes / 1024.0
        String.format(java.util.Locale.US, "%.0f KB", kb)
    }
}

@Composable
fun MainDashboardScreen(modifier: Modifier = Modifier) {
    com.aistudio.smarttoolkit.tyvz.ui.ApsToolsApp(modifier = modifier)
}
