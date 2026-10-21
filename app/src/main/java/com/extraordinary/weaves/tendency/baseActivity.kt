package com.extraordinary.weaves.tendency

import android.app.AlertDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.extraordinary.weaves.tendency.notification.NotificationPermissionPolicy
import com.extraordinary.weaves.tendency.analytics.MelodiPushAnalytics
import com.extraordinary.weaves.tendency.runtime.MelodiRuntime
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.dialog.event_permit_notice_agree
import com.extraordinary.weaves.tendency.dialog.event_permit_notice_refuse
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.beans.MDataBeans
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.ApiStudios.Companion.commonConfig
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_CACHE_TM
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.finishPageInterTwo
import com.extraordinary.interstings.studios.workmanager.CommonRequestTools
import com.extraordinary.interstings.studios.workmanager.TransSpesInters
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.core.MusicTimer
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_JK_REFRESH
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_MUSIC_HOME_MPS_PLAYING
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.Transaction
import dora.db.dao.DaoFactory
import dora.util.PinyinUtils


open class baseActivity : AppCompatActivity() {

    private var notificationRequestInFlight = false
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            notificationRequestInFlight = false
            val result = NotificationPermissionPolicy.afterResult(
                granted || hasNotificationPermission(), notificationDenials(),
            )
            MMKV.defaultMMKV().putInt(NOTIFICATION_DENIALS_KEY, result.denials)
            MelodiPushAnalytics.logPermissionPath(this,
                if (result.nextStep == NotificationPermissionPolicy.NextStep.GRANTED)
                    "path_notif_perm_granted" else "path_notif_perm_denied")
            logMusicEvents(if (result.nextStep == NotificationPermissionPolicy.NextStep.GRANTED)
                event_permit_notice_agree else event_permit_notice_refuse)
            if (result.nextStep == NotificationPermissionPolicy.NextStep.GRANTED) {
                MelodiRuntime.onNotificationPermissionGranted(this)
            }
            onNotificationPermissionRequestFinished()
        }

    protected fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun notificationDenials(): Int = MMKV.defaultMMKV().getInt(NOTIFICATION_DENIALS_KEY, 0)

    fun requestNotificationPermissionAtLaunch(): Boolean {
        if (hasNotificationPermission()) {
            MelodiRuntime.onNotificationPermissionGranted(this)
            return false
        }
        if (NotificationPermissionPolicy.canLaunchRequest(
                notificationRequestInFlight,
                NotificationPermissionPolicy.shouldRequestAtLaunch(notificationDenials()),
            )) {
            MelodiPushAnalytics.logPermissionPath(this, "path_notif_perm_shown")
            notificationRequestInFlight = true
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return true
        }
        return false
    }

    fun requestNotificationPermissionAtHome() {
        if (hasNotificationPermission() || isFinishing) return
        if (NotificationPermissionPolicy.canLaunchRequest(
                notificationRequestInFlight,
                NotificationPermissionPolicy.shouldRequestAtHome(
                    notificationDenials(), shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS),
                ),
            )) {
            MelodiPushAnalytics.logPermissionPath(this, "path_notif_perm_home_retry")
            notificationRequestInFlight = true
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    protected open fun onNotificationPermissionRequestFinished() = Unit

    companion object {
        private const val NOTIFICATION_DENIALS_KEY = "push_notification_denials"
    }

    private val baseBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (BackNavigationPolicy.exitActionFor(localClassName)) {
                BackNavigationPolicy.ExitAction.FIRST_AD -> {
                    LogUtils.e("------------------------one back ")
                    MainHomeStudy.homeStudy.getShowCyAds(finishPageInterOne(), this@baseActivity)
                    finish()
                }
                BackNavigationPolicy.ExitAction.SECOND_AD -> {
                    LogUtils.e("------------------------two back ")
                    MainHomeStudy.homeStudy.getShowCyAds(finishPageInterTwo(), this@baseActivity)
                    finish()
                }
                BackNavigationPolicy.ExitAction.NONE -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, baseBackCallback)
    }

    val albumOnlineList: MutableList<Album> = ArrayList()
    val artOnlineList: MutableList<Artist> = ArrayList()
    var timer : MusicTimer?= null
    private fun insertAlbumArt(count: Int, names: String, id: String) {
        val albums =  Album().apply {
            album_name = names
            album_id =  id.substring(id.length-4).toInt()
            number_of_songs = count
        }

        albumOnlineList.add(albums)
    }

    private val art = DaoFactory.getDao(Artist::class.java)
    private val album = DaoFactory.getDao(Album::class.java)
    fun loadHomeConfig() {
        if( System.currentTimeMillis() - MMKV.defaultMMKV()
                .getLong("isSave", 0) < 4 * 60 * 60 * 1000){
            return
        }

       loadDetsConfig()
    }
    fun processAndInsertArtists(zxsnkzBeans: List<MDataBeans.YvbgjmfbteobhoBean.XgyvsjeazynBean.VscruffsqgprwaBean.NyjxynieiuBean.EfkqmycrBean.RzcurBean.ZxsnkzBean>) {
        try {
            // 1. 獲取現有數據庫中的歌手列表（用於去重）
            val existingArtists = art.selectAll().associateBy { it.name }
            val artistMap = mutableMapOf<String, Artist>()  // 用於臨時存儲處理後的歌手

            // 2. 處理數據
            zxsnkzBeans.forEach { bean ->
                val artistName = bean.plmkpq
                if (!artistMap.containsKey(artistName)) {
                    // 3. 檢查是否已存在（數據庫中或當前批次中）
                    val existingArtist = existingArtists[artistName]

                    // 4. 創建或更新歌手對象
                    val artist = existingArtist?.copy() ?: Artist().apply {
                        name = artistName
                        number_of_tracks = 0  // 初始化為0，後續會累加
                    }

                    // 5. 累加歌曲計數
                    artist.number_of_tracks += 1  // 每首歌曲對應的歌手計數+1

                    artistMap[artistName] = artist
                } else {
                    // 6. 如果當前批次已存在，只增加歌曲計數
                    artistMap[artistName]?.number_of_tracks = artistMap[artistName]?.number_of_tracks?.plus(1) ?: 0
                }
            }

            // 7. 執行事務插入
            if (artistMap.isNotEmpty()) {
                Transaction.execute(Artist::class.java) {
                    it.insert(artistMap.values.toList())
                }
            }
            artistMap.forEach{
                Log.e("arts----",it.toString())
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    fun processAndInsertAlbums(zxsnkzBeans: List<MDataBeans.YvbgjmfbteobhoBean.XgyvsjeazynBean.VscruffsqgprwaBean.NyjxynieiuBean.EfkqmycrBean.RzcurBean.ZxsnkzBean>) {
      try {
          // 1. 获取现有数据库中的专辑列表（用于去重）
          val existingAlbums = album.selectAll().associateBy { it.album_name }
          val albumMap = mutableMapOf<String, Album>()  // 用于临时存储处理后的专辑

          // 2. 处理数据
          zxsnkzBeans.forEach { bean ->
              val albumName = bean.yzfvr
              if (albumName.isNullOrEmpty()) {
                  return@forEach
              }

              if (!albumMap.containsKey(albumName)) {
                  // 3. 检查是否已存在（数据库中或当前批次中）
                  val existingAlbum = existingAlbums[albumName]

                  // 4. 创建或更新专辑对象
                  val album = existingAlbum?.copy() ?: Album().apply {
                      album_name = albumName
                      // 使用gcvoy字段的后4位作为专辑ID
                      album_id = bean.ddbcc?.takeLast(4)?.toIntOrNull() ?: albumName.hashCode()
                      number_of_songs = 0  // 初始化为0，后续会累加
                  }

                  // 5. 累加歌曲计数
                  album.number_of_songs += 1

                  albumMap[albumName] = album
              } else {
                  // 6. 如果当前批次已存在，只增加歌曲计数
                  albumMap[albumName]?.number_of_songs = albumMap[albumName]?.number_of_songs?.plus(1) ?: 0
              }
          }

          // 7. 执行事务插入
          if (albumMap.isNotEmpty()) {
              Transaction.execute(Album::class.java) {
                  it.insert(albumMap.values.toList())
              }
          }
          Log.e("albums----", albumMap.toString())
      }catch (e : Exception){
          e.printStackTrace()
      }

    }


    fun processAndInsertMusic(
        zxsnkzBeans: List<MDataBeans.YvbgjmfbteobhoBean.XgyvsjeazynBean.VscruffsqgprwaBean.NyjxynieiuBean.EfkqmycrBean.RzcurBean.ZxsnkzBean>,
    ) {
        try
        {
            // 1. 获取现有数据库中的音乐列表（用于去重）
            val existingMusic = musicDao.selectAll().map { it.songId }.toSet()
            val newMusicList = mutableListOf<Music>()

            // 2. 处理数据
            zxsnkzBeans.forEach { bean ->
                // 3. 创建音乐对象
                val music = Music().apply {
                    songId = bean.wqzh.hashCode() // 使用URL哈希作为唯一ID
                    musicName = bean.zpuqox // 音乐名称
                    artist = bean.plmkpq // 歌手名
                    duration = 0 // 初始时长设为0
                    data = bean.wqzh // MP3地址
                    folder = "Online" // 表示该音乐来自线上
                    musicNameKey = PinyinUtils.getPinyinFromSentence(musicName) // 拼音key
                    artistKey = PinyinUtils.getPinyinFromSentence(artist) // 歌手拼音key

                    // 处理专辑关联（取第一个专辑作为主要专辑）
                    albumId = bean.ddbcc?.takeLast(4)?.toInt() ?:-1

                }

                // 4. 检查是否已存在
                if (!existingMusic.contains(music.songId)) {
                    newMusicList.add(music)
                }
            }

            // 5. 执行事务插入
            if (newMusicList.isNotEmpty()) {
                Transaction.execute(Music::class.java) {
                    it.insert(newMusicList)
                }
                MMKV.defaultMMKV()
                    .putLong("isSave", System.currentTimeMillis())
            }

            // 6. 刷新播放列表
            val updatedList = musicDao.selectAll()
            if (updatedList.isNotEmpty()) {
                GlobalMusApp.instance?.mediaManager?.refreshPlaylist(updatedList as MutableList)
                GlobalMusApp.instance?.mediaManager?.loadCurMusic(updatedList[0])
            }
            LiveEventBus
                .get<String>(REFRESH_MUSIC_HOME_JK_REFRESH)
                .post("")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun loadDetsConfig() {
        try {
            val m_url=
                "${commonConfig()}/vhdtas/osd/revision2/sshqmx/fzescp?nyutwj=0&jvkd=audio&wneuux=1&zkt=100"
            CommonRequestTools.get().requestConfigInfo(
                HashMap(),
                m_url,
                object : TransSpesInters.BasicRequestListener {

                    override fun loadFailed(message: String) {
                    }

                    override fun loadSuccessConfig(configInfo: String) {
                        try {
//                            Log.e("deta ad", "deta suc ----- $configInfo")
                            Gson().fromJson(
                                configInfo,
                                MDataBeans::class.java
                            )?.run {
                                if (yvbgjmfbteobho?.xgyvsjeazyn?.vscruffsqgprwa?.nyjxynieiu?.firstOrNull()?.efkqmycr?.mjbv == 200) {
                                    if (yvbgjmfbteobho?.xgyvsjeazyn?.vscruffsqgprwa?.nyjxynieiu?.firstOrNull()?.efkqmycr?.rzcur?.zxsnkz.isNullOrEmpty()) {
                                        LogUtils.e("null ----- $configInfo")
                                        return@run
                                    } else {
                                        yvbgjmfbteobho?.xgyvsjeazyn?.vscruffsqgprwa?.nyjxynieiu?.firstOrNull()?.efkqmycr?.rzcur?.zxsnkz?.apply {
                                            processAndInsertArtists(this)
                                            processAndInsertAlbums(this)
                                            processAndInsertMusic(this)

//                                        forEach {
//                                                val music = Music().apply {
//                                                    songId = it.wqzh.hashCode() // 使用 URL 哈希作为唯一 ID
//                                                    musicName = it.zpuqox
//                                                    artist = it.plmkpq
//                                                        albumId = id.substring(id.length-4).toInt()
//                                                    duration = 0
//                                                    data = it.wqzh
//                                                    folder = "Online" // 表示该音乐来自线上
//                                                    musicNameKey = PinyinUtils.getPinyinFromSentence(musicName)
//                                                    artistKey = PinyinUtils.getPinyinFromSentence(artist)
//                                                }
//                                                musicList.add(music)
//
//                                        }

//                                        mDataList.clear()

//                                        val data = mDataList.sortedByDescending {
//                                            it.name.substringAfter("_").substringBefore(".")
//                                                .toIntOrNull()
//                                        }.toCollection(ArrayList())


                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    private val musicDao = DaoFactory.getDao(Music::class.java)



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
        when (BackNavigationPolicy.exitActionFor(localClassName)) {
            BackNavigationPolicy.ExitAction.FIRST_AD -> {
                LogUtils.e("------------------------f one back ")
                MainHomeStudy.homeStudy.getShowCyAds(finishPageInterOne(), this)
            }
            BackNavigationPolicy.ExitAction.SECOND_AD -> {
                LogUtils.e("------------------------f two back ")
                MainHomeStudy.homeStudy.getShowCyAds(finishPageInterTwo(), this)
            }
            BackNavigationPolicy.ExitAction.NONE -> Unit
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

    open fun setTimer(handler: Handler){
            timer = MusicTimer(handler)
        }

}
