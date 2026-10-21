package com.extraordinary.weaves.tendency.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AppWidgetPinReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_PIN_SUCCESS) return
        Log.i("AppWidgetPin", "Widget pinned from ${intent.getStringExtra(WidgetPinHelper.EXTRA_FROM_KEY).orEmpty()}")
        MusicPlayerAppWidgetProvider.refreshAll(context)
    }

    companion object {
        const val ACTION_PIN_SUCCESS = "com.extraordinary.weaves.tendency.action.WIDGET_PIN_SUCCESS"
    }
}
