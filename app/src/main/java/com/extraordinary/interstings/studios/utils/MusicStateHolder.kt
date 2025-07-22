package com.extraordinary.interstings.studios.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.extraordinary.weaves.tendency.bean.Music

// MusicStateHolder.kt
object MusicStateHolder {

    // 缓冲状态 (true表示正在缓冲)
    val bufferingState = MutableLiveData<Boolean>()


    fun updateBufferingState(isBuffering: Boolean) {
        bufferingState.postValue(isBuffering)
    }
    
}