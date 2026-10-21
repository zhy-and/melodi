package com.extraordinary.weaves.tendency.notification.localpush

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.extraordinary.weaves.tendency.widget.MusicPlayerAppWidgetProvider

/** Receives playback changes from MediaService's separate :appmedia process. */
class PlaybackBridgeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_AUDIO_FOCUS_LOST, false)) {
            AudioFocusPushBridge.onTransientAudioFocusLoss(context)
            return
        }
        val hasSong = intent.getBooleanExtra(EXTRA_HAS_SONG, false)
        val isPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, false)
        PlaybackPushWatcher.onPlaybackChanged(context, hasSong, isPlaying)
        MusicPlayerAppWidgetProvider.updatePlayback(
            context,
            intent.getStringExtra(EXTRA_TITLE),
            intent.getStringExtra(EXTRA_ARTIST),
            intent.getLongExtra(EXTRA_DURATION, 0L),
            intent.getLongExtra(EXTRA_POSITION, 0L),
            isPlaying,
        )
    }

    companion object {
        const val EXTRA_AUDIO_FOCUS_LOST = "playback_audio_focus_lost"
        const val EXTRA_HAS_SONG = "playback_has_song"
        const val EXTRA_IS_PLAYING = "playback_is_playing"
        const val EXTRA_TITLE = "playback_title"
        const val EXTRA_ARTIST = "playback_artist"
        const val EXTRA_DURATION = "playback_duration"
        const val EXTRA_POSITION = "playback_position"
    }
}
