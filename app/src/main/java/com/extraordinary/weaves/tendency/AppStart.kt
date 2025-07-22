package com.extraordinary.weaves.tendency

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.beans.ViewModelRemove
import com.extraordinary.interstings.studios.goodads.GoogleMobileAdsConsentManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.ApiStudios.Companion.lauchEventsUpload
import com.extraordinary.interstings.studios.services.ApiStudios.Companion.loadBaseInfos
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.checkOUsers
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.databinding.StartModeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicBoolean

class AppStart : baseActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    companion object{
        var ouUserIsCallBack = false
    }
    private  lateinit var modeBinding: StartModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        try {
            ImmersionBar.with(this)
                .transparentStatusBar()
                .transparentNavigationBar()
                .fullScreen(true)
                .init()
            modeBinding = StartModeBinding.inflate(layoutInflater)
            setContentView(modeBinding.root)
            loadHomeConfig()
            if (MMKV.defaultMMKV().getLong("first_open",0L) == 0L){
                MMKV.defaultMMKV().putLong("first_open",System.currentTimeMillis())
            }
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    LogUtils.e( "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Log and toast
                LogUtils.e(task.result)
            })
        }catch (e : Exception){
            e.printStackTrace()
        }

        if (!checkOUsers()){
            ouUserIsCallBack = true
        }
        startInit()
        if (checkOUsers()){
            createView()
        }else{
            toHomePage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        messManager?.removeCallbacksAndMessages(null)
        modeBinding?.loadings?.cancelAnimation()

    }

    private fun startInit() {
        try {
            messManager =  Handler(Looper.getMainLooper())
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
            loadBaseInfos(this,false)
            lauchEventsUpload()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun destroyHd() {
        try {
            messManager?.removeCallbacksAndMessages(null)
            messManager = null
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun remove(re: ViewModelRemove?) {
        LogUtils.e(" EventAdvenseRemove------")
        if (re?.isShow == true) {
            destroyHd()
        }
    }

    override fun onPause() {
        super.onPause()
    }



    private val adsInitializeCalled = AtomicBoolean(false)

    private fun createView() {
        val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(
            GlobalMusApp.instance
        )
        googleMobileAdsConsentManager?.gatherConsent(
            this
        ) { consentError ->
            if (consentError != null) {
                // Consent not obtained in current session.
                LogUtils.e(
                    java.lang.String.format(
                        "%s: %s",
                        consentError.getErrorCode(),
                        consentError.getMessage()
                    )
                )
            }
            ouUserIsCallBack = true
            LogUtils.e("oumeng load  flag -- "+googleMobileAdsConsentManager?.canRequestAds())
            initializeMobileAdsSdk()
        }
        if (googleMobileAdsConsentManager?.canRequestAds() == true) {
            LogUtils.e("oumeng err 222 ---- ")

            initializeMobileAdsSdk()
        }else{
            LogUtils.e("oumeng err  ---- ")
        }
    }

    private fun initializeMobileAdsSdk() {
        if (adsInitializeCalled.getAndSet(true)) {
//            LogUtils.e("UM TAG ", "getAndSet")
            return
        }
        if (messManager == null){
            LogUtils.e("jumped   err---- ")
            return
        }
        LogUtils.e("oumeng 111  ---- ")

        // Initialize the Mobile Ads SDK.
        MainHomeStudy.homeStudy.initStartAds(this)
        ThreadUtils.runOnUiThread {
            if (!CommonStudyUtils.weavesIsCanLoadStart()){
//                MainHomeStudy.homeStudy.initLoadInter(this) // can't load kp to load inter
            }
            MainHomeStudy.homeStudy.musWeavesLoadNatives()
        }
        toHomePage()
    }


    private var messManager : Handler ?= null


    private fun toHomePage() {
        messManager?.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 10000)
    }

}