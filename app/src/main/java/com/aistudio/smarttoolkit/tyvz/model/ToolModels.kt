package com.aistudio.smarttoolkit.tyvz.model

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class AppScreen {
    SPLASH,
    DASHBOARD,
    PHOTO_COMPRESSOR,
    IMAGE_RESIZER,
    IMAGE_TO_PDF,
    GST_CALCULATOR,
    LOAN_EMI_CALCULATOR,
    AGE_CALCULATOR,
    SPLIT_BILL,
    QR_CODE_TOOL,
    NOTES_COUNTER,
    DISCOUNT_CALCULATOR,
    BMI_CALCULATOR,
    UNIT_CONVERTER,
    TALLY_COUNTER,

    // Batch 2: Finance & Math Utilities
    PERCENTAGE_CALCULATOR,
    PROFIT_LOSS_CALCULATOR,
    CGPA_PERCENTAGE_CALCULATOR,
    FUEL_COST_PLANNER,

    // Batch 2: Daily Utilities & Device Sensors
    COMPASS_TOOL,
    BUBBLE_LEVEL,
    DAY_FINDER,
    WORLD_CLOCK,

    // Batch 2: Health & Fitness
    IDEAL_BODY_WEIGHT,
    BODY_FAT_ESTIMATOR,
    BREATHING_EXERCISE,
    HEART_RATE_ZONES,

    // Batch 2: Science & Converters
    AREA_CONVERTER,
    DATA_STORAGE_CONVERTER,
    ROMAN_NUMERAL_CONVERTER,

    // Batch 3: Text & Developer Utilities
    BASE64_TOOL,
    URL_ENCODER_TOOL,
    NUMBER_BASE_CONVERTER,
    MORSE_CODE_TOOL,

    // Batch 3: Daily Utilities & Decision Makers
    DECISION_MAKER,
    EVENT_COUNTDOWN,
    SCREEN_LIGHT,
    SOUND_METER,

    // Batch 3: Math & Academic Utilities
    PRIME_FACTOR_FINDER,
    SPEED_CONVERTER,
    INFLATION_CALCULATOR,
    TIP_CALCULATOR,

    // Batch 4: Text & Utility (Media & Documents)
    REVERSE_TEXT_TOOL,
    DUPLICATE_LINE_REMOVER,
    URL_SLUG_GENERATOR,

    // Batch 4: Math & Daily Utility (Finance & Shopping)
    ASPECT_RATIO_CALCULATOR,
    MULTIPLICATION_TABLE,
    FACTORIAL_EVEN_ODD,

    // Batch 4: Daily Utilities & Sensors (Daily Utilities)
    LEAP_YEAR_CHECKER,
    DEAD_PIXEL_TESTER,
    SLEEP_CYCLE_CALCULATOR,

    // Batch 5: Utilities
    WHITEBOARD_SIGNATURE_PAD
}

enum class ToolCategory(val title: String) {
    ALL("All Tools"),
    IMAGE_TOOLS("Image Tools"),
    PDF_TOOLS("PDF Tools"),
    QR_SCANNER("QR & Scanner Tools"),
    FINANCE_BUSINESS("Finance & Business"),
    MATH_EDUCATION("Math & Education"),
    TEXT_DEVELOPER("Text & Developer"),
    CREATIVE_WHITEBOARD("Creative & Whiteboard"),
    DAILY_UTILITIES("Daily Utilities"),
    HEALTH_FITNESS("Health & Fitness"),
    SCIENCE_CONVERTERS("Science & Converters"),
    DEVICE_SENSORS("Device & Sensors");

    companion object {
        val IMAGE_DOC get() = IMAGE_TOOLS
        val MEDIA_DOC get() = TEXT_DEVELOPER
        val FINANCE get() = FINANCE_BUSINESS
        val DAILY get() = DAILY_UTILITIES
        val DAILY_HEALTH get() = DAILY_UTILITIES
    }
}

