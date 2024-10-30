package com.extraordinary.weaves.tendency.application

import android.text.TextUtils
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.workmanager.CommonRequestTools
import com.extraordinary.weaves.tendency.BuildConfig
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Folder
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.google.android.gms.ads.MobileAds
import com.tencent.mmkv.MMKV
import dora.BaseApplication
import dora.db.Orm
import dora.db.OrmConfig

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
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
    private fun init() {
        MMKV.initialize(this)
        initDb()    // 初始化SQLite数据库的表
        initMedia() // 初始化媒体管理器
        LogUtils.getConfig().isLogSwitch = BuildConfig.DEBUG
        initManager()
    }

    private fun initManager() {
        MobileAds.initialize(this)
        CommonRequestTools.get().initNetWork()
        initReferInfos()
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
