package com.extraordinary.weaves.tendency

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.extraordinary.interstings.studios.beans.SpecialModels
import com.extraordinary.interstings.studios.beans.bannerEvent
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.ApiStudios.Companion.referInfoLoads
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_IS_AUTO_PLAY
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.homeBanner
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.artist.AppArtistList
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.IMediaService
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.core.MusicControl
import com.extraordinary.weaves.tendency.core.MusicScanner
import com.extraordinary.weaves.tendency.databinding.HomeMainBinding
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.scan.ScanPage
import com.extraordinary.weaves.tendency.search.AppSearch
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.extraordinary.weaves.tendency.view.AppMoreView
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ThreadUtils
import dora.util.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Executors


class HomeActivity : baseActivity(), MusicControl.OnConnectCompletionListener {
    private  var playlist: MutableList<Music> ?= null
    private  var favAdapter: FavAdapter ?= null
//    private var mPanHandle : Handler = CommonMessageHandler(this)
    var string = arrayListOf(R.mipmap.banner_one,R.mipmap.banner_tw,R.mipmap.banner_th,
        R.mipmap.banner_fo, R.mipmap.banner_fv)
    private lateinit var  binding : HomeMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.trans)
            .init()
        binding = HomeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        mediaManager = GlobalMusApp.instance?.mediaManager
        mediaManager?.connectService()
        mediaManager?.setOnCompletionListener(this)
        initPermission()
