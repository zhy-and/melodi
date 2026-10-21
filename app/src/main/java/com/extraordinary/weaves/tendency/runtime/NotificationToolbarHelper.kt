package com.extraordinary.weaves.tendency.runtime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.AppStart
import com.extraordinary.weaves.tendency.HomeActivity
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.scan.ScanPage

object NotificationToolbarHelper {
    const val NOTIFICATION_ID = 1901
    const val CHANNEL_ID = "toolbar_channel_v3"
    const val EXTRA_ACTION = "extra_toolbar_action"

    fun build(context: Context): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_toolbar_note)
            .setColor(context.getColor(R.color.notif_toolbar_play))
            .setContentTitle(context.getString(R.string.notif_toolbar_brand_title))
            .setContentText(context.getString(R.string.notif_toolbar_hint))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent(context, NotificationToolbarAction.HOME, 0))
        if (Build.MANUFACTURER.equals("samsung", true) && Build.VERSION.SDK_INT >= 34) {
            return builder
                .addAction(R.drawable.ic_notif_play, context.getString(R.string.notif_toolbar_play_pause), playbackPendingIntent(context, 1))
                .addAction(R.drawable.ic_notif_toolbar_playlist, context.getString(R.string.notif_toolbar_playlists), activityPendingIntent(context, NotificationToolbarAction.LIBRARY, 2))
                .addAction(R.drawable.ic_notif_audio_interrupt, context.getString(R.string.notif_toolbar_scan), activityPendingIntent(context, NotificationToolbarAction.SCAN, 3))
        }
        val collapsed = RemoteViews(context.packageName, R.layout.notification_toolbar_compact)
        val expanded = RemoteViews(context.packageName, R.layout.notification_toolbar_expanded)
        for (view in listOf(collapsed, expanded)) {
            view.setOnClickPendingIntent(R.id.notif_action_play_pause, playbackPendingIntent(context, 1))
            view.setOnClickPendingIntent(R.id.notif_action_library, activityPendingIntent(context, NotificationToolbarAction.LIBRARY, 2))
            view.setOnClickPendingIntent(R.id.notif_action_scan, activityPendingIntent(context, NotificationToolbarAction.SCAN, 3))
        }
        expanded.setOnClickPendingIntent(R.id.notif_header, activityPendingIntent(context, NotificationToolbarAction.HOME, 4))
        return builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(collapsed).setCustomBigContentView(expanded)
    }

    private fun activityPendingIntent(
        context: Context,
        action: NotificationToolbarAction,
        code: Int,
    ): PendingIntent {
        val target = when (action) {
            NotificationToolbarAction.LIBRARY -> HomeActivity::class.java
            NotificationToolbarAction.SCAN -> ScanPage::class.java
            else -> AppStart::class.java
        }
        val intent = Intent(context, target).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ACTION, action.wireValue)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, code, intent, flags)
    }

    private fun playbackPendingIntent(context: Context, code: Int): PendingIntent {
        val intent = Intent(AppConfig.ACTION_PAUSE_RESUME).setPackage(context.packageName)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, code, intent, flags)
    }
}
