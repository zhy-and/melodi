package com.extraordinary.weaves.tendency.event

interface AppEvent {

    companion object {

        /**
         * 刷新本地歌曲数量。
         */
        const val REFRESH_LOCAL_NUMS = "refresh_local_numbers"

        /**
         * 刷新播放列表。
         */
        const val REFRESH_MUSIC_PLAY_LIST = "refresh_music_play_list"


        const val REFRESH_MUSIC_HOME_MPS_INVALID = "REFRESH_MUSIC_HOME_MPS_INVALID"
        const val REFRESH_MUSIC_HOME_MPS_PAUSE = "REFRESH_MUSIC_HOME_MPS_PAUSE"
        const val REFRESH_MUSIC_HOME_MPS_PLAYING = "REFRESH_MUSIC_HOME_MPS_PLAYING"
        const val REFRESH_MUSIC_HOME_MPS_PREPARE = "REFRESH_MUSIC_HOME_MPS_PREPARE"
        const val REFRESH_MUSIC_HOME_JK_REFRESH = "REFRESH_MUSIC_HOME_JK_REFRESH"
        const val REFRESH_NAME = "REFRESH_NAME"
        const val REFRESH_MUSIC_LOADING = "aaa"
    }
}