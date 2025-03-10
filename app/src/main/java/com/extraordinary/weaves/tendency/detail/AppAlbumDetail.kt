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
import com.extraordinary.interstings.studios.utils.albumDetBanner
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.AlbumDetViewBinding
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
import dora.util.ToastUtils


class AppAlbumDetail : baseActivity(){

    private lateinit var  binding : AlbumDetViewBinding
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
            mediaManager?.curMusic?.apply {
                binding.tvHomeBottomMusicName.text = musicName
            }
            if (mediaManager?.playState == AppConfig.MPS_PLAYING){
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

    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            albumDetBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("albumDetBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("albumDetBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(albumDetBanner())) {
                LogUtils.e("albumDetBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(albumDetBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("albumDetBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("albumDetBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(albumDetBanner())){
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(albumDetInter(),this)
        mediaManager = GlobalMusApp?.instance?.mediaManager
        binding = AlbumDetViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .fullScreen(false)
            .titleBar(binding.title)
            .init()
        var albumId = intent?.getIntExtra("album_id",-1)
        LogUtils.e("intent ---- "+albumId)
        albumId?.let {
            try {
                if (albumId != -1){
                    var list =   musicDao.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("album_id", it)
                        ))
                    musicArrayList.clear()
                    musicArrayList.addAll(list)

                    var album =  album.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("album_id", it)
                        ))
                    if (album.isNotEmpty()){
                        binding.albumName.text = album[0].album_name
                    }
                }else{
                    binding.albumName.text = getString(R.string.app_name)
                }

            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        binding.backBtn.setOnClickListener {
            speFinish()
        }
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            checkPermission()
//            LiveEventBus
//                .get(ApolloEvent.REFRESH_NAME, String::class.java)
//                .observe(this) {
//                    if (isFinishing || isDestroyed){
//                        return@observe
//                    }
//                    startActivity(Intent(this,AppAlbumList::class.java))
//                    finish()
//                }
        }else{
            initRecyleList()
        }
        if (musicArrayList.isEmpty()){
            binding.recycleHislist.visibility = View.GONE
            binding.noContentPic.visibility = View.VISIBLE
        }
        binding.btnHomeBottomPlay.setOnClickListener {
            showPlay(false)
            mediaManager?.replay()
        }
        binding.btnHomeBottomPause.setOnClickListener {
            showPlay(true)
            mediaManager?.pause()
        }
        binding.btnHomeBottomMenu.setOnClickListener {
            startInterActivity(Intent(this, AppPlayerList::class.java), queueInter())

        }
        binding.bottomMusic.setOnClickListener {
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())

        }

        binding.playBtn.setOnClickListener {
            try {
                GlobalMusApp.instance?.mediaManager?.also {
                    if (musicArrayList.isNotEmpty()){
                        it.refreshPlaylist(musicArrayList)
                        it.playById(musicArrayList[0].songId)
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
        }

    }
    private  var musicArrayList: ArrayList<Music> = arrayListOf()

    private fun initRecyleList() {
        listAdapter = TrackListAdapter(musicArrayList,2,this@AppAlbumDetail)
        binding.recycleHislist.run {
            layoutManager = LinearLayoutManager(this@AppAlbumDetail)
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
    private val art = DaoFactory.getDao(Artist::class.java)
    private val album = DaoFactory.getDao(Album::class.java)

    private  var mediaManager: MediaManager?= null

}