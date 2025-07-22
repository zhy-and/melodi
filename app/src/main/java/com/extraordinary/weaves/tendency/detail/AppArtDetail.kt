package com.extraordinary.weaves.tendency.detail

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
import com.extraordinary.interstings.studios.utils.artistDetBanner
import com.extraordinary.interstings.studios.utils.artistDetInter
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.ArtDetViewBinding
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.event_th_album_play
import com.extraordinary.weaves.tendency.dialog.event_th_artist_play
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.performScanMusic
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import dora.db.builder.QueryBuilder
import dora.db.builder.WhereBuilder
import dora.db.dao.DaoFactory
import dora.util.TextUtils
import dora.util.ToastUtils


class AppArtDetail : baseActivity(){

    private lateinit var  binding : ArtDetViewBinding
    private  var listAdapter: TrackListAdapter?= null
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
            if (mediaManager.playState == AppConfig.MPS_PLAYING){
                showPlay(false)

            }else{
                if (musicDao.selectAll().isEmpty()){
                    binding.tvHomeBottomMusicName.text = getString(R.string.app_name)
                }
                showPlay(true)
            }
            mediaManager.curMusic?.apply {
                binding.tvHomeBottomMusicName.text = musicName
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(artistDetInter(),this)
        binding = ArtDetViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .fullScreen(false)
            .titleBar(binding.title)
            .init()

        val artist = intent?.getStringExtra("artist")

        artist?.let {
            try {
                if (artist != "-1" && !TextUtils.isEmpty(artist)){
                    musicArrayList.clear()
                    musicArrayList.addAll( musicDao.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("artist", it)
                        )
                    ))
                    binding.albumName.text = it
                }else{
                    binding.albumName.text = getString(R.string.app_name)
                }

            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        binding.backBtn.setOnClickListener {
            speFinish()
        }
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
//            checkPermission()
//            LiveEventBus
//                .get(ApolloEvent.REFRESH_NAME, String::class.java)
//                .observe(this) {
//                    if (isFinishing || isDestroyed){
//                        return@observe
//                    }
//                    checkEmpty()
//                    startActivity(Intent(this,AppArtistList::class.java))
//                    finish()
//                }
        }
//        else{
            initRecyleList()
//        }
        checkEmpty()
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
            logMusicEvents(event_one_play_bar)

        }
        binding.bottomMusic.setOnClickListener {
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
            logMusicEvents(event_one_play_bar)

        }

        binding.playBtn.setOnClickListener {
            try {
                GlobalMusApp.instance?.mediaManager?.also {
                    if (musicArrayList.isNotEmpty()){
                        it.refreshPlaylist(musicArrayList)
                        it.playById(musicArrayList[0].songId)
                    }
                }
                logMusicEvents(event_th_artist_play)

            }catch (e : Exception){
                startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())

                e.printStackTrace()
            }

            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
        }

    }

    private fun checkEmpty() {
        if (musicArrayList.isEmpty()) {
            binding.recycleHislist.visibility = View.GONE
            binding.noContentPic.visibility = View.VISIBLE
        }
    }

    private  var musicArrayList: ArrayList<Music> = arrayListOf()

    private fun initRecyleList() {

        listAdapter = TrackListAdapter(musicArrayList,6,this@AppArtDetail)
        binding.recycleHislist.run {
            layoutManager = LinearLayoutManager(this@AppArtDetail)
            adapter = listAdapter
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


    private lateinit var mediaManager: MediaManager
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            artistDetBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("artistDetBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("artistDetBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(artistDetBanner())) {
                LogUtils.e("artistDetBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(artistDetBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("artistDetBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("artistDetBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(artistDetBanner())){
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


}