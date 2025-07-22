package com.extraordinary.weaves.tendency.artist

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
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistListBanner
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListInter
import com.extraordinary.weaves.tendency.AppGeDanList
import com.extraordinary.weaves.tendency.AppStyleList
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.search.AppSearch
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.ArtistListViewBinding
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.event_two_album_artist2
import com.extraordinary.weaves.tendency.dialog.event_two_albums_2
import com.extraordinary.weaves.tendency.dialog.event_two_playlist_created
import com.extraordinary.weaves.tendency.dialog.event_two_playlists_2
import com.extraordinary.weaves.tendency.dialog.event_two_search_2
import com.extraordinary.weaves.tendency.dialog.event_two_styles_2
import com.extraordinary.weaves.tendency.dialog.event_two_track_2
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.performScanMusic
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ToastUtils


class AppArtistList : baseActivity(){

    private lateinit var listAdapter: ArtListAdapter
    private lateinit var  binding : ArtistListViewBinding
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

    private  var artist: ArrayList<Artist> = arrayListOf()

    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            artistListBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("artistListBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("artistListBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(artistListBanner())) {
                LogUtils.e("artistListBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(artistListBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("artistListBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("artistListBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(artistListBanner())){
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
        binding?.music?.cancelAnimation()
    }

    private fun initRecyleList() {
        try {
            artist.clear()
            artist.addAll(art.selectAll())
            listAdapter = ArtListAdapter(artist,this@AppArtistList,5)
            binding.recycleHislist.run {
                layoutManager = LinearLayoutManager(this@AppArtistList)
                adapter = listAdapter
            }
            if (album != null && album.selectAll().isNotEmpty()){
                album.selectAll().also {
                    if (it.size > 2){
                        binding.al1Txt.text = it[0].album_name
                        binding.al1Txt.tag = it[0].album_id
                        binding.al2Txt.text = it[1].album_name
                        binding.al2Txt.tag = it[1].album_id
                        binding.al3Txt.text = it[2].album_name
                        binding.al3Txt.tag = it[2].album_id

                    }else if (it.size == 2){
                        binding.al1Txt.text = it[0].album_name
                        binding.al1Txt.tag = it[0].album_id
                        binding.al2Txt.text = it[1].album_name
                        binding.al2Txt.tag = it[1].album_id
                    }else if (it.size == 1){
                        binding.al1Txt.text = it[0].album_name
                        binding.al1Txt.tag = it[0].album_id
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        MainHomeStudy.homeStudy.getShowCyAds(artistListInter(),this)

        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()
        binding = ArtistListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!

        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
//            checkPermission()

            LiveEventBus
                .get(AppEvent.REFRESH_NAME, String::class.java)
                .observe(this) {
                    if (isFinishing || isDestroyed){
                        return@observe
                    }
                    try {
                        refreshName()
                        initRecyleList()
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
        }
        initRecyleList()
        checkEmpty()
        binding.rlTrack.setOnClickListener {
            startInterActivity(Intent(this, TrackList::class.java),trackListInter())
//            finish()
            logMusicEvents(event_two_track_2)

        }

        binding.rlAlbums.setOnClickListener {
            logMusicEvents(event_two_albums_2)

            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter())
//            finish()
        }

        binding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Artists"
            searchBtn.visibility = View.VISIBLE
            searchBtn.setOnClickListener {
                logMusicEvents(event_two_search_2)

                startInterActivity(Intent(this@AppArtistList, AppSearch::class.java), searchInter())
//                finish()
            }
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
        binding.rlAl1.setOnClickListener {
            startInterActivity(Intent(this@AppArtistList, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al1Txt.tag.toString().toInt())
            }, albumDetInter())
            logMusicEvents(event_two_album_artist2)

//            finish()
        }
        binding.rlAl2.setOnClickListener {
            startInterActivity(Intent(this@AppArtistList, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al2Txt.tag.toString().toInt())
            }, albumDetInter())
//            finish()
            logMusicEvents(event_two_album_artist2)

        }
        binding.rlAl3.setOnClickListener {
            startInterActivity(Intent(this@AppArtistList, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al3Txt.tag.toString().toInt())
            }, albumDetInter())
//            finish()
            logMusicEvents(event_two_album_artist2)

        }
        binding.rlPlayList.setOnClickListener {
            logMusicEvents(event_two_playlists_2)

            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter())
//            finish()
        }
        binding.rlStyles.setOnClickListener {
            logMusicEvents(event_two_styles_2)

            startInterActivity(Intent(this, AppStyleList::class.java), styleListInter())
//            finish()
        }


    }

    private fun checkEmpty() {
        if (artist.isEmpty()) {
            binding.recycleHislist.visibility = View.GONE
            binding.noContentPic.visibility = View.VISIBLE
        } else {
            binding.recycleHislist.visibility = View.VISIBLE
            binding.noContentPic.visibility = View.GONE
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