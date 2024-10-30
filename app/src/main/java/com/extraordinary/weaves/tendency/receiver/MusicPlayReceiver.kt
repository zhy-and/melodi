package com.extraordinary.weaves.tendency.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_MPS_INVALID
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_MPS_PAUSE
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_MPS_PLAYING
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_MPS_PREPARE
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_PLAY_LIST
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.blankj.utilcode.util.LogUtils

open class MusicPlayReceiver(val mediaManager: MediaManager) : BroadcastReceiver() {



    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action = intent.action
            if (action != null && action == AppConfig.ACTION_PLAY) {
                val music = mediaManager.curMusic
                val playState = mediaManager.playState
                val pendingProgress = intent.getIntExtra("pending_progress", 0)
                when (playState) {
                    AppConfig.MPS_INVALID -> {  // 考虑后面加上如果文件不可播放直接跳到下一首
                        LogUtils.e("pendingProgress1")
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_HOME_MPS_INVALID)
                            .post("")
                    }
                    AppConfig.MPS_PAUSE -> {    //  刷新播放列表当前播放的条目
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_HOME_MPS_PAUSE)
                            .post("")
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_PLAY_LIST)
                            .post("")
                        LogUtils.e("pendingProgress2")
                    }
                    AppConfig.MPS_PLAYING -> {  //刷新播放列表当前播放的条目
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_HOME_MPS_PLAYING)
                            .post("")
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_PLAY_LIST)
                            .post("")
                        LogUtils.e("pendingProgress3")
                    }
                    AppConfig.MPS_PREPARE -> {
                        LiveEventBus
                            .get<String>(REFRESH_MUSIC_HOME_MPS_PREPARE)
                            .post("")
                        LogUtils.e("pendingProgress4")
                    }
                }
            }
        }catch (e : Exception){e.printStackTrace()}

    }
}