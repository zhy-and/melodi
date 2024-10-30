package com.extraordinary.weaves.tendency

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.geDanListBanner
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.artist.AppArtistList
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.GeDanData
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.PlayListViewBinding
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.global.DataKeeper.loadGeDanData
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.search.AppSearch
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ToastUtils


class AppGeDanList : baseActivity(){

    private lateinit var  binding : PlayListViewBinding
    private  var listAdapter: GeDanListAdapter?= null
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
            initRecyleList()
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
        MainHomeStudy.homeStudy.getShowCyAds(geDanListInter(),this)

        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()

        binding = PlayListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        binding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Playlists"
            searchBtn.visibility = View.VISIBLE
            searchBtn.setOnClickListener {
                startActivity(Intent(this@AppGeDanList, AppSearch::class.java))
                finish()
            }
        }

        binding.rlAl1.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppArtDetail::class.java).apply {
                putExtra("artist",binding.al1Txt.text.toString())
            })
        }
        binding.rlAl2.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppArtDetail::class.java).apply {
                putExtra("artist",binding.al2Txt.text.toString())
            })
        }
        binding.rlAl3.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppArtDetail::class.java).apply {
                putExtra("artist",binding.al3Txt.text.toString())
            })
        }
        binding.rlAl4.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppArtDetail::class.java).apply {
                putExtra("artist",binding.al4Txt.text.toString())
            })
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
                        initRecyleList()
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            //todo
        }else{
            mediaManager = GlobalMusApp.instance!!.mediaManager!!
        }
        checkEmpty()
        binding.rlTrack.setOnClickListener {
            startActivity(Intent(this, TrackList::class.java))
//            finish()
        }

        binding.rlArtList.setOnClickListener {
            startActivity(Intent(this, AppArtistList::class.java))
//            finish()
        }
        binding.rlAlbums.setOnClickListener {
            startActivity(Intent(this, AppAlbumList::class.java))
//            finish()
        }
        binding.rlStyles.setOnClickListener {
            startActivity(Intent(this, AppStyleList::class.java))
//            finish()
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
            startActivity(Intent(this, AppPlayerList::class.java))
//            finish()
        }
        binding.bottomMusic.setOnClickListener {
            startActivity(Intent(this, PlayerView::class.java))
//            finish()
        }

        binding.mostPlayed.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",7)
            })
        }


        binding.golden.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",8)

            })
        }
        binding.lost.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",9)

            })
        }
        binding.fav.setOnClickListener {
            //collect
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",10)

            })
        }
        binding.moon.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",11)

            })
        }
        binding.vibes.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("style",12)

            })
        }

        binding.topCreateGedan.setOnClickListener {
            startActivity(Intent(this@AppGeDanList, AppGeDanDetail::class.java).apply {
                putExtra("create",true)
            })
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
    private fun checkEmpty() {
//        if (albumList.isEmpty()) {
//            binding.recycleHislist.visibility = View.GONE
//            binding.noContentPic.visibility = View.VISIBLE
//        } else {
//            binding.recycleHislist.visibility = View.VISIBLE
//            binding.noContentPic.visibility = View.GONE
//        }
    }

    private fun initRecyleList() {
        try {
            Thread {
                //查询歌单列表
                val oldGeDanListData = loadGeDanData(GeDanData())
                ThreadUtils.runOnUiThread {
                    if (listAdapter == null){
                        listAdapter = GeDanListAdapter(oldGeDanListData,this@AppGeDanList)
                        binding.recycleHislist.run {
                            layoutManager = GridLayoutManager(this@AppGeDanList,2)
                            adapter = listAdapter
                            addItemDecoration(SpaceItemDecoration(10))
                        }
                    }else{
                        listAdapter?.upCreateData(oldGeDanListData)
                    }
                }
                if (art.selectAll().isNotEmpty()){
                    if (art.selectAll().isNotEmpty()){
                        binding.al1Txt.post {
                            binding.al1Txt.text = art.selectAll()[0].name
                        }
                    }
                    if (1 < art.selectAll().size){
                        binding.al2Txt.post {
                            binding.al2Txt.text = art.selectAll()[1].name
                        }
                    }
                    if (2 < art.selectAll().size){
                        binding.al3Txt.post {
                            binding.al3Txt.text = art.selectAll()[2].name
                        }
                    }
                    if (3 < art.selectAll().size){
                        binding.al3Txt.post {
                            binding.al3Txt.text = art.selectAll()[3].name
                        }
                    }
                }
            }.start()


        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private val musicDao = DaoFactory.getDao(Music::class.java)
    private val art = DaoFactory.getDao(Artist::class.java)

    private lateinit var mediaManager: MediaManager

    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            geDanListBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("geDanListBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("geDanListBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(geDanListBanner())) {
                LogUtils.e("geDanListBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(geDanListBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("geDanListBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("geDanListBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(geDanListBanner())){
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