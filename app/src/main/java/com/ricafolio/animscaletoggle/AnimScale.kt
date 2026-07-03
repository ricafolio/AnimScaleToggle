package com.ricafolio.animscaletoggle

import android.content.Context
import android.provider.Settings

/**
 * Reads/writes the three Settings.Global animation scale keys.
 * Requires WRITE_SECURE_SETTINGS, granted once via:
 *   adb shell pm grant com.ricafolio.animscaletoggle android.permission.WRITE_SECURE_SETTINGS
 *
 * This permission grant is NOT tied to Developer Options being on/off,
 * so toggling dev options no longer resets these values back to 1x —
 * only Settings > Developer Options > "Reset" or app uninstall clears it.
 */
object AnimScale {

    private val KEYS = listOf(
        "window_animation_scale",
        "transition_animation_scale",
        "animator_duration_scale"
    )

    private const val PREFS_NAME = "anim_scale_prefs"
    private const val KEY_OFF_SCALE = "off_scale"

    /** Default "off" scale if the user hasn't customized it. */
    const val DEFAULT_OFF_SCALE = 0.5f

    /** Value used for "on" / normal speed. */
    const val SCALE_ON = 1f

    class PermissionMissingException : Exception(
        "WRITE_SECURE_SETTINGS not granted. Run: adb shell pm grant " +
            "com.ricafolio.animscaletoggle android.permission.WRITE_SECURE_SETTINGS"
    )

    /** The user's configured "off" scale value (defaults to 0.5f). */
    fun getOffScale(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_OFF_SCALE, DEFAULT_OFF_SCALE)
    }

    /** Persists the user's chosen "off" scale value for future toggles/shortcuts/tile/widget. */
    fun setOffScale(context: Context, value: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_OFF_SCALE, value).apply()
    }

    /** Returns true if scales are currently set to the configured "off" value. */
    fun isOff(context: Context): Boolean {
        val offScale = getOffScale(context)
        return KEYS.all { key ->
            Settings.Global.getFloat(context.contentResolver, key, 1f) == offScale
        }
    }

    /** Sets all three keys to [scale]. Throws PermissionMissingException if not granted. */
    @Throws(PermissionMissingException::class)
    fun setScale(context: Context, scale: Float) {
        try {
            for (key in KEYS) {
                Settings.Global.putFloat(context.contentResolver, key, scale)
            }
        } catch (e: SecurityException) {
            throw PermissionMissingException()
        }
    }

    /** Flips current state: off -> on, on/anything-else -> off (using the configured off scale). Returns the new "isOff" state. */
    @Throws(PermissionMissingException::class)
    fun toggle(context: Context): Boolean {
        val goingOff = !isOff(context)
        setScale(context, if (goingOff) getOffScale(context) else SCALE_ON)
        return goingOff
    }
}
