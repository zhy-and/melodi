package com.extraordinary.weaves.tendency

import android.content.Context
import dora.util.SPUtils

class PreferencesManager(val context: Context) {

    fun getBassBoost(): Boolean {
        return SPUtils.readBoolean(context, PREFS_BASS_BOOST_ENABLE, false)
    }

    fun getFilterSize(): Boolean {
        return SPUtils.readBoolean(context, PREFS_FILTER_SIZE, false)
    }

    fun getFilterTime(): Boolean {
        return SPUtils.readBoolean(context, PREFS_FILTER_TIME, false)
    }

    fun getEqualizerDecibels(): String {
        return SPUtils.readString(context, PREFS_EQUALIZER_DECIBELS, "")
    }

    companion object {

        const val PREFS_FILTER_SIZE = "prefs_filter_size"
        const val PREFS_FILTER_TIME = "prefs_filter_time"
        const val PREFS_BASS_BOOST_ENABLE = "prefs_bass_boost_enable"
        const val PREFS_EQUALIZER_DECIBELS = "prefs_equalizer_decibels"
    }
}
