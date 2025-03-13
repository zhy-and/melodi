package com.extraordinary.weaves.tendency.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.google.firebase.analytics.FirebaseAnalytics

fun logMusicEvents(eventName: String){
    try {
        Log.e("-events-","$eventName")
       GlobalMusApp?.instance?.let {
           FirebaseAnalytics.getInstance(it).logEvent(eventName,null)
       }
    }catch (e : Exception){
        e.printStackTrace()
    }

}

fun logMusicEvents(eventName: String,musicCount : String){
    try {
        Log.e("-events-","$eventName"+"-------"+musicCount)
       GlobalMusApp?.instance?.let {
           FirebaseAnalytics.getInstance(it).logEvent(eventName, Bundle().apply { putString("music_count",musicCount) })
       }
    }catch (e : Exception){
        e.printStackTrace()
    }

}

@JvmOverloads
fun createDialog(
    context: Context,
    layoutResId: Int,
    isCanceled: Boolean,
    gravity: Int = Gravity.BOTTOM,
    style: Int? = null,
    block: (AppCompatDialog.() -> Unit)? = null
): AppCompatDialog = AppCompatDialog(context, R.style.PopTheme).also { dialog ->
    dialog.setContentView(layoutResId)
    dialog.setCanceledOnTouchOutside(isCanceled)
    val window = dialog.window
    val layoutParams = window?.attributes
    layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
    layoutParams?.gravity = gravity
    window?.attributes = layoutParams
    style?.let { window?.setWindowAnimations(it) }
    block?.let { it(dialog) }
}

@JvmOverloads
fun showComDialog(
    context: Context,
    layoutResId: Int,
    isCanceled: Boolean = true,
    gravity: Int = Gravity.BOTTOM,
    style: Int? = null,
    block: (AppCompatDialog.() -> Unit)? = null
): AppCompatDialog = createDialog(context, layoutResId, isCanceled, gravity, style, block).apply {
    show()
}

const val event_one_search_home = "search_home"
const val event_one_setting_home = "setting_home"
const val event_one_tracks_home = "tracks_home"
const val event_one_playlists_home = "playlists_home"
const val event_one_albums_home = "albums_home"
const val event_one_artists_home = "artists_home"
const val event_one_styles_home = "styles_home"
const val event_one_random_home = "random_home"
const val event_one_shuffle_home = "shuffle_home"
const val event_one_play_home = "play_home"
const val event_one_scan_home = "scan_home"
const val event_one_feedback_home = "feedback_home"
const val event_one_re_album_more = "re_album_more"
const val event_one_re_album = "re_album"
const val event_one_re_artist_more = "re_artist_more"
const val event_one_re_artist = "re_artist"
const val event_one_re_playlist_more = "re_playlist_more"
const val event_one_re_playlist = "re_playlist"
const val event_one_re_style_more = "re_style_more"
const val event_one_re_style = "re_style"
const val event_one_most_played_more = "most_played_more"
const val event_one_most_played = "most_played"
const val event_one_for_you = "for_you"
const val event_one_for_you_collect = "for_you_collect"
const val event_one_play_bar = "play_bar"

const val event_permit_scan_agree = "permit_scan_agree"
const val event_permit_scan_refuse = "permit_scan_refuse"
const val event_permit_notice_agree = "permit_notice_agree"
const val event_permit_notice_refuse = "permit_notice_refuse"

const val event_two_search_ok = "search_ok"
const val event_two_scan_setting = "scan_setting"
const val event_two_feedback_setting = "feedback_setting"
const val event_two_privacy_policy_setting = "privacy_policy_setting"
const val event_two_search_2 = "search_2"
const val event_two_playlists_2 = "playlists_2"
const val event_two_albums_2 = "albums_2"
const val event_two_artists_2 = "artists_2"
const val event_two_styles_2 = "styles_2"
const val event_two_playlist_track2 = "playlist_track2"
const val event_two_track_click = "track_click"
const val event_two_track_edit = "track_edit"
const val event_two_track_2 = "track_2"
const val event_two_artist_playlist2 = "artist_playlist2"
const val event_two_create_playlist = "create_playlist"
const val event_two_playlist_default = "playlist_default"
const val event_two_playlist_fav = "playlist_favourite"
const val event_two_playlist_created = "playlist_created"
const val event_two_style_album2 = "style_album2"
const val event_two_album_click = "album_click"
const val event_two_album_edit = "album_edit"
const val event_two_album_artist2 = "album_artist2"
const val event_two_artist_click = "artist_click"
const val event_two_artist_edit = "artist_edit"
const val event_two_track_style2 = "track_style2"
const val event_two_style_click = "style_click"

const val event_th_tracks_no = "tracks_no"
const val event_th_playlists_no = "playlists_no"
const val event_th_albums_no = "albums_no"
const val event_th_artists_no = "artists_no"
const val event_th_styles_no = "styles_no"
const val event_th_tracks_r = "tracks_r"
const val event_th_albums_r = "albums_r"
const val event_th_artists_r = "artists_r"
const val event_th_start_scan = "start_scan"
const val event_th_submit_feed = "submit_feed"
const val event_th_play_artist = "play_artist"
const val event_th_play_lyrics = "play_lyrics"
const val event_th_play_mode = "play_mode"
const val event_th_play_collect = "play_collect"
const val event_th_play_album = "play_album"
const val event_th_play_queue = "play_queue"
const val event_th_play_previous = "play_previous"
const val event_th_play_next = "play_next"
const val event_th_play_play = "play_play"
const val event_th_play_pause = "play_pause"
const val event_th_playlist_play = "playlist_play"
const val event_th_playlist_track = "playlist_track"
const val event_th_playlist_edit3 = "playlist_edit3"
const val event_th_playlist_created_empty_play = "playlist_cre_em_play"
const val event_th_playlist_created_empty_add = "playlist_cre_em_addbtn"
const val event_th_playlist_created_play = "playlist_cre_play"
const val event_th_playlist_created_add = "playlist_cre_addicon"
const val event_th_playlist_created_delete = "playlist_cre_delete"
const val event_th_playlist_created_track = "playlist_cre_track"
const val event_th_playlist_created_edit = "playlist_cre_edit"
const val event_th_album_play = "album_play"
const val event_th_album_track = "album_track"
const val event_th_album_edit_3 = "album_edit_3"
const val event_th_artist_play = "artist_play"
const val event_th_artist_track = "artist_track"
const val event_th_artist_edit_3 = "artist_edit_3"
const val event_th_style_play = "style_play"
const val event_th_style_track = "style_track"
const val event_th_style_edit_3 = "style_edit_3"







