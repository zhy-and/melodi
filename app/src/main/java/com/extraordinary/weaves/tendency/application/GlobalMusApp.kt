package com.extraordinary.weaves.tendency.application

import android.app.Activity
import android.text.TextUtils
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.workmanager.CommonRequestTools
import com.extraordinary.weaves.tendency.BuildConfig
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Folder
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.analytics.MelodiPushAnalytics
import com.extraordinary.weaves.tendency.notification.localpush.LocalEventPushBootstrap
import com.extraordinary.weaves.tendency.notification.localpush.LocalPushTelemetry
import com.extraordinary.weaves.tendency.notification.localpush.LocalPushUi
import com.extraordinary.weaves.tendency.runtime.MelodiRuntime
import com.extraordinary.weaves.tendency.runtime.RuntimeRemoteConfig
import com.extraordinary.weaves.tendency.runtime.UserSegmentHelper
import com.extraordinary.weaves.tendency.widget.WidgetPinHelper
import com.google.android.gms.ads.MobileAds
import com.tencent.mmkv.MMKV
import dora.BaseApplication
import dora.db.Orm
import dora.db.OrmConfig
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.bytedance.sdk.openadsdk.api.PAGConstant
import com.extraordinary.interstings.studios.utils.CommonStudyUtils.weaveLauStatus
import com.google.ads.mediation.pangle.PangleMediationAdapter
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.unity3d.ads.metadata.MetaData
import java.net.HttpURLConnection

internal fun shouldRunFullAppInitialization(processName: String, packageName: String): Boolean =
    processName == packageName

/**
 *
 */
class GlobalMusApp : BaseApplication(), AppConfig {

    private var referrerClient: InstallReferrerClient? = null

    /**
     * 全局的音乐播放控制管理器。
     */
    var mediaManager: MediaManager? = null
        private set

    companion object {
        /**
         * 全局Application单例。
         */
        var instance: GlobalMusApp? = null
            private set

        var referStatusCallBack = false
        var isClicked = false

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (!shouldRunFullAppInitialization(ProcessUtils.getCurrentProcessName(), packageName)) {
            initDb()
            return
        }
        init()
    }
    private fun initReferInfos() {
        try {

            referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient?.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    referStatusCallBack = true
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            LogUtils.e("InstallReferrerClient success")
                            try {
                                referrerClient?.installReferrer?.let {
                                    val referrerUrl: String = it.installReferrer
                                    LogUtils.e("InstallReferrerClient","InstallReferrerClient success$referrerUrl")
                                    if (!TextUtils.isEmpty(referrerUrl)){
                                        MMKV.defaultMMKV().putString(MUSIC_CONSTANT_APP_REFER_INFO,referrerUrl)
                                        UserSegmentHelper.onReferInfoUpdated(this@GlobalMusApp)
                                        referrerClient?.endConnection()
                                    }
                                }

                            }catch (e : Exception){
                                referrerClient?.endConnection()
                                e.printStackTrace()
                            }

                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            // API not available on the current Play Store app.
                            referrerClient?.endConnection()
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            // Connection couldn't be established.
                            referrerClient?.endConnection()
                        }
                    }
                    UserSegmentHelper.notifyReferFlowFinished(this@GlobalMusApp)
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
            if (TextUtils.isEmpty(MMKV.defaultMMKV().getString("ad_id",""))){
                Thread{
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
                    adInfo?.id?.let {
                        MMKV.defaultMMKV().putString("ad_id",it)
                    }
                }.start()
            }
            AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener{
                override fun onForeground(activity: Activity?) {
                    MelodiRuntime.onAppForeground(this@GlobalMusApp)
                    LocalEventPushBootstrap.refreshOnForeground(this@GlobalMusApp)
                }

                override fun onBackground(activity: Activity?) {
                    weaveLauStatus = false
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
    private fun init() {
        weaveLauStatus = true
        initAF()
        MMKV.initialize(this)
        UserSegmentHelper.startGate(this)
        initDb()    // 初始化SQLite数据库的表
        initMedia() // 初始化媒体管理器
        LogUtils.getConfig().isLogSwitch = BuildConfig.DEBUG
        initManager()
        LocalPushTelemetry.listener = object : LocalPushTelemetry.Listener {
            override fun funnel(context: android.content.Context, values: Map<String, String>) {
                MelodiPushAnalytics.logLpFunnel(context, values)
            }
            override fun report(context: android.content.Context, route: String, reason: String, result: String) {
                MelodiPushAnalytics.reportBackend(context, "10008", route, reason, result)
            }
        }
        LocalPushUi.widgetPinnedChecker = { WidgetPinHelper.hasPinnedWidget(it) }
        LocalEventPushBootstrap.start(this)
        MelodiRuntime.bootstrap(this)
        RuntimeRemoteConfig.refreshIfNeeded(this)

    }

    private fun initAF() {
//        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().init("PHgNNUUFqYqzC5S9NAop7N", null, this)
        AppsFlyerLib.getInstance().start(this)

    }

    private fun initManager() {
        try {
            PangleMediationAdapter.setGDPRConsent(PAGConstant.PAGGDPRConsentType.PAG_GDPR_CONSENT_TYPE_CONSENT)
            MetaData(this)["gdpr.consent"] = true
            MBridgeSDKFactory.getMBridgeSDK().setConsentStatus(this,MBridgeConstans.IS_SWITCH_ON)
            Thread{
                MobileAds.initialize(this)
            }.start()
            CommonRequestTools.get().initNetWork()
            initReferInfos()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private fun initMedia() {
        mediaManager = MediaManager(this)
    }


    private fun initDb() {
        Orm.init(this, OrmConfig.Builder()
            .database(AppConfig.DB_NAME)
            .version(AppConfig.DB_VERSION)
            .tables(
                Music::class.java, Artist::class.java,
                Album::class.java, Folder::class.java)
            .build())
    }
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
//        if (level == TRIM_MEMORY_UI_HIDDEN && !isClicked) {
//            ActivityUtils.finishAllActivities()
//        }
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            ActivityUtils.finishAllActivities()
        }
    }

}
