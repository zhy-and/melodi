package com.extraordinary.weaves.tendency

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.styleListBanner
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListInter
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.search.AppSearch
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.albums.AlbumListAdapter
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.extraordinary.weaves.tendency.artist.AppArtistList
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.AlbumListViewBinding
import com.extraordinary.weaves.tendency.databinding.PlayListViewBinding
import com.extraordinary.weaves.tendency.databinding.StyleListViewBinding
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.event_two_albums_2
import com.extraordinary.weaves.tendency.dialog.event_two_artists_2
import com.extraordinary.weaves.tendency.dialog.event_two_playlists_2
import com.extraordinary.weaves.tendency.dialog.event_two_search_2
import com.extraordinary.weaves.tendency.dialog.event_two_style_album2
import com.extraordinary.weaves.tendency.dialog.event_two_style_click
import com.extraordinary.weaves.tendency.dialog.event_two_styles_2
import com.extraordinary.weaves.tendency.dialog.event_two_track_2
import com.extraordinary.weaves.tendency.dialog.event_two_track_style2
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ToastUtils


class AppStyleList : baseActivity(){
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            styleListBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("styleListBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("styleListBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(styleListBanner())) {
                LogUtils.e("styleListBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(styleListBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("styleListBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("styleListBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(styleListBanner())){
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

    override fun onDestroy() {
        super.onDestroy()
        binding?.music?.cancelAnimation()
        handler?.removeCallbacksAndMessages(null)
    }
    override fun onPause() {
        super.onPause()
        if (handler?.hasMessages(MESSAGE_MSG_CODE) == true){
            handler?.removeMessages(MESSAGE_MSG_CODE)
        }
    }



    private lateinit var  binding : StyleListViewBinding
    fun showPlay(flag: Boolean) {
        if (flag) {
            binding.music.pauseAnimation()
            binding.btnHomeBottomPlay.visibility = View.VISIBLE
            binding.btnHomeBottomPause.visibility = View.GONE
        } else {
            binding.music.resumeAnimation()
            binding.btnHomeBottomPlay.visibility = View.GONE
            binding.btnHomeBottomPause.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            showBanners()
            refreshName()
            if (mediaManager.playState == AppConfig.MPS_PLAYING){
                showPlay(false)

            }else{
                if (musicDao.selectAll().isEmpty()){
                    binding.tvHomeBottomMusicName.text = getString(R.string.app_name)
                }
                showPlay(true)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private fun refreshName() {
        mediaManager.curMusic?.apply {
            binding.tvHomeBottomMusicName.text = musicName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(styleListInter(),this)

        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()

        binding = StyleListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        binding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Styles"
            searchBtn.visibility = View.VISIBLE
            searchBtn.setOnClickListener {
                startInterActivity(Intent(this@AppStyleList, AppSearch::class.java), searchInter())
//                finish()
                logMusicEvents(event_two_search_2)
            }
        }
        binding.ranks.setOnClickListener {
            startInterActivity(Intent(this, TrackList::class.java),trackListInter())
            logMusicEvents(event_two_track_style2)

//            finish()
        }
        binding.pops.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",1)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

        }


        binding.rocks.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",2)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

        }
        binding.countrys.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",3)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

        }
        binding.jazzs.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",4)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

        }
        binding.classic.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",5)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

        }
        binding.elec.setOnClickListener {
            startInterActivity(Intent(this@AppStyleList, AppGeDanDetail::class.java).apply {
                putExtra("style",6)
            }, geDanDetInter())
//            finish()
            logMusicEvents(event_two_style_click)

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
                        refreshName()
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            //todo
        }else{
            mediaManager = GlobalMusApp.instance!!.mediaManager!!

            initRecyleList()
        }
        checkEmpty()
        binding.rlTrack.setOnClickListener {
            startInterActivity(Intent(this, TrackList::class.java),trackListInter())
//            finish()
            logMusicEvents(event_two_track_2)

        }
        binding.rlPlayList.setOnClickListener {
            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter())
//            finish()
            logMusicEvents(event_two_playlists_2)

        }
        binding.rlAlbums.setOnClickListener {
            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter())
//            finish()
            logMusicEvents(event_two_albums_2)

        }
        binding.rlStyles.setOnClickListener {
            startInterActivity(Intent(this, AppArtistList::class.java), artistListInter())
//            finish()
            logMusicEvents(event_two_artists_2)

        }
        binding.btnHomeBottomPlay.setOnClickListener {
            showPlay(false)
            mediaManager?.replay()
            logMusicEvents(event_one_play_bar)

        }
        binding.btnHomeBottomPause.setOnClickListener {
            showPlay(true)
            mediaManager?.pause()
            logMusicEvents(event_one_play_bar)

        }
        binding.btnHomeBottomMenu.setOnClickListener {
            startInterActivity(Intent(this, AppPlayerList::class.java), queueInter())
//            finish()
            logMusicEvents(event_one_play_bar)

        }
        binding.bottomMusic.setOnClickListener {
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
//            finish()
            logMusicEvents(event_one_play_bar)

        }


    }

    private fun checkEmpty() {
        if (albumList.isEmpty()) {
//            binding.noContentPic.visibility = View.VISIBLE
        } else {
//            binding.noContentPic.visibility = View.GONE
        }
    }

    private  var albumList: ArrayList<Album> = arrayListOf()

    private fun initRecyleList() {
        try {

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private  fun checkPermission() {
        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
            .request(object :OnPermissionCallback{
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


    private val musicDao = DaoFactory.getDao(Music::class.java)
    private val art = DaoFactory.getDao(Artist::class.java)
    private val album = DaoFactory.getDao(Album::class.java)

    private lateinit var mediaManager: MediaManager



}