package com.extraordinary.weaves.tendency

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.RatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.beans.bannerEvent
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.ApiStudios.Companion.referInfoLoads
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.CommonStudyUtils.getInstallSys
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_IS_AUTO_PLAY
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistDetInter
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.feedBacksInter
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.homeBanner
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.settingsInter
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListInter
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
import com.extraordinary.weaves.tendency.dialog.event_one_albums_home
import com.extraordinary.weaves.tendency.dialog.event_one_artists_home
import com.extraordinary.weaves.tendency.dialog.event_one_feedback_home
import com.extraordinary.weaves.tendency.dialog.event_one_most_played
import com.extraordinary.weaves.tendency.dialog.event_one_most_played_more
import com.extraordinary.weaves.tendency.dialog.event_one_play_bar
import com.extraordinary.weaves.tendency.dialog.event_one_play_home
import com.extraordinary.weaves.tendency.dialog.event_one_playlists_home
import com.extraordinary.weaves.tendency.dialog.event_one_re_album
import com.extraordinary.weaves.tendency.dialog.event_one_re_album_more
import com.extraordinary.weaves.tendency.dialog.event_one_re_artist
import com.extraordinary.weaves.tendency.dialog.event_one_re_artist_more
import com.extraordinary.weaves.tendency.dialog.event_one_re_playlist
import com.extraordinary.weaves.tendency.dialog.event_one_re_playlist_more
import com.extraordinary.weaves.tendency.dialog.event_one_re_style
import com.extraordinary.weaves.tendency.dialog.event_one_re_style_more
import com.extraordinary.weaves.tendency.dialog.event_one_scan_home
import com.extraordinary.weaves.tendency.dialog.event_one_search_home
import com.extraordinary.weaves.tendency.dialog.event_one_setting_home
import com.extraordinary.weaves.tendency.dialog.event_one_shuffle_home
import com.extraordinary.weaves.tendency.dialog.event_one_styles_home
import com.extraordinary.weaves.tendency.dialog.event_one_tracks_home
import com.extraordinary.weaves.tendency.dialog.event_permit_notice_agree
import com.extraordinary.weaves.tendency.dialog.event_permit_notice_refuse
import com.extraordinary.weaves.tendency.dialog.event_permit_scan_agree
import com.extraordinary.weaves.tendency.dialog.event_permit_scan_refuse
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.dialog.showComDialog
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.scan.ScanPage
import com.extraordinary.weaves.tendency.search.AppSearch
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.extraordinary.weaves.tendency.view.AppMoreView
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ThreadUtils
import dora.util.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Executors


