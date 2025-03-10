package com.extraordinary.weaves.tendency

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
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
    fun startInterActivity(intent: Intent, pos : Long,isFinish : Boolean = true){
        if (!CommonStudyUtils.posIsCanShow(pos) && MainHomeStudy.homeStudy.uniqueInter == null){
            MainHomeStudy.homeStudy.createInter(pos)
            showLoadingBar()
            dora.util.ThreadUtils.runOnUiThreadDelayed({
                cancelLoadingBar()
                startActivity(intent)
                if (isFinish){
                    finish()
                }
            },3000)
        }else{
            startActivity(intent)
            if (isFinish){
                finish()
            }
        }
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

    @JvmField
    var loadingStart: AlertDialog? = null
    open fun showLoadingBar() {
        ThreadUtils.runOnUiThread {
            try {
                if (loadingStart == null){
                    loadingStart = AlertDialog.Builder(this).create()
                    loadingStart!!.window!!.setBackgroundDrawable(ColorDrawable())
                    loadingStart!!.setCancelable(false)
                    loadingStart!!.setOnKeyListener { _, keyCode, event -> keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK }
                }
//                if (null != commonDialog && commonDialog!!.isShowing && !isFinishing && !isDestroyed) {
//                    commonDialog!!.dismiss()
//                }
                if (null != loadingStart && !loadingStart!!.isShowing && !isFinishing && !isDestroyed) {
                    loadingStart!!.show()
                    loadingStart!!.setContentView(R.layout.loadings)
                    loadingStart!!.setCanceledOnTouchOutside(false)
                }

            }catch (e : Exception){
                e.printStackTrace()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != loadingStart && loadingStart!!.isShowing && !isFinishing && !isDestroyed) {
            loadingStart!!.dismiss()
        }
    }
    open fun cancelLoadingBar() {
        ThreadUtils.runOnUiThread {
            try {
                if (null != loadingStart && loadingStart!!.isShowing && !isFinishing && !isDestroyed) {
                    loadingStart!!.dismiss()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
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