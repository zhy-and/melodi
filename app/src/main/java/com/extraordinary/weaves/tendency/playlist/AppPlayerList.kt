package com.extraordinary.weaves.tendency.playlist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.AppPlayListBinding
import com.extraordinary.weaves.tendency.performScanMusic
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.lyricInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.queueListBanner
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.google.android.gms.ads.nativead.NativeAd
import dora.util.ToastUtils

class AppPlayerList : baseActivity(), TrackListAdapter.RefreshPlayView {
    private  var musicArrayList: ArrayList<Music> = arrayListOf()
    private lateinit var mediaManager: MediaManager
    private  lateinit var playListBinding: AppPlayListBinding
    private  var playAdapter: TrackListAdapter?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(queueInter(),this)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()
        playListBinding = AppPlayListBinding.inflate(layoutInflater)
        setContentView(playListBinding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        playListBinding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Queue"
        }

        playListBinding.btnHomeBottomPlay.setOnClickListener {
            showPlay(false)
            mediaManager?.replay()
            logMusicEvents(event_one_play_bar)

        }
        playListBinding.btnHomeBottomPause.setOnClickListener {
            showPlay(true)
            mediaManager?.pause()
            logMusicEvents(event_one_play_bar)

        }
        playListBinding.btnHomeBottomMenu.setOnClickListener {
            startInterActivity(Intent(this, AppPlayerList::class.java), queueInter())
//            finish()
            logMusicEvents(event_one_play_bar)

        }
        playListBinding.bottomMusic.setOnClickListener {
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
//            finish()
            logMusicEvents(event_one_play_bar)

        }
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            checkPermission()
            LiveEventBus
                .get(AppEvent.REFRESH_NAME, String::class.java)
                .observe(this) {
                    if (isFinishing || isDestroyed){
                        return@observe
                    }
                    try {
                        mediaManager.curMusic?.apply {
                            playListBinding.tvHomeBottomMusicName.text = musicName
                        }
                        initRecyleList()
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }
        }else{
            initRecyleList()
        }
      checkEmpty()
    }

    private  fun checkPermission() {
        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (allGranted) {
                        MMKV.defaultMMKV().putBoolean("is_first_auth",true)

                        performScanMusic()
                        LogUtils.e("-----pl list")

                    }
                }
                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        ToastUtils.showLong(R.string.permission_never_denyd)
                    } else {
                        ToastUtils.showLong(R.string.permission_one_denyd)
                    }
                }
            })
    }
    private fun checkEmpty() {
        if (musicArrayList.isEmpty()){
            playListBinding.recycleHislist.visibility = View.GONE
            playListBinding.noContentPic.visibility = View.VISIBLE
        }else{
            playListBinding.recycleHislist.visibility = View.VISIBLE
            playListBinding.noContentPic.visibility = View.GONE
        }
    }
    
    fun showPlay(flag: Boolean) {
        if (flag) {
            playListBinding.music.pauseAnimation()
            playListBinding.btnHomeBottomPlay.visibility = View.VISIBLE
            playListBinding.btnHomeBottomPause.visibility = View.GONE
        } else {
            playListBinding.music.resumeAnimation()
            playListBinding.btnHomeBottomPlay.visibility = View.GONE
            playListBinding.btnHomeBottomPause.visibility = View.VISIBLE
        }
    }
    private val musicDao = DaoFactory.getDao(Music::class.java)

    override fun onResume() {
        super.onResume()
        try {
            showBanners()
            mediaManager.curMusic?.apply {
                playListBinding.tvHomeBottomMusicName.text = musicName
            }
            if (mediaManager.playState == AppConfig.MPS_PLAYING){
                showPlay(false)
            }else{
                if (musicDao.selectAll().isEmpty()){
                    playListBinding.tvHomeBottomMusicName.text = getString(R.string.app_name)
                }
                showPlay(true)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private fun initRecyleList() {
        try {
            musicArrayList.clear()
            mediaManager.playlist?.forEach {
                musicArrayList.add(it)
            }

            playAdapter = TrackListAdapter(musicArrayList,4,this@AppPlayerList,this)
            playListBinding.recycleHislist.run {
                layoutManager = LinearLayoutManager(this@AppPlayerList)
                adapter = playAdapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            queueListBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("queueListBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("queueListBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(queueListBanner())) {
                LogUtils.e("queueListBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(queueListBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("queueListBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("queueListBanner  ban show  suc------")
            playListBinding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(queueListBanner())){
                    val cts = MMKV.defaultMMKV().getInt(MUSIC_CONSTANT_APP_REFRESH, 0)

                    if (cts != 0){
                        handler.removeMessages(MESSAGE_MSG_CODE)
                        handler.sendEmptyMessageDelayed(MESSAGE_MSG_CODE,(cts * 1000).toLong())
                    }
                }
            }
        }
    }



    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                MESSAGE_MSG_CODE -> {
                    showBanners()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (handler?.hasMessages(MESSAGE_MSG_CODE) == true){
            handler?.removeMessages(MESSAGE_MSG_CODE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        playListBinding?.music?.cancelAnimation()
    }
    override fun refresh() {
        if (isFinishing || isDestroyed){
            return
        }
        try {
            mediaManager.curMusic?.apply {
                playListBinding.tvHomeBottomMusicName.text = musicName
            }
            if (mediaManager.playState == AppConfig.MPS_PLAYING){
                showPlay(false)
            }else{
                if (musicDao.selectAll().isEmpty()){
                    playListBinding.tvHomeBottomMusicName.text = getString(R.string.app_name)
                }
                showPlay(true)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}