enum class GstMode {
    ADD_GST,
    REMOVE_GST
}

data class GstResult(
    val netAmount: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val totalGst: Double = 0.0,
    val totalAmount: Double = 0.0
)

enum class EmiTenureType {
    YEARS,
    MONTHS
}

data class EmiResult(
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0,
    val principalPercentage: Float = 100f,
    val interestPercentage: Float = 0f
)

data class AgeResult(
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val daysUntilNextBirthday: Int = 0,
    val nextBirthdayDayOfWeek: String = ""
)

data class DiscountResult(
    val originalPrice: Double = 0.0,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val finalPrice: Double = 0.0
)

data class SplitBillResult(
    val billAmount: Double = 0.0,
    val tipPercent: Double = 0.0,
    val tipAmount: Double = 0.0,
    val totalWithTip: Double = 0.0,
    val numPeople: Int = 1,
    val perPersonTotal: Double = 0.0,
    val perPersonTip: Double = 0.0,
    val perPersonBill: Double = 0.0
)

data class TextStatsResult(
    val charCount: Int = 0,
    val charCountNoSpaces: Int = 0,
    val wordCount: Int = 0,
    val sentenceCount: Int = 0,
    val paragraphCount: Int = 0,
    val readingTimeSeconds: Int = 0,
    val speakingTimeSeconds: Int = 0
)

object Calculators {
    private val currencyFormatter = DecimalFormat("#,##0.00")
    private val intFormatter = DecimalFormat("#,##0")

    fun formatNumber(value: Double): String = currencyFormatter.format(value)
    fun formatInt(value: Long): String = intFormatter.format(value)

    fun calculateGst(amount: Double, ratePercent: Double, mode: GstMode): GstResult {
        if (amount <= 0.0 || ratePercent < 0.0) {
            return GstResult()
        }
        return if (mode == GstMode.ADD_GST) {
            val netAmount = amount
            val totalGst = amount * (ratePercent / 100.0)
            val halfGst = totalGst / 2.0
            val totalAmount = netAmount + totalGst
            GstResult(
                netAmount = netAmount,
                cgst = halfGst,
                sgst = halfGst,
                totalGst = totalGst,
                totalAmount = totalAmount
            )
        } else {
            val totalAmount = amount
            val netAmount = amount / (1.0 + (ratePercent / 100.0))
            val totalGst = totalAmount - netAmount
            val halfGst = totalGst / 2.0
            GstResult(
                netAmount = netAmount,
                cgst = halfGst,
                sgst = halfGst,
                totalGst = totalGst,
                totalAmount = totalAmount
            )
        }
    }

    fun calculateEmi(principal: Double, annualRatePercent: Double, tenureValue: Int, tenureType: EmiTenureType): EmiResult {
        if (principal <= 0.0 || tenureValue <= 0) {
            return EmiResult()
        }
        val totalMonths = if (tenureType == EmiTenureType.YEARS) tenureValue * 12 else tenureValue
        if (totalMonths <= 0) return EmiResult()

        if (annualRatePercent <= 0.0) {
            val emi = principal / totalMonths
            return EmiResult(
                monthlyEmi = emi,
                totalInterest = 0.0,
                totalPayment = principal,
                principalPercentage = 100f,
                interestPercentage = 0f
            )
        }

        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val factor = Math.pow(1.0 + monthlyRate, totalMonths.toDouble())
        val monthlyEmi = (principal * monthlyRate * factor) / (factor - 1.0)
        val totalPayment = monthlyEmi * totalMonths
        val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)
        val principalPct = ((principal / totalPayment) * 100).toFloat().coerceIn(0f, 100f)
        val interestPct = (100f - principalPct).coerceIn(0f, 100f)