class HomeActivity : baseActivity(), MusicControl.OnConnectCompletionListener {
    private var isCanShow = false
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
        loadHomeConfig()
        initPermission()
        mediaManager = GlobalMusApp.instance?.mediaManager
        mediaManager?.connectService()
        mediaManager?.setOnCompletionListener(this)
//        initPermission()
//        timer = MusicTimer(mPanHandle)
        binding.bans.apply {
            setAlphaItem(true)
            setLoop()
            adapter = RecyHomeAdapter(string,this@HomeActivity)
        }
        LiveEventBus
            .get(AppEvent.REFRESH_MUSIC_HOME_JK_REFRESH, String::class.java)
            .observe(this) {
                Log.e("status =----","REFRESH_MUSIC_HOME_JK_REFRESH")
                updateCollectItem()
            }
        binding.settings.setOnClickListener {
            logMusicEvents(event_one_setting_home)
            isCanShow = true
            startInterActivity(Intent(this, AppMoreView::class.java), settingsInter(),false)
        }
        binding.rlTrack.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this, TrackList::class.java),trackListInter(),false)
            logMusicEvents(event_one_tracks_home)

        }
        binding.rlAlbums.setOnClickListener {
            isCanShow = true
//            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter())
            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter(),false)
            logMusicEvents(event_one_albums_home)

        }
        binding.rlArtist.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this, AppArtistList::class.java), artistListInter(),false)
            logMusicEvents(event_one_artists_home)

        }

        binding.search.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this, AppSearch::class.java), searchInter(),false)
            logMusicEvents(event_one_search_home)
        }
        binding.artistTitle.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this, AppArtistList::class.java), artistListInter(),false)
            logMusicEvents(event_one_re_artist_more)

        }
        binding.stylesTitle.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this, AppStyleList::class.java), styleListInter(),false)
            logMusicEvents(event_one_re_style_more)

        }
        binding.playlistTitle.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter(),false)
            logMusicEvents(event_one_re_playlist_more)

        }

        binding.bottomMusic.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_play_bar)

            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter(),false)
        }
        binding.playMode.setOnClickListener {
            logMusicEvents(event_one_play_bar)

            isCanShow = true
            startInterActivity(Intent(this, PlayerView::class.java).apply {
                putExtra("play",true)
            }, playViewsInter(),false)
            logMusicEvents(event_one_shuffle_home)
        }

        binding.scan.setOnClickListener {
            isCanShow = true
            

            startInterActivity(Intent(this, ScanPage::class.java), scansInter(),false)
            logMusicEvents(event_one_scan_home)
        }

        binding.albumsTitle.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter(),false)
            logMusicEvents(event_one_re_album_more)
        }

        binding.rlStyles.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this, AppStyleList::class.java), styleListInter(),false)
            logMusicEvents(event_one_styles_home)

        }
        binding.rlPlayList.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter(),false)
            logMusicEvents(event_one_playlists_home)

        }


        binding.mostPlayedTitle.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_most_played_more)

            startInterActivity(Intent(this, TrackList::class.java),trackListInter(),false)
        }
        binding.albumsOne.setOnClickListener {
            isCanShow = true
            
            startInterActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al1Txt.tag.toString().toInt())
            }, albumDetInter(),false)
            logMusicEvents(event_one_re_album)

        }
        binding.albumsTw.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al2Txt.tag.toString().toInt())
            }, albumDetInter(),false)
            logMusicEvents(event_one_re_album)

       
        }
        binding.albumsThr.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al3Txt.tag.toString().toInt())
            }, albumDetInter(),false)
            logMusicEvents(event_one_re_album)

        }
        binding.albumsFor.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this@HomeActivity, AppAlbumDetail::class.java).apply {
                putExtra("album_id",binding.al4Txt.tag.toString().toInt())
            }, albumDetInter(),false)
            logMusicEvents(event_one_re_album)

        }
       binding.artistOne.setOnClickListener {
           isCanShow = true

           startInterActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
               putExtra("artist",binding.art1Txt.text.toString())
           }, artistDetInter(),false)
           logMusicEvents(event_one_re_artist)

       }
        binding.artistTw.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art2Txt.text.toString())
            }, artistDetInter(),false)
            logMusicEvents(event_one_re_artist)

        }
        binding.artistTh.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art3Txt.text.toString())
            }, artistDetInter(),false)
            logMusicEvents(event_one_re_artist)

        }
        binding.pops.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_style)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",1)
            }, geDanDetInter(),false)
        }


        binding.rocks.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",2)
            }, geDanDetInter(),false)
            logMusicEvents(event_one_re_style)

          
        }
        binding.countrys.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_style)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",3)
            }, geDanDetInter(),false)
        }
        binding.jazzs.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_style)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",4)
            }, geDanDetInter(),false)
        }
        binding.artistFor.setOnClickListener {
            isCanShow = true

            startInterActivity(Intent(this@HomeActivity, AppArtDetail::class.java).apply {
                putExtra("artist",binding.art4Txt.text.toString())
            }, artistDetInter(),false)
            logMusicEvents(event_one_re_artist)
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
            isCanShow = true
            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",8)
            }, geDanDetInter(),false)
            logMusicEvents(event_one_re_playlist)

        }


        binding.listTw.setOnClickListener {
            isCanShow = true
            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",9)
            }, geDanDetInter(),false)
            logMusicEvents(event_one_re_playlist)

        }
        binding.listThr.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_playlist)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",11)
            }, geDanDetInter(),false)
        }
        binding.listFor.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_playlist)

            //collect
            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",12)
            }, geDanDetInter(),false)
        }
        binding.listFv.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_playlist)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",7)
            }, geDanDetInter(),false)
        }
        binding.listSix.setOnClickListener {
            isCanShow = true
            logMusicEvents(event_one_re_playlist)

            startInterActivity(Intent(this@HomeActivity, AppGeDanDetail::class.java).apply {
                putExtra("style",10)
            }, geDanDetInter(),false)
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
        try {
            logMusicEvents(event_one_most_played)
            musicDao.selectAll()?.also {
                if (it.isNotEmpty() && postion < it.size){
                    mediaManager?.playById(it[postion].songId)
                }
            }
            isCanShow = true
            startInterActivity(Intent(this, PlayerView::class.java), playViewsInter(),false)
        }catch (e : Exception){
            e.printStackTrace()
        }

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
//        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
//            XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
//                .request(object :OnPermissionCallback{
//
//                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
//                        logMusicEvents(event_permit_scan_agree)
//                        checkPostPermission()
//                        if (allGranted) {
//                            MMKV.defaultMMKV().putBoolean("is_first_auth",true)
//                            scanMusic1()
//                        }
//                    }
//                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
//                        logMusicEvents(event_permit_scan_refuse)
//
//                        checkPostPermission()
//                        if (doNotAskAgain) {
//                            ToastUtils.showLong(R.string.permission_never_denyd)
////                        toast("被永久拒绝授权，请手动授予录音和日历权限")
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
////                            XXPermissions.startPermissionActivity(this@HomeActivity, permissions)
//                        } else {
//                            ToastUtils.showLong(R.string.permission_one_denyd)
////                        toast("获取录音和日历权限失败")
//                        }
//                    }
//                })
//        }else{
            checkPostPermission()
//        }
    }





    private fun checkPostPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !XXPermissions.isGranted(this,Permission.POST_NOTIFICATIONS)) {
            XXPermissions.with(this@HomeActivity).permission(Permission.POST_NOTIFICATIONS).request(object : OnPermissionCallback{
                override fun onGranted(p0: MutableList<String>, p1: Boolean) {
                    logMusicEvents(event_permit_notice_agree)

                }
                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    logMusicEvents(event_permit_notice_refuse)

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
            if (!MMKV.defaultMMKV().getBoolean("showSuc",false)
                && (System.currentTimeMillis() - getInstallSys() > 6 * 60 * 60 * 1000)  && isCanShow) {
                starRate()
            }
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
    private fun starRate() {
        try {
            val dialog =   showComDialog(this, R.layout.pop_start,false, Gravity.CENTER)
            dialog.show()
            val ratingBar = dialog.findViewById<RatingBar>(R.id.rating_bar)
            ratingBar?.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                ThreadUtils.runOnUiThreadDelayed({
                    dialog.dismiss()
                    MMKV.defaultMMKV().putBoolean("showSuc",true)
                    if (rating == 5f) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data =
                                Uri.parse("market://details?id=" + AppUtils.getAppPackageName()) //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
                            //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
                            if (intent.resolveActivity(packageManager) != null) { //可以接收
                                startActivity(intent)
                            } else { //没有应用市场，我们通过浏览器跳转到Google Play
                                intent.data =
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + AppUtils.getAppPackageName())
                                //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
                                if (intent.resolveActivity(packageManager) == null) { //有浏览器
                                    startActivity(intent)
                                } else { //
                                    ToastUtils.showShort(
                                        "You don't have an app market installed, not even a browser!",
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },1000)

            }

        } catch (e: Exception) {
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
            if (MainHomeStudy.homeStudy.uniqueInter != null){
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
                                logMusicEvents("home_scan_count",playlist?.size.toString())
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
                binding.music.pauseAnimation()
            if (binding.btnHomeBottomPlay.visibility == View.GONE){
                binding.btnHomeBottomPlay.visibility = View.VISIBLE
            }
            if (binding.btnHomeBottomPause.visibility == View.VISIBLE){
                binding.btnHomeBottomPause.visibility = View.GONE
            }
        } else {
                binding.music.resumeAnimation()
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

                if (MMKV.defaultMMKV().getBoolean(MUSIC_CONSTANT_APP_IS_AUTO_PLAY, false) && mediaManager?.playState !=  AppConfig.MPS_PLAYING
                    && !checkRefer()
                    ) {
                    autoPlay(musics)

                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun autoPlay(musics: List<Music>) {
        try {
            mediaManager?.playById(musics[0].songId)
            messages.post {
                showPlay(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkRefer(): Boolean {
        val str = MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO, "") ?: ""
        if (!TextUtils.isEmpty(str) && str.contains("google-play")){
                return true
        }
        return  false
    }

    private  var mediaManager: MediaManager?= null

    override fun onConnectCompletion(service: IMediaService?) {
        initData()
    }

}