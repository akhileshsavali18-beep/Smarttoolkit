package com.aistudio.smarttoolkit.tyvz.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aistudio.smarttoolkit.tyvz.analytics.AppAnalytics
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.model.AppScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.aistudio.smarttoolkit.tyvz.ui.screens.AgeCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.AreaConverterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.BmiCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.BodyFatScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.BreathingExerciseScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.BubbleLevelScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.CgpaCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.CompassScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DashboardScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DataConverterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DayFinderScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DiscountCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.FuelCostScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.GstCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.HeartRateZonesScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.IdealWeightScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.ImageResizerScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.ImageToPdfScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.LoanEmiScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.NotesCounterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.PercentageCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.PhotoCompressorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.ProfitLossCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.QrToolScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.RomanNumeralScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.SplashScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.SplitBillScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.TallyCounterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.UnitConverterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.WorldClockScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.Base64ToolScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.UrlEncoderScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.NumberBaseConverterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.MorseCodeScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DecisionMakerScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.EventCountdownScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.ScreenLightScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.SoundMeterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.PrimeFactorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.SpeedConverterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.InflationCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.TipCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.ReverseTextScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DuplicateRemoverScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.UrlSlugGeneratorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.AspectRatioScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.MultiplicationTableScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.FactorialEvenOddScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.LeapYearScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.DeadPixelTesterScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.SleepCycleCalculatorScreen
import com.aistudio.smarttoolkit.tyvz.ui.screens.WhiteboardScreen

@Composable
fun ApsToolsApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Initialize Billing and Ads asynchronously so UI thread is never blocked
    val billingManager = remember { BillingManager.getInstance(context) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppAnalytics.initialize(context.applicationContext)
            } catch (e: Throwable) {
                // Background analytics init
            }
            try {
                billingManager.startConnection()
            } catch (e: Throwable) {
                // Background billing setup
            }
        }
    }

    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    // Keep each screen's Compose state (including Dashboard tab/scroll position)
    // alive while navigating to another screen, so Back returns to the exact place.
    val saveableStateHolder = rememberSaveableStateHolder()

    LaunchedEffect(currentScreen) {
        if (currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.DASHBOARD) {
            AppAnalytics.logToolOpened(context, currentScreen.name)
            com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager.getInstance(context).recordRecentTool(currentScreen.name)
        }
    }

    // Intercept back button when inside a tool screen to return to dashboard
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.SPLASH) {
        currentScreen = AppScreen.DASHBOARD
    }

    Surface(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (initialState == AppScreen.SPLASH) {
                    fadeIn().togetherWith(fadeOut())
                } else if (targetState != AppScreen.DASHBOARD) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width / 3 } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "screen_navigation"
        ) { screen ->
            saveableStateHolder.SaveableStateProvider(screen.name) {
                when (screen) {
                AppScreen.SPLASH -> SplashScreen(
                    onSplashFinished = { currentScreen = AppScreen.DASHBOARD }
                )

                AppScreen.DASHBOARD -> DashboardScreen(
                    billingManager = billingManager,
                    onNavigateToTool = { newScreen -> currentScreen = newScreen }
                )

                // Image & Doc Tools
                AppScreen.PHOTO_COMPRESSOR -> PhotoCompressorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )
                AppScreen.IMAGE_RESIZER -> ImageResizerScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )
                AppScreen.IMAGE_TO_PDF -> ImageToPdfScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )

                // Financial & Utility Tools
                AppScreen.GST_CALCULATOR -> GstCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.LOAN_EMI_CALCULATOR -> LoanEmiScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.AGE_CALCULATOR -> AgeCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.SPLIT_BILL -> SplitBillScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )
                AppScreen.DISCOUNT_CALCULATOR -> DiscountCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Daily Quick Tools
                AppScreen.QR_CODE_TOOL -> QrToolScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )
                AppScreen.NOTES_COUNTER -> NotesCounterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    billingManager = billingManager
                )
                AppScreen.BMI_CALCULATOR -> BmiCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.UNIT_CONVERTER -> UnitConverterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.TALLY_COUNTER -> TallyCounterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 2: Finance & Math
                AppScreen.PERCENTAGE_CALCULATOR -> PercentageCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.PROFIT_LOSS_CALCULATOR -> ProfitLossCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.CGPA_PERCENTAGE_CALCULATOR -> CgpaCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.FUEL_COST_PLANNER -> FuelCostScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 2: Daily Utilities & Device Sensors
                AppScreen.COMPASS_TOOL -> CompassScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.BUBBLE_LEVEL -> BubbleLevelScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.DAY_FINDER -> DayFinderScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.WORLD_CLOCK -> WorldClockScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 2: Health & Fitness
                AppScreen.IDEAL_BODY_WEIGHT -> IdealWeightScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.BODY_FAT_ESTIMATOR -> BodyFatScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.BREATHING_EXERCISE -> BreathingExerciseScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.HEART_RATE_ZONES -> HeartRateZonesScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 2: Science & Converters
                AppScreen.AREA_CONVERTER -> AreaConverterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.DATA_STORAGE_CONVERTER -> DataConverterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.ROMAN_NUMERAL_CONVERTER -> RomanNumeralScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 3: Text & Developer Utilities
                AppScreen.BASE64_TOOL -> Base64ToolScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.URL_ENCODER_TOOL -> UrlEncoderScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.NUMBER_BASE_CONVERTER -> NumberBaseConverterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.MORSE_CODE_TOOL -> MorseCodeScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 3: Daily Utilities & Decision Makers
                AppScreen.DECISION_MAKER -> DecisionMakerScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.EVENT_COUNTDOWN -> EventCountdownScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.SCREEN_LIGHT -> ScreenLightScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.SOUND_METER -> SoundMeterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 3: Math & Academic Utilities
                AppScreen.PRIME_FACTOR_FINDER -> PrimeFactorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.SPEED_CONVERTER -> SpeedConverterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.INFLATION_CALCULATOR -> InflationCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.TIP_CALCULATOR -> TipCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 4: Text & Utility (Media & Documents)
                AppScreen.REVERSE_TEXT_TOOL -> ReverseTextScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.DUPLICATE_LINE_REMOVER -> DuplicateRemoverScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.URL_SLUG_GENERATOR -> UrlSlugGeneratorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 4: Math & Daily Utility (Finance & Shopping)
                AppScreen.ASPECT_RATIO_CALCULATOR -> AspectRatioScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.MULTIPLICATION_TABLE -> MultiplicationTableScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.FACTORIAL_EVEN_ODD -> FactorialEvenOddScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )

                // Batch 4: Daily Utilities & Sensors (Daily Utilities)
                AppScreen.LEAP_YEAR_CHECKER -> LeapYearScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.DEAD_PIXEL_TESTER -> DeadPixelTesterScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.SLEEP_CYCLE_CALCULATOR -> SleepCycleCalculatorScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
                AppScreen.WHITEBOARD_SIGNATURE_PAD -> WhiteboardScreen(
                    onBack = { currentScreen = AppScreen.DASHBOARD }
                )
            }
            }
        }
    }
}

// Backward compatibility alias
@Composable
fun SmartToolKitApp(modifier: Modifier = Modifier) {
    ApsToolsApp(modifier = modifier)
}

@Composable
fun MainDashboardScreen(modifier: Modifier = Modifier) {
    ApsToolsApp(modifier = modifier)
}
