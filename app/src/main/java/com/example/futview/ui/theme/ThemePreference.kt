package com.example.futview.ui.theme

import android.content.Context

/** Persistencia simple del modo oscuro (sin dependencias extra). */
object ThemePreference {

    private const val PREFS_NAME = "futview_settings"
    private const val KEY_DARK = "dark_mode"

    fun isDark(context: Context): Boolean = context
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_DARK, false)

    fun setDark(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, enabled)
            .apply()
    }
}
