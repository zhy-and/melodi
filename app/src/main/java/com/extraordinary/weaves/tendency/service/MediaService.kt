package com.extraordinary.weaves.tendency.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import androidx.annotation.RequiresApi
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.ACTION_CANCEL
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.ACTION_NEXT
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.ACTION_PAUSE_RESUME
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.ACTION_PREV
import com.extraordinary.weaves.tendency.core.MusicControl
import com.extraordinary.weaves.tendency.core.MusicUtils
import com.extraordinary.weaves.tendency.PreferencesManager
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.core.SimpleAudioPlayer
import com.extraordinary.weaves.tendency.bean.IMediaService
import com.extraordinary.weaves.tendency.bean.Music


/**
 * 这个服务单独开启一个进程，用来在Android系统后台播放音乐。
 */
class MediaService : Service(){

    /**
     * 音乐播放流程控制。
     */
    private lateinit var mc: MusicControl
    private var binder: IBinder? = null

    /**
     * 更新通知栏。
     */
    private var notificationManager: NotificationManager? = null
    private var controlBroadcast: ControlBroadcast? = null
    private var prefsManager: PreferencesManager? = null
    private var simplePlayer: SimpleAudioPlayer? = null
    private val playbackBridgeHandler = Handler(Looper.getMainLooper())
    private val playbackBridgeTicker = object : Runnable {
        override fun run() {
            if (::mc.isInitialized && mc.isPlaying) {
                mc.publishPlaybackBridgeSnapshot()
            }
            playbackBridgeHandler.postDelayed(this, 15_000L)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        binder = MediaServiceImpl()
        return binder
    }



    inner class ControlBroadcast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE_RESUME -> {
                    if (mc.isPlaying) {
                        mc.pause()
                    } else {
                        mc.replay()
                    }
                    val title = intent.getStringExtra(NOTIFICATION_TITLE) ?: ""
                    val name = intent.getStringExtra(NOTIFICATION_NAME) ?: ""
                    val music = mc.curMusic
                    val defaultArtwork = BitmapFactory.decodeResource(this@MediaService.resources,
                        R.drawable.ic_launcher_background
                    )
                    val bitmap = MusicUtils.getCachedArtwork(
                        this@MediaService, music.albumId.toLong(),
                        defaultArtwork
                    )
//                    updateNotification(bitmap, title, name)
                }
                ACTION_NEXT -> mc.next()
                ACTION_PREV -> mc.prev()
                ACTION_CANCEL -> {
                    cancelNotification()
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    fun killAllOtherProcess(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcessList = am.runningAppProcesses ?: return
        for (ai in appProcessList) {
            if (ai.uid == android.os.Process.myUid() && ai.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(ai.pid)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        controlBroadcast = ControlBroadcast()
        val filter = IntentFilter()
        filter.addAction(ACTION_PAUSE_RESUME)
        filter.addAction(ACTION_PREV)
        filter.addAction(ACTION_NEXT)
        filter.addAction(ACTION_CANCEL)
        if (Build.VERSION.SDK_INT >= 34){
            registerReceiver(controlBroadcast, filter,RECEIVER_NOT_EXPORTED)
        }else{
            registerReceiver(controlBroadcast, filter)
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        prefsManager = PreferencesManager(this)
        simplePlayer = SimpleAudioPlayer(this)
        mc = MusicControl(this)
        playbackBridgeHandler.postDelayed(playbackBridgeTicker, 15_000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackBridgeHandler.removeCallbacks(playbackBridgeTicker)
        unregisterReceiver(controlBroadcast)
        mc.exit()
        simplePlayer?.exit()
    }

    private inner class MediaServiceImpl : IMediaService.Stub(), IBinder {

        @Throws(RemoteException::class)
        override fun play(pos: Int): Boolean {
            return mc.play(pos)
        }

        @Throws(RemoteException::class)
        override fun playById(id: Int): Boolean {
            return mc.playById(id)
        }

        @Throws(RemoteException::class)
        override fun playByPath(path: String) {
            mc.play(path)
        }

        @Throws(RemoteException::class)
        override fun playByUrl(music: Music, url: String) {
            mc.playByUrl(music, url)
        }

        @Throws(RemoteException::class)
        override fun replay(): Boolean {
            return mc.replay()
        }

        @Throws(RemoteException::class)
        override fun pause(): Boolean {
            return mc.pause()
        }

        @Throws(RemoteException::class)
        override fun prev(): Boolean {
            return mc.prev()
        }

        @Throws(RemoteException::class)
        override fun next(): Boolean {
            return mc.next()
        }

        @Throws(RemoteException::class)
        override fun stop() {
            mc.stop()
        }

        @Throws(RemoteException::class)
        override fun duration(): Int {
            return mc.duration()
        }

        @Throws(RemoteException::class)
        override fun setCurMusic(music: Music) {
            return mc.setCurMusic(music)
        }

        @Throws(RemoteException::class)
        override fun position(): Int {
            return mc.position()
        }

        @Throws(RemoteException::class)
        override fun pendingProgress(): Int {
            return mc.pendingProgress()
        }

        @Throws(RemoteException::class)
        override fun seekTo(progress: Int): Boolean {
            return mc.seekTo(progress)
        }

        @Throws(RemoteException::class)
        override fun refreshPlaylist(playlist: List<Music>) {
            mc.refreshPlaylist(playlist)
        }

        @Throws(RemoteException::class)
        override fun setBassBoost(strength: Int) {
            mc.setBassBoost(strength)
        }

        @Throws(RemoteException::class)
        override fun setEqualizer(bandLevels: IntArray) {
            mc.setEqualizer(bandLevels)
        }

        @Throws(RemoteException::class)
        override fun getEqualizerFreq(): IntArray {
            return mc.equalizerFreq
        }

        @Throws(RemoteException::class)
        override fun getPlayState(): Int {
            return mc.playState
        }

        @Throws(RemoteException::class)
        override fun getPlayMode(): Int {
            return mc.playMode
        }

        @Throws(RemoteException::class)
        override fun setPlayMode(mode: Int) {
            mc.playMode = mode
        }

        @Throws(RemoteException::class)
        override fun getCurMusicId(): Int {
            // 获取的是歌曲的id，而非数据库主键id
            return mc.curMusic.songId
        }
        @Throws(RemoteException::class)
        override fun getCurrentIndex(): Int {
            // 获取的是当前播放位置index
            return mc.curMusic.songId
        }

        @Throws(RemoteException::class)
        override fun loadCurMusic(music: Music): Boolean {
            return mc.loadCurMusic(music)
        }

        @Throws(RemoteException::class)
        override fun getCurMusic(): Music {
            return mc.curMusic
        }

        @Throws(RemoteException::class)
        override fun getPlaylist(): List<Music> {
            return mc.playlist
        }
    }


    private fun cancelNotification() {
    }

    companion object {
        private const val NOTIFICATION_ID = 0x100
        private const val NOTIFICATION_TITLE = "notification_title"
        private const val NOTIFICATION_NAME = "notification_name"
    }
}
