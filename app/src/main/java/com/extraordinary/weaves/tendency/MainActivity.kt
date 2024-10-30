//package com.extraordinary.weaves.tendency
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Message
//import android.view.KeyEvent
//import android.view.View
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.TextView
//import com.bumptech.glide.Glide
//import com.extraordinary.weaves.tendency.global.DataKeeper.createGeDanData
//import com.extraordinary.weaves.tendency.global.DataKeeper.initRecommendData
//import com.extraordinary.weaves.tendency.global.DataKeeper.loadGeDanData
//import com.extraordinary.weaves.tendency.global.DataKeeper.loadRecommendData
//import com.extraordinary.weaves.tendency.databinding.ActivityMainBinding
//import com.hjq.permissions.OnPermissionCallback
//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
//import com.stx.xhb.androidx.XBanner
//import com.stx.xhb.androidx.transformers.OverLapPageTransformer
//import dora.db.builder.QueryBuilder
//import dora.db.builder.WhereBuilder
//import dora.db.dao.DaoFactory
//import dora.http.DoraHttp.net
//import com.blankj.utilcode.util.LogUtils
//import dora.util.TextUtils
//import java.util.Collections
//import java.util.concurrent.Executors
//
//
//class MainActivity : baseActivity(), MusicControl.OnConnectCompletionListener {
//    private var mPanHandle : Handler = PankaHandlerKeeper(this)
//    private fun refreshSeekProgress(curTime: Int, totalTime: Int, pendingProgress: Int) {
//        var curTime = curTime
//        var totalTime = totalTime
//
//        curTime /= 1000
//        totalTime /= 1000
//        val curMinute = curTime / 60
//        val curSecond = curTime % 60
//
//        val curTimeString = String.format("%02d:%02d", curMinute, curSecond)
//        starttxt.text = curTimeString
//
//        var rate = 0
//        if (totalTime != 0) {
//            rate = (curTime.toFloat() / totalTime * 100).toInt()
//        }
//        progress.progress = rate
//        progress.secondaryProgress = pendingProgress
//    }
//
//    private  val  handler = Handler { msg ->
//        when (msg.what) {
//            0x100 -> refreshSeekProgress(
//                mediaManager!!.position(),
//                mediaManager.duration(), mediaManager.pendingProgress()
//            )
//        }
//        false
//    }
//    var string = arrayListOf(CustomViewsInfo(R.drawable.ic_launcher_background),CustomViewsInfo(R.drawable.ic_launcher_background),CustomViewsInfo(R.drawable.ic_launcher_background),CustomViewsInfo(R.drawable.ic_launcher_background))
//    private lateinit var  play : View
//    private lateinit var  binding : ActivityMainBinding
//    private lateinit var  next : View
//    private lateinit var  pause : View
//    private lateinit var  musicname : TextView
//    private lateinit var  artist : TextView
//    private lateinit var  starttxt : TextView
//    private lateinit var  endtxt : TextView
//    private lateinit var  progress : ProgressBar
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        next  = findViewById(R.id.btn_home_bottom_next)
//        play  = findViewById(R.id.btn_home_bottom_play)
//        pause  = findViewById(R.id.btn_home_bottom_pause)
//        musicname  = findViewById(R.id.tv_home_bottom_music_name)
//        artist  = findViewById(R.id.tv_home_bottom_artist)
//        starttxt  = findViewById(R.id.tv_home_bottom_position)
//        endtxt  = findViewById(R.id.tv_home_bottom_duration)
//        progress  = findViewById(R.id.sb_home_bottom_playback)
//        mediaManager = GlobalMusApp.instance!!.mediaManager!!
//        mediaManager.connectService()
//        mediaManager.setOnCompletionListener(this)
////        setTimer(handler)
//        timer = MusicTimer(mPanHandle)
//        binding.xbanner.setBannerData(string)
//        binding.xbanner.setCustomPageTransformer(OverLapPageTransformer())
//        binding.xbanner.loadImage(object :XBanner.XBannerAdapter{
//            override fun loadBanner(banner: XBanner?, model: Any?, view: View?, position: Int) {
//                Glide.with(this@MainActivity).load(
//                    (model as CustomViewsInfo).xBannerUrl).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(
//                    view as ImageView
//                )
//            }
//        })
////        LiveEventBus
////            .get(REFRESH_MUSIC_HOME_MPS_INVALID, String::class.java)
////            .observe(this) {
////                LogUtils.e("eeeeeeee11")
////                timer?.stopTimer()
////                val music = mediaManager.curMusic
////                refreshUI(0, music!!.duration, music)
////                showPlay(true)
////            }
////
////        LiveEventBus
////            .get(REFRESH_MUSIC_HOME_MPS_PAUSE, String::class.java)
////            .observe(this) {
////                LogUtils.e("eeeeeeee2")
////                val music = mediaManager.curMusic
////                timer?.stopTimer()
////                refreshUI( mediaManager.position(), music!!.duration, music)
////                showPlay(true)
////            }
////
////        LiveEventBus
////            .get(REFRESH_MUSIC_HOME_MPS_PLAYING, String::class.java)
////            .observe(this) {
////                LogUtils.e("eeeeeeee3")
////
////                val music = mediaManager.curMusic
////                refreshUI(mediaManager.position(), music!!.duration, music)
////                showPlay(false)
////                timer?.startTimer()
////            }
////
////        LiveEventBus
////            .get(REFRESH_MUSIC_HOME_MPS_PREPARE, String::class.java)
////            .observe(this) {
////                LogUtils.e("eeeeeeee4")
////                timer?.stopTimer()
////                val music = mediaManager.curMusic
////                refreshUI(0, music!!.duration, music)
////                showPlay(true)
////            }
//
////        defaultArtwork = BitmapFactory.decodeResource(
////            resources,
////            R.drawable.bottom_bar_cover_bg
////        )
//
////        uiManager = UIManager(this, binding.root)
////        bottomBar = UIBottomBar(this, uiManager)
////        musicPlay = UIMusicPlay(this, uiManager)
////        musicTimer = MusicTimer(bottomBar.handler, musicPlay.handler)
////        musicPlay.setMusicTimer(musicTimer)
////        receviver = MusicPlayReceiver(mediaManager)
////        val filter = IntentFilter(AppConfig.ACTION_PLAY)
////        if (Build.VERSION.SDK_INT >= 34){
////            registerReceiver(receviver, filter, RECEIVER_EXPORTED)
////        }else{
////            registerReceiver(receviver, filter)
////        }
//
//    }
//    private var receviver : MusicPlayReceiver ?= null
//    override fun onDestroy() {
//        super.onDestroy()
//        LogUtils.e("dddddddddddddddd")
//    }
//
//    override fun receiverMsgCallBack(message: Message) {
//        super.receiverMsgCallBack(message)
//
//        when (message.what) {
//            0x100 -> refreshSeekProgress(
//                mediaManager!!.position(),
//                mediaManager.duration(), mediaManager.pendingProgress()
//            )
//        }
//    }
//
//
//    /**
//     * 扫描歌曲。
//     */
//     fun performScanMusic() {
//        val builder = QueryBuilder.create().where(
//            WhereBuilder.create()
//                .addWhereEqualTo(Music.COLUMN_FAVORITE, 1))
//        val favoriteCount = DaoFactory.getDao(Music::class.java).count(builder)
//        if (favoriteCount > 0) { //有收藏的歌曲
//        } else {
//            scanMusic()
//        }
//    }
//    /**
//     * 扫描歌曲。
//     */
//    private fun scanMusic() {
//        net {
////            val dialog = DoraLoadingDialog(this).show("正在扫描...") {
////                setCancelable(false)
////                setCanceledOnTouchOutside(false)
////            }
////            DoraHttp.request {
//            Executors.newCachedThreadPool().submit {
//                try {
//                    val playlist = MusicScanner.scan(this@MainActivity) as MutableList<Music>
//                    for (i in playlist){
//                        LogUtils.e(""+i.songId+"---"+i.albumId+"name ="+i.musicName+"dura ="+i.duration+"art ="+i.artist)
//                    }
//                    GlobalMusApp.instance!!.mediaManager!!.refreshPlaylist(playlist)
//                    if (playlist.isEmpty()){
//                        LogUtils.e("empty")
//                        return@submit
//                    }
//                    initRecommendData(musicDao.selectAll().take(8))
//                    for (i in loadRecommendData(Music())){
//                        LogUtils.e("recommed ----"+i.musicName)
//                    }
//                } finally {
////                        it.releaseLock(th)
//                }
//            }
////            }
////            homeFragment.onRefreshLocalMusic()
////            dialog.dismiss()
//        }
//
//    }
//
//    fun click(view: View) {
//
//
////        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
////            .request { _, allGranted ->
////                if (allGranted) {
////                    performScanMusic()
////                }
////
////            }
//
//        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
//            .request(object :OnPermissionCallback{
//                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
//                    if (allGranted) {
//                        performScanMusic()
//                    }
//                }
//                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
//                    if (doNotAskAgain) {
////                        toast("被永久拒绝授权，请手动授予录音和日历权限")
//                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
//                    } else {
////                        toast("获取录音和日历权限失败")
//                    }
//                }
//            })
//    }
//
//
//    private val musicDao = DaoFactory.getDao(Music::class.java)
//    private val art = DaoFactory.getDao(Artist::class.java)
//    private val album = DaoFactory.getDao(Album::class.java)
//
//    fun youtube(view: View) {
//        val artists =  art.selectAll()
//        for (i in artists){
//            LogUtils.e("++++"+i.name+"---"+i.number_of_tracks)
//        }
//
//
//        val albumList =  album.selectAll()
//        for (i in albumList){
//            LogUtils.e("al_id"+i.album_id+"al_name"+i.album_name+"al_ath"+i.album_cover_path+"number"+i.number_of_songs)
//            val art_det = musicDao.select(
//                QueryBuilder.create().where(
//                    WhereBuilder.create().addWhereEqualTo("music_name", i.album_id)
//                )
//            )
//            for (j in art_det){
//                LogUtils.e("query song ==="+j.songId+j.duration+j.musicName)
//            }
//
//        }
//
//        //todo 下一秒播放
////        binding.statusbarHome.postDelayed({
//        var isexist = false
//        var index = -1
//        var list = mediaManager.playlist ?: return
//
//        // -------------------
//        list?.let{
//            for (i in list){
//                LogUtils.e("old"+i.musicName)
//            }
//        }
//        val art_det = musicDao.select(
//            QueryBuilder.create().where(
//                WhereBuilder.create().addWhereEqualTo("music_name", "Cruel Summer")
//            )
//        )
//        //如果列表中存在当前歌曲
//        for (ind in list.indices){
//            if (list[ind].musicName == art_det[0].musicName){
//                index = ind
//                isexist = true
//                break
//            }
//        }
//
//
//        if (isexist){
//            LogUtils.e("列表存在")
//            val sourceIndex: Int = index
//            val targetIndex: Int = mediaManager.currentIndex + 1 // 将元素2移动到列表的第一个位置
//
//            if (sourceIndex != -1) {
//                Collections.swap(list, sourceIndex, targetIndex)
//                LogUtils.e("移动后的列表: $list")
//            } else {
//                LogUtils.e("列表中不存在指定的元素")
//            }
//        }else{
//            LogUtils.e("列表不存在")
//            //不存在 全局直接添加
//
//            list?.add(mediaManager.currentIndex + 1, art_det[0]) //插入到当前播放位置
//
//        }
//        list?.let{
//            for (i in list){
//                LogUtils.e("charuhou"+i.musicName)
//            }
//        }
//        mediaManager.refreshPlaylist(list)
//        //todo  top
//
//
////        val albumList =  album.selectAll()
////        for (i in albumList){
////            LogUtils.e("al_id"+i.album_id+"al_name"+i.album_name+"al_ath"+i.album_cover_path+"number"+i.number_of_songs)
////        }
//    }
//
//    fun search(view: View) {
//        val edite : EditText = findViewById(R.id.search_edit)
//        val searchlist = arrayListOf<Music>()
//        searchlist.clear()
//        val allmusic =  musicDao.selectAll()
//        for (i in allmusic){
//            if (i.musicName.lowercase().contains(edite.text.toString().lowercase())){
//                searchlist.add(i)
//            }
//        }
//        for (j in searchlist){
//            LogUtils.e("------"+j.musicName + "++++++"+j.artist)
//        }
//    }
//
//
//
//    fun showPlay(flag: Boolean) {
//        if (flag) {
//            play!!.visibility = View.VISIBLE
//            pause!!.visibility = View.GONE
//        } else {
//            play!!.visibility = View.GONE
//            pause!!.visibility = View.VISIBLE
//        }
//    }
//
//    fun list(view: View) {
////         mediaManager!!.pause()
////        showPlaylistDialog()
//        startActivity(Intent(this,PlayerView::class.java))
//
//    }
//    fun next(view: View) {
//        mediaManager!!.next()
//    }
//    fun play(view: View) {
////        pause.visibility = View.VISIBLE
////        play.visibility = View.GONE
//        mediaManager!!.replay()
//    }
//
//    fun initData() {
//        val musics = DaoFactory.getDao(Music::class.java).selectAll()
////        val music = DaoFactory.getDao(Music::class.java).selectOne(
////            QueryBuilder.create()
////                .orderBy(Music.COLUMN_LAST_PLAY_TIME + " desc"))
////        if (music != null) {
//        if (musics.isEmpty()){
//            return
//        }
//        mediaManager.refreshPlaylist(musics as MutableList<Music>)
//        val isOk = mediaManager.loadCurMusic(musics[0])
//        if (isOk) {
//            musicname.text = musics[0].musicName
//            artist.text = musics[0].artist
//            refreshUI(0, musics[0].duration, musics[0])
////                mediaManager.playById(musics[0].songId)
//        }
////        }
//    }
//    fun refreshUI(curTime: Int, totalTime: Int, music: Music?) {
//        if (music == null) return
//        var totalTime = totalTime
//        val tempTotalTime = totalTime
//
//        totalTime /= 1000
//        val totalMinute = totalTime / 60
//        val totalSecond = totalTime % 60
//        val totalTimeString = String.format("%02d:%02d", totalMinute,
//            totalSecond)
//
//        endtxt!!.text = totalTimeString
//        if (TextUtils.isNotEqualTo(musicname!!.text.toString(), music.musicName)) {
//            musicname!!.text = music.musicName
//        }
//
//        if (TextUtils.isNotEqualTo(artist!!.text.toString(), music.artist)) {
//            artist!!.text = music.artist
//        }
//        if (music.albumId != -1) {
//            try {
////                val bitmap = MusicUtils.getCachedArtwork(manager.view.context, music.albumId.toLong(),
////                    defaultAlbumIcon)
////                if (bitmap != null) {
////                    iv_home_bottom_album!!.setBackgroundDrawable(BitmapDrawable(manager.view.context
////                        .resources, bitmap))
////                }
//            } catch (e:UnsupportedOperationException) {
////                java.lang.UnsupportedOperationException: Unknown or unsupported URL: content://media/external/audio/albumart/-840129354
//            }
//        } else {
////            mediaManager!!.updateNotification(defaultAlbumIcon!!, music.musicName, music.artist)
//        }
//        refreshSeekProgress(curTime, tempTotalTime, mediaManager!!.pendingProgress())
//    }
//    fun pause(view: View) {
////        pause.visibility = View.GONE
////        play.visibility = View.VISIBLE
//        mediaManager!!.pause()
//    }
//    private lateinit var mediaManager: MediaManager
//
//    override fun onConnectCompletion(service: IMediaService?) {
//        initData()
//    }
//
//    fun topage(view: View) {
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        moveTaskToBack(false)
//        return true
//    }
//
//    fun search2(view: View) {
//        //查询歌单列表
//        var oldGeDanListData = loadGeDanData(GeDanData())
//        var geDan1 = loadRecommendData(Music())
//        oldGeDanListData.add(0,GeDanData(geDan1,"test1",System.currentTimeMillis()))
//        createGeDanData(oldGeDanListData)
//
////        oldData.add(0,GeDanData(musicDao.selectAll().subList(0,2),"test2"))
////        createGeDanData(oldData)
//
//        //查询歌单列表
//        for (i in  loadGeDanData(GeDanData())){
//            LogUtils.e("gedan ::"+i.geDanName+"size :::"+i.musicList.size)
//        }
//
//        // 某个歌单歌曲增删改（通过歌单id）
////        var oldGeDanListData = loadGeDanData(GeDanData())
////        for (i in oldGeDanListData){
////            if (i.geDanId == 0L){
////                i.musicList = newMusicList
////            }
////        }
////        createGeDanData(oldGeDanListData)
//
//
//        //查询某个专辑的所有歌曲
//        val album1 = album.selectAll()[0]
//       var albumIdList =  musicDao.select(
//            QueryBuilder.create().where(
//                WhereBuilder.create().addWhereEqualTo("album_id", album1.album_id)
//            ))
//
//        //查询某个歌手的所有歌曲
//        val artist = art.selectAll()[0]
//        var artNameList =  musicDao.select(
//            QueryBuilder.create().where(
//                WhereBuilder.create().addWhereEqualTo("artist", artist.name)
//            ))
//
//
//        //查询收藏列表
//        var favorite =  musicDao.select(
//            QueryBuilder.create().where(
//                WhereBuilder.create().addWhereEqualTo("favorite", 1)
//            ))
//
//
//
//        //
//
//
//    }
//
//
//}