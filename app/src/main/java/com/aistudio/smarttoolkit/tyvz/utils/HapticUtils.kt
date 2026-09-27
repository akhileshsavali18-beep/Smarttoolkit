package com.aistudio.smarttoolkit.tyvz.utils

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Utility for providing tactile haptic feedback for user interactions
 * such as button clicks, tool launches, reset actions, and calculation completions.
 */
object HapticUtils {

    /**
     * Standard light click feedback for button taps, chips, and toggles.
     */
    fun performClick(context: Context, view: View? = null) {
        try {
            if (view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                return
            }
            vibrate(context, durationMs = 15, amplitude = 60)
        } catch (_: Throwable) {
            // Graceful fallback if haptics unavailable
        }
    }

    /**
     * Distinctive tactile feedback for when a calculation or conversion completes.
     */
    fun performSuccess(context: Context, view: View? = null) {
        try {
            if (view != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    return
                }
            }
            vibratePattern(context, longArrayOf(0, 30, 40, 45), intArrayOf(0, 100, 0, 180))
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    /**
     * Light bump feedback for preset selections or sliders.
     */
    fun performTick(context: Context, view: View? = null) {
        try {
            if (view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_TICK)
                return
            }
            vibrate(context, durationMs = 8, amplitude = 40)
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    private fun vibrate(context: Context, durationMs: Long, amplitude: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                vibrator?.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Throwable) {
            // Ignore if device has no vibrator hardware
        }
    }

    private fun vibratePattern(context: Context, timings: LongArray, amplitudes: IntArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    vibrator?.vibrate(effect)
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Throwable) {
            // Ignore if device has no vibrator hardware
        }
    }
}
