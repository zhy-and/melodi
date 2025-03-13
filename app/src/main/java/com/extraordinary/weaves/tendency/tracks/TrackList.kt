package com.extraordinary.weaves.tendency.tracks

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.weaves.tendency.AppGeDanList
import com.extraordinary.weaves.tendency.AppStyleList
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.artist.AppArtistList
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.TrackListViewBinding
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail
import com.extraordinary.weaves.tendency.performScanMusic
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.search.AppSearch
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListBanner
import com.extraordinary.interstings.studios.utils.trackListInter
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.event_two_albums_2
import com.extraordinary.weaves.tendency.dialog.event_two_artists_2
import com.extraordinary.weaves.tendency.dialog.event_two_playlist_track2
import com.extraordinary.weaves.tendency.dialog.event_two_playlists_2
import com.extraordinary.weaves.tendency.dialog.event_two_privacy_policy_setting
import com.extraordinary.weaves.tendency.dialog.event_two_search_2
import com.extraordinary.weaves.tendency.dialog.event_two_styles_2
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import dora.util.ToastUtils

class TrackList : baseActivity() {
    private val musicDao = DaoFactory.getDao(Music::class.java)
    private  var musicArrayList: ArrayList<Music> = arrayListOf()
    private lateinit var mediaManager: MediaManager
    private  lateinit var trackListViewBinding: TrackListViewBinding
    private  var trackListAdapter: TrackListAdapter?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(trackListInter(),this)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()
        trackListViewBinding = TrackListViewBinding.inflate(layoutInflater)
        setContentView(trackListViewBinding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        trackListViewBinding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Tracks"
            searchBtn.visibility = View.VISIBLE
            searchBtn.setOnClickListener {
                logMusicEvents(event_two_search_2)

                startInterActivity(Intent(this@TrackList, AppSearch::class.java), searchInter())
            }
        }

        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            LiveEventBus
                .get(AppEvent.REFRESH_NAME, String::class.java)
                .observe(this) {
                    if (isFinishing || isDestroyed){
                        return@observe
                    }
                    try {
                        initRecyleList()
                        mediaManager.curMusic?.apply {
                            trackListViewBinding.tvHomeBottomMusicName.text = musicName
                        }
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }
            initPermission()
        }else{
            initRecyleList()
        }
        trackListViewBinding.rlTrack.setOnClickListener {
            startInterActivity(Intent(this, TrackList::class.java),trackListInter())
        }
        trackListViewBinding.rlAlbums.setOnClickListener {
            logMusicEvents(event_two_albums_2)

            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter())
        }
        trackListViewBinding.rlArtist.setOnClickListener {
            logMusicEvents(event_two_artists_2)

            startInterActivity(Intent(this, AppArtistList::class.java), artistListInter())
        }
        trackListViewBinding.rlPlayList.setOnClickListener {
            logMusicEvents(event_two_playlists_2)

            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter())
        }
        trackListViewBinding.rlStyles.setOnClickListener {
            logMusicEvents(event_two_styles_2)

            startInterActivity(Intent(this, AppStyleList::class.java), styleListInter())
        }
        trackListViewBinding.scan.setOnClickListener {
            initPermission()
        }
        trackListViewBinding.btnHomeBottomPlay.setOnClickListener {
            showPlay(false)
            mediaManager?.replay()
            logMusicEvents(event_one_play_bar)

        }
        trackListViewBinding.btnHomeBottomPause.setOnClickListener {
            showPlay(true)
            mediaManager?.pause()
            logMusicEvents(event_one_play_bar)

        }
        trackListViewBinding.btnHomeBottomMenu.setOnClickListener {
            startInterActivity(Intent(this, AppPlayerList::class.java), queueInter())
            logMusicEvents(event_one_play_bar)

        }
        trackListViewBinding.bottomMusic.setOnClickListener {
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
            logMusicEvents(event_one_play_bar)

        }
        trackListViewBinding.albumsOne.setOnClickListener {
            startInterActivity(Intent(this@TrackList, AppGeDanDetail::class.java).apply {
                putExtra("style",9)
            }, geDanDetInter())
            logMusicEvents(event_two_playlist_track2)

        }


        trackListViewBinding.albumsTw.setOnClickListener {
            startInterActivity(Intent(this@TrackList, AppGeDanDetail::class.java).apply {
                putExtra("style",8)
            }, geDanDetInter())
            logMusicEvents(event_two_playlist_track2)

        }
        trackListViewBinding.albumsThr.setOnClickListener {
            startInterActivity(Intent(this@TrackList, AppGeDanDetail::class.java).apply {
                putExtra("style",11)
            }, geDanDetInter())
            logMusicEvents(event_two_playlist_track2)

        }
        checkEmpty()
    }

    private fun checkEmpty(){
        if (musicArrayList.isEmpty()){
            trackListViewBinding.recycleHislist.visibility = View.GONE
            trackListViewBinding.noContentPic.visibility = View.VISIBLE
        }else{
            trackListViewBinding.recycleHislist.visibility = View.VISIBLE
            trackListViewBinding.noContentPic.visibility = View.GONE
        }
    }

    fun showPlay(flag: Boolean) {
        if (flag) {
            trackListViewBinding.music.pauseAnimation()
            trackListViewBinding.btnHomeBottomPlay.visibility = View.VISIBLE
            trackListViewBinding.btnHomeBottomPause.visibility = View.GONE
        } else {
            trackListViewBinding.music.resumeAnimation()
            trackListViewBinding.btnHomeBottomPlay.visibility = View.GONE
            trackListViewBinding.btnHomeBottomPause.visibility = View.VISIBLE
        }
    }
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            trackListBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("trackListBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("trackListBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(trackListBanner())) {
                LogUtils.e("trackListBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(trackListBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("trackListBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("trackListBanner  ban show  suc------")
            trackListViewBinding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(trackListBanner())){
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
        trackListViewBinding?.music?.cancelAnimation()
    }
    override fun onResume() {
        super.onResume()
        try {
            showBanners()
            mediaManager.curMusic?.apply {
                trackListViewBinding.tvHomeBottomMusicName.text = musicName
            }
            if (mediaManager.playState == AppConfig.MPS_PLAYING){
                showPlay(false)

            }else{
                if (musicDao.selectAll().isEmpty()){
                    trackListViewBinding.tvHomeBottomMusicName.text = getString(R.string.app_name)
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
            if (musicDao.selectAll().isNotEmpty()){
                musicDao.selectAll().forEach {
                    musicArrayList.add(it)
                }
                trackListAdapter = TrackListAdapter(musicArrayList, 1, this@TrackList)
                trackListViewBinding.recycleHislist.run {
                    layoutManager = LinearLayoutManager(this@TrackList)
                    adapter = trackListAdapter
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }



    private fun initPermission() {
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (allGranted) {
                            MMKV.defaultMMKV().putBoolean("is_first_auth",true)
                            performScanMusic()


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
    }

}