package com.extraordinary.weaves.tendency

import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_NAME
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.core.MusicScanner
import com.jeremyliao.liveeventbus.LiveEventBus
import com.blankj.utilcode.util.LogUtils
import java.util.concurrent.Executors

fun performScanMusic() {
    scanMusic()
}

/**
 * 扫描歌曲。
 */
private fun scanMusic() {
    try {
        Executors.newCachedThreadPool().submit {
            try {
                GlobalMusApp.instance?.let {
                    val playlist = MusicScanner.scan(it) as MutableList<Music>
                    for (i in playlist){
                        LogUtils.e(""+i.songId+"---"+i.albumId+"name ="+i.musicName+"dura ="+i.duration+"art ="+i.artist)
                    }
                    GlobalMusApp.instance?.mediaManager?.refreshPlaylist(playlist)
                    prepareSongs(playlist)
                    LiveEventBus
                        .get<String>(REFRESH_NAME)
                        .post("")
                }
            }

            finally {

            }
        }
    }catch (e : Exception){
        e.printStackTrace()
    }

}

 fun prepareSongs(musics: List<Music>) {
    try {
        GlobalMusApp.instance?.mediaManager?.refreshPlaylist(musics as MutableList)
        if (musics.isEmpty()){
            return
        }
        GlobalMusApp.instance?.mediaManager?.loadCurMusic(musics[0])
    }catch (e : Exception){
        e.printStackTrace()
    }
}