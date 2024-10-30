package com.extraordinary.weaves.tendency

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.finishPageInterTwo
import com.extraordinary.weaves.tendency.core.MusicTimer


open class baseActivity : AppCompatActivity() {
    var timer : MusicTimer?= null
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }
    open fun receiverMsgCallBack(message: Message) {
    }


     fun speFinish() {
        if (checkPageOne()){
            LogUtils.e("------------------------f one back ")
            MainHomeStudy.homeStudy.getShowCyAds(finishPageInterOne(),this)
        }else if (checkPageTwo()){
            LogUtils.e("------------------------f two back ")
            MainHomeStudy.homeStudy.getShowCyAds(finishPageInterTwo(),this)
        }
        super.finish()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && checkPageOne()){
            LogUtils.e("------------------------one back ")
            MainHomeStudy.homeStudy.getShowCyAds(finishPageInterOne(),this)
            finish()
            return true
        }else if (keyCode == KeyEvent.KEYCODE_BACK && checkPageTwo()){
            LogUtils.e("------------------------two back ")
            MainHomeStudy.homeStudy.getShowCyAds(finishPageInterTwo(),this)
            finish()
            return true
        }
        return  super.onKeyDown(keyCode,event)
    }
    private fun checkPageOne(): Boolean {
        if (this.localClassName.contains("HomeActivity")
            || this.localClassName.contains("AppStart")
            || this.localClassName.contains("AppGeDanDetail")
            || this.localClassName.contains("AppAlbumDetail")
            || this.localClassName.contains("AppArtDetail")
            || this.localClassName.contains("AppLyricView")
            || this.localClassName.contains("FeedView")
            || this.localClassName.contains("PrivacyPage")
        ){
            return false
        }
        return true
    }

    private fun checkPageTwo(): Boolean {
        if (this.localClassName.contains("HomeActivity")
            || this.localClassName.contains("AppStart")
            || this.localClassName.contains("AppSearch")
            || this.localClassName.contains("AppMoreView")
            || this.localClassName.contains("ScanPage")
            || this.localClassName.contains("TrackList")
            || this.localClassName.contains("AppStyleList")
            || this.localClassName.contains("AppAlbumList")
            || this.localClassName.contains("AppArtistList")
            || this.localClassName.contains("AppGeDanList")
            || this.localClassName.contains("PlayerView")
            || this.localClassName.contains("AppPlayerList")
        ){
            return false
        }

        return true
    }
    open fun setTimer(handler: Handler){
            timer = MusicTimer(handler)
        }

}