//        timer = MusicTimer(mPanHandle)
        binding.bans.apply {
            setAlphaItem(true)
            setLoop()
            adapter = RecyHomeAdapter(string,this@HomeActivity)
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(this, AppMoreView::class.java))
        }
        binding.rlTrack.setOnClickListener {
            startActivity(Intent(this, TrackList::class.java))
        }

        binding.rlAlbums.setOnClickListener {
            startActivity(Intent(this, AppAlbumList::class.java))
        }
        binding.rlArtist.setOnClickListener {
            startActivity(Intent(this, AppArtistList::class.java))
        }
        binding.settings.setOnClickListener {
            startActivity(Intent(this, AppMoreView::class.java))
        }
        binding.search.setOnClickListener {
            startActivity(Intent(this, AppSearch::class.java))
        }
        binding.artistTitle.setOnClickListener {
            startActivity(Intent(this, AppArtistList::class.java))
        }
        binding.stylesTitle.setOnClickListener {
            startActivity(Intent(this, AppStyleList::class.java))
        }
        binding.playlistTitle.setOnClickListener {
            startActivity(Intent(this, AppGeDanList::class.java))
        }
        binding.btnHomeBottomPlay.setOnClickListener {
            if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
                startActivity(Intent(this,PlayerView::class.java))
                return@setOnClickListener
            }
            showPlay(false)
            mediaManager?.replay()
        }
        binding.btnHomeBottomPause.setOnClickListener {
            showPlay(true)
            mediaManager?.pause()
        }
        binding.btnHomeBottomMenu.setOnClickListener {
            startActivity(Intent(this, AppPlayerList::class.java))
        }
        binding.bottomMusic.setOnClickListener {
            startActivity(Intent(this, PlayerView::class.java))
        }
        binding.playMode.setOnClickListener {
            startActivity(Intent(this, PlayerView::class.java).apply {
                putExtra("play",true)
            })
        }
        binding.playSong.setOnClickListener {
            startActivity(Intent(this, PlayerView::class.java).apply {
                putExtra("play",true)
            })
        }
        binding.scan.setOnClickListener {
            startActivity(Intent(this, ScanPage::class.java))

        }
        binding.feed.setOnClickListener {
            startActivity(Intent(this,FeedView::class.java))

        }
        binding.albumsTitle.setOnClickListener {
            startActivity(Intent(this, AppAlbumList::class.java))
        }

        binding.rlStyles.setOnClickListener {
            startActivity(Intent(this, AppStyleList::class.java))
        }
        binding.rlPlayList.setOnClickListener {
            startActivity(Intent(this, AppGeDanList::class.java))
        }

        binding.albumsTitle.setOnClickListener {
            startActivity(Intent(this, AppAlbumList::class.java))
        }

        binding.mostPlayedTitle.setOnClickListener {
            startActivity(Intent(this, TrackList::class.java))
        }
        binding.albumsOne.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al1Txt.tag.toString().toInt())
            })
        }
        binding.albumsTw.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al2Txt.tag.toString().toInt())
            })
        }
        binding.albumsThr.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al3Txt.tag.toString().toInt())
            })
        }
        binding.albumsFor.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al4Txt.tag.toString().toInt())
            })
        }
       binding.artistOne.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art1Txt.text.toString())
            })
        }
        binding.artistTw.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art2Txt.text.toString())
            })
        }
        binding.artistTh.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art3Txt.text.toString())
            })
        }
        binding.pops.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",1)
            })
        }


        binding.rocks.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",2)

            })
        }
        binding.countrys.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",3)

            })
        }
        binding.jazzs.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",4)

            })
        }
        binding.artistFor.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art4Txt.text.toString())
            })
        }
         binding.playOne.setOnClickListener {
             clickPlay(0)
        }
        binding.playTw.setOnClickListener {
            clickPlay(1)
        }
        binding.playThr.setOnClickListener {
            clickPlay(2)
        }
        binding.playFor.setOnClickListener {
            clickPlay(3)
        }
        binding.playFive.setOnClickListener {
            clickPlay(4)
        }
        binding.playSix.setOnClickListener {
            clickPlay(5)
        }


        binding.listOne.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",8)
            })
        }


        binding.listTw.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",9)

            })
        }
        binding.listThr.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",11)

            })
        }
        binding.listFor.setOnClickListener {
            //collect
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",12)

            })
        }
        binding.listFv.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",7)

            })
        }
        binding.listSix.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",10)

            })
        }
        initPic()
    }

    private fun initPic() {
//        GlobalMusApp.instance?.let {
//            Glide.with(it).load().into(binding.)
//        }
    }

    private val musicDao = DaoFactory.getDao(Music::class.java)

    private fun clickPlay(postion: Int) {
        musicDao.selectAll()?.also {
                if (it.isNotEmpty() && postion < it.size){
                    mediaManager?.playById(it[postion].songId)
                }
        }
        startActivity(Intent(this@HomeActivity, PlayerView::class.java))
    }
    var ms1 = Music().apply {
        musicName = "Back To Her Men"
        artist = "Demien Rice"
        favorite = 0
        coverPath = R.mipmap.recent_one.toString()
    }
    var ms2 = Music().apply {
        musicName = "Back To Her Men"
        artist = "Demien Rice"
        favorite = 0
        coverPath = R.mipmap.recent_tw.toString()
    }
    var ms3 = Music().apply {
        musicName = "Back To Her Men"
        artist = "Demien Rice"
        favorite = 0
        coverPath = R.mipmap.recent_th.toString()
    }
    var ms4 = Music().apply {
        musicName = "Back To Her Men"
        artist = "Demien Rice"
        favorite = 0
        coverPath = R.mipmap.recent_fo.toString()
    }
    var ms5 = Music().apply {
        musicName = "Back To Her Men"
        artist = "Demien Rice"
        favorite = 0
        coverPath = R.mipmap.recent_fv.toString()
    }
    val favlist = arrayListOf<Music>()

    private fun initFavList() {
        favlist.clear()
        val musicList = musicDao.selectAll()
        if (musicList.size > 4){
            musicList.take(5).forEach {
                favlist.add(it)
            }
        }else{
            if (musicList.isEmpty()){
                favlist.add(ms1)
                favlist.add(ms2)
                favlist.add(ms3)
                favlist.add(ms4)
                favlist.add(ms5)
            }else if (musicList.size == 1){
                favlist.add(musicList[0])
                favlist.add(ms2)
                favlist.add(ms3)
                favlist.add(ms4)
                favlist.add(ms5)
            }
            else if (musicList.size == 2){
                favlist.add(musicList[0])
                favlist.add(musicList[1])
                favlist.add(ms3)
                favlist.add(ms4)
                favlist.add(ms5)
            }
            else if (musicList.size == 3){
                favlist.add(musicList[0])
                favlist.add(musicList[1])
                favlist.add(musicList[2])
                favlist.add(ms4)
                favlist.add(ms5)
            }
            else if (musicList.size == 4){
                favlist.add(musicList[0])
                favlist.add(musicList[1])
                favlist.add(musicList[2])
                favlist.add(musicList[3])
                favlist.add(ms5)
            }

        }
            favAdapter = FavAdapter(favlist,this)
            binding.recycleFav.run {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = favAdapter
        }
     favAdapter?.notifyDataSetChanged()
    }
    private fun initPermission() {
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
                .request(object :OnPermissionCallback{
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (allGranted) {
                            MMKV.defaultMMKV().putBoolean("is_first_auth",true)
                            scanMusic1()
                        }
                    }
                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            ToastUtils.showLong(R.string.permission_never_denyd)
//                        toast("被永久拒绝授权，请手动授予录音和日历权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(this@HomeActivity, permissions)
                        } else {
                            ToastUtils.showLong(R.string.permission_one_denyd)
//                        toast("获取录音和日历权限失败")
                        }
                    }
                })
        }
    }
    private var isLoaded = false
    override fun onDestroy() {
        super.onDestroy()
        isLoaded = false
            if (EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().unregister(this)
            }
            EventBus.getDefault().removeAllStickyEvents()
        messages?.removeCallbacksAndMessages(null)
        }
    override fun onResume() {
        super.onResume()
        try {
            referInfoLoads(this,true)
            initHomeBan()
            updateCollectItem()
            if (mediaManager?.playState == AppConfig.MPS_PLAYING){
                showPlay(false)
            }else{
                if (musicDao.selectAll().isEmpty()){
                    binding.tvHomeBottomMusicName.text = getString(R.string.app_name)
                }
                showPlay(true)
            }
            mediaManager?.curMusic?.apply {
                binding.tvHomeBottomMusicName.text = musicName
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }


    private fun updateAlbumItem() {
        if (album != null && album.selectAll().isNotEmpty()){
            album.selectAll().also {
                if (it.size > 3){
                    binding.al1Txt.text = it[0].album_name
                    binding.al1Txt.tag = it[0].album_id
                    binding.al2Txt.text = it[1].album_name
                    binding.al2Txt.tag = it[1].album_id
                    binding.al3Txt.text = it[2].album_name
                    binding.al3Txt.tag = it[2].album_id
                    binding.al4Txt.text = it[3].album_name
                    binding.al4Txt.tag = it[3].album_id

                }else if (it.size == 3){
//                    binding.al1Txt.text = it[0].album_name
                    binding.al1Txt.tag = it[0].album_id
//                    binding.al2Txt.text = it[1].album_name
                    binding.al2Txt.tag = it[1].album_id
//                    binding.al3Txt.text = it[2].album_name
                    binding.al3Txt.tag = it[2].album_id

                }else if (it.size == 2){
//                    binding.al1Txt.text = it[0].album_name
                    binding.al1Txt.tag = it[0].album_id
//                    binding.al2Txt.text = it[1].album_name
                    binding.al2Txt.tag = it[1].album_id

                }else if (it.size == 1){
//                    binding.al1Txt.text = it[0].album_name
                    binding.al1Txt.tag = it[0].album_id
                }
            }
        }

        if (art != null && art.selectAll().isNotEmpty()){
            art.selectAll().also {
                if (it.size <=3){
                    binding.art1Txt.visibility = View.GONE
                    binding.art2Txt.visibility = View.GONE
                    binding.art3Txt.visibility = View.GONE
                    binding.art4Txt.visibility = View.GONE
                }else{
                    binding.art1Txt.visibility = View.VISIBLE
                    binding.art2Txt.visibility = View.VISIBLE
                    binding.art3Txt.visibility = View.VISIBLE
                    binding.art4Txt.visibility = View.VISIBLE
                }
                if (it.size > 3){
                    binding.art1Txt.text = it[0].name
                    binding.art2Txt.text = it[1].name
                    binding.art3Txt.text = it[2].name
                    binding.art4Txt.text = it[3].name
                }else if (it.size == 3){
                    binding.art1Txt.text = it[0].name
                    binding.art2Txt.text = it[1].name
                    binding.art3Txt.text = it[2].name
                }else if (it.size == 2){
                    binding.art1Txt.text = it[0].name
                    binding.art2Txt.text = it[1].name
                }else if (it.size == 1){
                    binding.art1Txt.text = it[0].name
                }
            }
        }
        musicDao.selectAll().also {
            if (it.isNotEmpty()){
                    if (it.size > 5){
                        binding.recentOne.text = it[0].musicName
                        binding.recentTw.text = it[1].musicName
                        binding.recentTr.text = it[2].musicName
                        binding.recentFr.text = it[3].musicName
                        binding.recentFv.text = it[4].musicName
                        binding.recentSx.text = it[5].musicName
                    }
                    else if (it.size == 5){
                        binding.recentOne.text = it[0].musicName
                        binding.recentTw.text = it[1].musicName
                        binding.recentTr.text = it[2].musicName
                        binding.recentFr.text = it[3].musicName
                        binding.recentFv.text = it[4].musicName
                    }else if (it.size == 4){
                        binding.recentOne.text = it[0].musicName
                        binding.recentTw.text = it[1].musicName
                        binding.recentTr.text = it[2].musicName
                        binding.recentFr.text = it[3].musicName
                    }
                    else if (it.size == 3){
                        binding.recentOne.text = it[0].musicName
                        binding.recentTw.text = it[1].musicName
                        binding.recentTr.text = it[2].musicName
                    }else if (it.size == 2){
                        binding.recentOne.text = it[0].musicName
                        binding.recentTw.text = it[1].musicName
                    }else if (it.size == 1){
                        binding.recentOne.text = it[0].musicName
                    }
            }
        }

    }

    private fun updateCollectItem() {
        if (musicDao != null && musicDao.selectAll().isNotEmpty()){
            if (binding.mostPlayedTitle.visibility == View.GONE){
                binding.mostPlayedTitle.visibility = View.VISIBLE
            }
            if ( binding.horllPlayed.visibility == View.GONE){
                binding.horllPlayed.visibility = View.VISIBLE
            }
            if ( binding.forTitle.visibility == View.GONE){
                binding.forTitle.visibility = View.VISIBLE
            }
            if ( binding.recycleFav.visibility == View.GONE){
                binding.recycleFav.visibility = View.VISIBLE
            }
            initFavList()
            updateAlbumItem()

        }else{
            binding.mostPlayedTitle.visibility = View.GONE
            binding.horllPlayed.visibility = View.GONE
            binding.forTitle.visibility = View.GONE
            binding.recycleFav.visibility = View.GONE

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadHomeData(reModel: bannerEvent?) {
        LogUtils.e("BannerEvs------")
        if (reModel == null) {
            return
        }
        if (isFinishing || isDestroyed) {
            return
        }
        if (reModel.bannerType && !isLoaded) {
            initHomeBan()
        }
    }
    private fun initHomeBan() {
            val mainAds: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
                homeBanner(),this)
            if (mainAds == null) {
                if (CommonStudyUtils.interWeavesIsShowing) {
                    LogUtils.e("main banner inter is showing return------")
                    return
                }
                if (isFinishing || isDestroyed) {
                    LogUtils.e("main banner destroy  return------")
                    return
                }
                if (CommonStudyUtils.posIsCanShow(homeBanner())) {
                    LogUtils.e("main banner limit  return------")
                    return
                }
                MainHomeStudy.homeStudy.initLoadBanners(homeBanner(),object :
                    NativeWeavesListener {
                    override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                        if (isFinishing || isDestroyed) {
                            return
                        }
                        if (sucFlag) {
                            LogUtils.e("main banner load  suc------")
                            initHomeBan()
                        }
                    }
                })
            } else {
                isLoaded = true
                LogUtils.e("main banner show  suc------")
                binding?.homeBans?.let {
                    it.visibility = View.VISIBLE
                    it.setStyles(CommonWeavesStyle.Builder().build())
                    it.setNativeAd(mainAds)
                    MainHomeStudy.homeStudy.loadButtons(it)
                    if(!CommonStudyUtils.posIsCanShow(homeBanner())){
                        val cts = MMKV.defaultMMKV().getInt(MUSIC_CONSTANT_APP_REFRESH, 0)
                        if (cts != 0){
                            messages.removeMessages(MESSAGE_MSG_CODE)
                            messages.sendEmptyMessageDelayed(MESSAGE_MSG_CODE,(cts * 1000).toLong())
                        }
                    }
                }
            }
        }


    private val messages: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                MESSAGE_MSG_CODE -> {
                    initHomeBan()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (messages?.hasMessages(MESSAGE_MSG_CODE) == true){
            messages?.removeMessages(MESSAGE_MSG_CODE)
        }
//        if (binding?.music?.isAnimating){
//            binding?.music.pauseAnimation()
//        }
    }


    /**
     * 扫描歌曲。
     */
    fun scanMusic1() {
            scanMusic()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (MainHomeStudy.homeStudy.uniqueInter != null && !CommonStudyUtils.posIsCanShow(
                    finishPageInterOne()
                )){
                MainHomeStudy.homeStudy.showCachedInter(finishPageInterOne(),this)
                return true
            }
            moveTaskToBack(false)
            return true
        }
        return  super.onKeyDown(keyCode,event)
    }

    /**
     * 扫描歌曲。
     */
    private fun scanMusic() {
        try {
//            net {
                Executors.newCachedThreadPool().submit {
                    try {
                         playlist = MusicScanner.scan(this@HomeActivity) as MutableList<Music>
                        playlist?.let {
                            GlobalMusApp.instance?.mediaManager?.refreshPlaylist(it)
                        }

                    } finally {
                        playlist?.let {
                            ThreadUtils.runOnUiThread {
                                prepareSongsHome(it as List<Music>)
                                updateCollectItem()
                            }

                        }
                    }
            }

        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    private val art = DaoFactory.getDao(Artist::class.java)
    private val album = DaoFactory.getDao(Album::class.java)


    private fun showPlay(flag: Boolean) {
        if (flag) {
            if (binding.music.isAnimating){
                binding.music.pauseAnimation()
            }
            if (binding.btnHomeBottomPlay.visibility == View.GONE){
                binding.btnHomeBottomPlay.visibility = View.VISIBLE
            }
            if (binding.btnHomeBottomPause.visibility == View.VISIBLE){
                binding.btnHomeBottomPause.visibility = View.GONE
            }
        } else {
            if (!binding.music.isAnimating){
                binding.music.resumeAnimation()
            }
            if (binding.btnHomeBottomPlay.visibility == View.VISIBLE){
                binding.btnHomeBottomPlay.visibility = View.GONE
            }
            if (binding.btnHomeBottomPause.visibility == View.GONE){
                binding.btnHomeBottomPause.visibility = View.VISIBLE
            }
        }
    }

    private fun initData() {
        Thread{
            val musics = DaoFactory.getDao(Music::class.java).selectAll()
            prepareSongsHome(musics)
        }.start()
    }

     private fun prepareSongsHome(musics: List<Music>) {
        try {
            mediaManager?.refreshPlaylist(musics as MutableList)
            if (musics.isEmpty()){
                return
            }
            val isOk = mediaManager?.loadCurMusic(musics[0])
            if (isOk == true) {
                binding?.recentTw?.post {
                    binding.tvHomeBottomMusicName.text = musics[0].musicName
                }
                if (MMKV.defaultMMKV().getBoolean(MUSIC_CONSTANT_APP_IS_AUTO_PLAY, false) && mediaManager?.playState !=  AppConfig.MPS_PLAYING) {
                    mediaManager?.playById(musics[0].songId)
                    messages.post {
                        showPlay(false)
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private  var mediaManager: MediaManager?= null

    override fun onConnectCompletion(service: IMediaService?) {
        initData()
    }

}