        return EmiResult(
            monthlyEmi = monthlyEmi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principalPercentage = principalPct,
            interestPercentage = interestPct
        )
    }

    fun calculateAge(birthMillis: Long, todayMillis: Long = System.currentTimeMillis()): AgeResult {
        val birthCal = Calendar.getInstance().apply { timeInMillis = birthMillis }
        val todayCal = Calendar.getInstance().apply { timeInMillis = todayMillis }

        if (birthCal.after(todayCal)) {
            return AgeResult()
        }

        val bYear = birthCal.get(Calendar.YEAR)
        val bMonth = birthCal.get(Calendar.MONTH)
        val bDay = birthCal.get(Calendar.DAY_OF_MONTH)

        val tYear = todayCal.get(Calendar.YEAR)
        val tMonth = todayCal.get(Calendar.MONTH)
        val tDay = todayCal.get(Calendar.DAY_OF_MONTH)

        var years = tYear - bYear
        var months = tMonth - bMonth
        var days = tDay - bDay

        if (days < 0) {
            val prevCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, tYear)
                set(Calendar.MONTH, tMonth)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
            }
            days += prevCal.get(Calendar.DAY_OF_MONTH)
            months -= 1
        }

        if (months < 0) {
            months += 12
            years -= 1
        }

        val nextBirthdayCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, tYear)
            set(Calendar.MONTH, bMonth)
            set(Calendar.DAY_OF_MONTH, bDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayZero = Calendar.getInstance().apply {
            set(Calendar.YEAR, tYear)
            set(Calendar.MONTH, tMonth)
            set(Calendar.DAY_OF_MONTH, tDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (nextBirthdayCal.before(todayZero)) {
            nextBirthdayCal.add(Calendar.YEAR, 1)
        }

        val diffMillis = (nextBirthdayCal.timeInMillis - todayZero.timeInMillis).coerceAtLeast(0L)
        val daysUntil = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        val dayFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val nextBirthdayStr = dayFormat.format(nextBirthdayCal.time)

        return AgeResult(
            years = years,
            months = months,
            days = days,
            daysUntilNextBirthday = daysUntil,
            nextBirthdayDayOfWeek = nextBirthdayStr
        )
    }

    fun calculateDiscount(originalPrice: Double, discountPercent: Double): DiscountResult {
        if (originalPrice <= 0.0 || discountPercent < 0.0) {
            return DiscountResult()
        }
        val validPercent = discountPercent.coerceIn(0.0, 100.0)
        val amountSaved = originalPrice * (validPercent / 100.0)
        val finalPrice = (originalPrice - amountSaved).coerceAtLeast(0.0)
        return DiscountResult(
            originalPrice = originalPrice,
            discountPercent = validPercent,
            discountAmount = amountSaved,
            finalPrice = finalPrice
        )
    }

    fun calculateSplitBill(billAmount: Double, tipPercent: Double, numPeople: Int): SplitBillResult {
        if (billAmount <= 0.0 || numPeople <= 0) {
            return SplitBillResult(numPeople = numPeople.coerceAtLeast(1))
        }
        val tipAmt = billAmount * (tipPercent.coerceAtLeast(0.0) / 100.0)
        val total = billAmount + tipAmt
        val perPersonTotal = total / numPeople
        val perPersonTip = tipAmt / numPeople
        val perPersonBill = billAmount / numPeople

        return SplitBillResult(
            billAmount = billAmount,
            tipPercent = tipPercent,
            tipAmount = tipAmt,
            totalWithTip = total,
            numPeople = numPeople,
            perPersonTotal = perPersonTotal,
            perPersonTip = perPersonTip,
            perPersonBill = perPersonBill
        )
    }

    fun calculateTextStats(text: String): TextStatsResult {
        val totalChars = text.length
        val charsNoSpaces = text.replace("\\s+".toRegex(), "").length
        val trimmed = text.trim()
        val words = if (trimmed.isEmpty()) 0 else trimmed.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val sentences = if (trimmed.isEmpty()) 0 else text.split(Regex("[.!?]+\\s*")).filter { it.isNotBlank() }.size
        val paragraphs = if (trimmed.isEmpty()) 0 else text.split(Regex("(\\r?\\n)+")).filter { it.isNotBlank() }.size
        
        // Average reading speed: 200 words per min
        val readSeconds = if (words > 0) ((words / 200.0) * 60).toInt().coerceAtLeast(1) else 0
        // Average speaking speed: 130 words per min
        val speakSeconds = if (words > 0) ((words / 130.0) * 60).toInt().coerceAtLeast(1) else 0

        return TextStatsResult(
            charCount = totalChars,
            charCountNoSpaces = charsNoSpaces,
            wordCount = words,
            sentenceCount = sentences,
            paragraphCount = paragraphs,
            readingTimeSeconds = readSeconds,
            speakingTimeSeconds = speakSeconds
        )
    }

    fun calculateBmi(heightCm: Double, weightKg: Double): BmiResult {
        if (heightCm <= 0.0 || weightKg <= 0.0) {
            return BmiResult()
        }
        val heightM = heightCm / 100.0
        val bmiRaw = weightKg / (heightM * heightM)
        val bmi = Math.round(bmiRaw * 10.0) / 10.0

        val category = when {
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }

        val minIdeal = Math.round(18.5 * heightM * heightM * 10.0) / 10.0
        val maxIdeal = Math.round(24.9 * heightM * heightM * 10.0) / 10.0

        return BmiResult(
            bmi = bmi,
            category = category,
            idealWeightMin = minIdeal,
            idealWeightMax = maxIdeal
        )
    }

    // Unit Conversion
    fun convertLength(value: Double, fromUnitId: String, toUnitId: String): Double {
        val metersFactor = mapOf(
            "m" to 1.0,
            "km" to 1000.0,
            "cm" to 0.01,
            "mm" to 0.001,
            "mi" to 1609.344,
            "yd" to 0.9144,
            "ft" to 0.3048,
            "in" to 0.0254
        )
        val inMeters = value * (metersFactor[fromUnitId] ?: 1.0)
        return inMeters / (metersFactor[toUnitId] ?: 1.0)
    }

    fun convertWeight(value: Double, fromUnitId: String, toUnitId: String): Double {
        val kgFactor = mapOf(
            "kg" to 1.0,
            "g" to 0.001,
            "mg" to 0.000001,
            "lb" to 0.45359237,
            "oz" to 0.028349523125,
            "t" to 1000.0
        )
        val inKg = value * (kgFactor[fromUnitId] ?: 1.0)
        return inKg / (kgFactor[toUnitId] ?: 1.0)
    }

    fun convertTemperature(value: Double, fromUnitId: String, toUnitId: String): Double {
        if (fromUnitId == toUnitId) return value
        val celsius = when (fromUnitId) {
            "c" -> value
            "f" -> (value - 32.0) * (5.0 / 9.0)
            "k" -> value - 273.15
            else -> value
        }
        return when (toUnitId) {
            "c" -> celsius
            "f" -> (celsius * (9.0 / 5.0)) + 32.0
            "k" -> celsius + 273.15
            else -> celsius
        }
    }
}

enum class BmiCategory(val label: String, val colorHex: Long, val advice: String) {
    UNDERWEIGHT("Underweight", 0xFF0284C7, "Focus on nutrient-dense meals and healthy calories to reach an optimal body weight."),
    NORMAL("Normal Weight", 0xFF16A34A, "Great job! Keep up your healthy nutrition, active routine, and balanced lifestyle."),
    OVERWEIGHT("Overweight", 0xFFEA580C, "Incorporate daily moderate cardio, strength exercises, and mindful portion sizes."),
    OBESE("Obese", 0xFFDC2626, "Consult with a doctor or certified dietitian for a structured, sustainable fitness plan.")
}

data class BmiResult(
    val bmi: Double = 0.0,
    val category: BmiCategory = BmiCategory.NORMAL,
    val idealWeightMin: Double = 0.0,
    val idealWeightMax: Double = 0.0
)

