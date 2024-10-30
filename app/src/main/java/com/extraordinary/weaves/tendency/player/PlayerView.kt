package com.extraordinary.weaves.tendency.player

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.MPS_PAUSE
import com.extraordinary.weaves.tendency.global.AppConfig.Companion.MPS_PLAYING
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.receiver.MusicPlayReceiver
import com.extraordinary.weaves.tendency.CommonMessageHandler
import com.extraordinary.weaves.tendency.core.PlayModeControl
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.scan.ScanPage
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.PlayerMainBinding
import com.extraordinary.weaves.tendency.lyric.AppLyricView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import dora.db.builder.WhereBuilder
import dora.db.dao.DaoFactory
import dora.db.dao.OrmDao
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.playViewBanner
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.google.android.gms.ads.nativead.NativeAd
import com.tencent.mmkv.MMKV

class PlayerView : baseActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private var mPanHandle : Handler = CommonMessageHandler(this)

    override fun receiverMsgCallBack(message: Message) {
        super.receiverMsgCallBack(message)

        when (message.what) {
            0x100 ->
                mediaManager?.let {
                    refreshSeekProgress(
                        it.position(),
                        it.duration()
                    )
                }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish()
            return true
        }
        return  super.onKeyDown(keyCode,event)
    }

    private fun finishBack() {
//       finish()
    }

    override fun onResume() {
        super.onResume()
        showBanners()
        updateStatus()
    }
    private  lateinit var binding: PlayerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(playViewsInter(),this)

        ImmersionBar.with(this)
            .transparentStatusBar()
            .init()
        binding = PlayerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaManager = GlobalMusApp.instance?.mediaManager
        updateStatus()

        playModeControl = PlayModeControl(this)
        initViews()
        setTimer(mPanHandle)
        LiveEventBus
            .get(AppEvent.REFRESH_MUSIC_HOME_MPS_INVALID, String::class.java)
            .observe(this) {
                LogUtils.e("aaaaaaae11")
                timer?.stopTimer()
                val music = mediaManager?.curMusic
                refreshUI(0, music!!.duration, music)
                showPlay(true)
            }

        LiveEventBus
            .get(AppEvent.REFRESH_MUSIC_HOME_MPS_PAUSE, String::class.java)
            .observe(this) {
                LogUtils.e("aaaaaaae2")
                val music = mediaManager?.curMusic
                timer?.stopTimer()
                //todo playauto
                if (playAuto){
                    mediaManager?.let {
                        if (music != null){
                            refreshUI( it.position(), music.duration, music)
                        }
                    }
                }
                showPlay(true)
            }

        LiveEventBus
            .get(AppEvent.REFRESH_MUSIC_HOME_MPS_PLAYING, String::class.java)
            .observe(this) {
                LogUtils.e("aaaaaaae3")
                val music = mediaManager?.curMusic
                mediaManager?.let {
                    refreshUI(it.position(), music!!.duration, music)
                }
                showPlay(false)
                timer?.startTimer()
            }

        LiveEventBus
            .get(AppEvent.REFRESH_MUSIC_HOME_MPS_PREPARE, String::class.java)
            .observe(this) {
                LogUtils.e("aaaaaaae4")
                timer?.stopTimer()
                val music = mediaManager?.curMusic
                refreshUI(0, music!!.duration, music)
                showPlay(true)
            }
        mediaManager?.let {
            receviver = MusicPlayReceiver(it)
        }
        val filter = IntentFilter(AppConfig.ACTION_PLAY)
        if (Build.VERSION.SDK_INT >= 34){
            registerReceiver(receviver, filter, RECEIVER_EXPORTED)
        }else{
            registerReceiver(receviver, filter)
        }
        LogUtils.e("111111"+mediaManager?.playState)
        if (mediaManager?.playState == MPS_PLAYING){
            val music = mediaManager?.curMusic
            mediaManager?.let {
                refreshUI(it.position(), music!!.duration, music)
            }
            showPlay(false)
            timer?.startTimer()
        }else if (mediaManager?.playState == MPS_PAUSE){
            val music = mediaManager?.curMusic
            timer?.stopTimer()
            mediaManager?.let {
                refreshUI(it.position(), music!!.duration, music)
            }
            showPlay(true)
        }
        val status = intent?.getBooleanExtra("play",false)
        if (status == true){
            if (curMusic == null) {
                return
            }
            mediaManager?.replay()
        }
    }

    private fun updateStatus() {
        try {
            mediaManager?.curMusic?.apply {
                if (musicDao.selectAll().isNotEmpty()) {
                    for (i in musicDao.selectAll()) {
                        if (i.id == id) {
                            favorite = i.favorite
                            mediaManager?.setCurMusic(this)
                            break
                        }
                    }
                }
                refreshFavorite(favorite)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var receviver: BroadcastReceiver?= null
    private var mediaManager: MediaManager?= null

    private var curMusic: Music? = null
    private var playAuto: Boolean = false
    private var seekBarProgress: Int = 0
    private val musicDao: OrmDao<Music> = DaoFactory.getDao(
        Music::class.java)
    private lateinit var playModeControl: PlayModeControl


    private fun initViews() {
        try {
            timer?.stopTimer()
            mediaManager?.curMusic?.let {
                refreshUI(0, it.duration, it)
            }
            showPlay(true)

        }catch (e: Exception){
            e.printStackTrace()
        }
        binding.btnMusicPlayPrev.setOnClickListener(this)
        binding.btnMusicPlayNext.setOnClickListener(this)
        binding.btnMusicPlayPlay.setOnClickListener(this)
        binding.btnMusicPlayPause.setOnClickListener(this)
        binding.btnMusicPlayMode.setOnClickListener(this)
        binding.btnMusicPlayFavorite.setOnClickListener(this)
        binding.sbMusicPlayPlayback.setOnSeekBarChangeListener(this)
        binding.lyric.setOnClickListener {
            startActivity(Intent(this, AppLyricView::class.java))
            finishBack()
        }

        binding.playList.setOnClickListener {
            if (curMusic == null) {
                toScan()
                return@setOnClickListener
            }
            startActivity(Intent(this, AppPlayerList::class.java))
            finishBack()
        }
        binding.lock.setOnClickListener {
            try {
                if (curMusic == null) {
                    toScan()
                    return@setOnClickListener
                }
                mediaManager?.curMusic?.albumId?.let {
                    startActivity(Intent(this@PlayerView, AppAlbumDetail::class.java).apply {
                        putExtra("album_id",it)
                    })
                    finishBack()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        binding.musicSinger.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        binding.musicSinger.paint.isAntiAlias = true
        binding.musicSinger.setOnClickListener {
            if (curMusic == null) {
                toScan()
                return@setOnClickListener
            }
            startActivity(Intent(this@PlayerView, AppArtDetail::class.java).apply {
                putExtra("artist",binding.musicSinger.text.toString())
            })
        }
        binding.backBtn.setOnClickListener { speFinish() }
    }

//    fun setMusicTimer(timer: MusicTimer) {
//        this.musicTimer = timer
//    }

    fun showPlay(flag: Boolean) {
        if (flag) {
            binding.btnMusicPlayPlay.visibility = View.VISIBLE
            binding.btnMusicPlayPause.visibility = View.GONE
        } else {
            binding.btnMusicPlayPlay.visibility = View.GONE
            binding.btnMusicPlayPause.visibility = View.VISIBLE
        }
    }


    private fun refreshFavorite(favorite: Int) {
        if (favorite == 1) {
            binding.btnMusicPlayFavorite.setImageResource(R.mipmap.bg_collect_suc)
        } else {
            binding.btnMusicPlayFavorite.setImageResource(R.mipmap.ic_favorite_unchecked)
        }
        curMusic!!.favorite = favorite
        mediaManager!!.setCurMusic(curMusic!!)
        saveFavorite(curMusic!!, favorite)
    }

    private fun saveFavorite(music: Music, favorite: Int) {
        music.favorite = favorite
        musicDao.update(WhereBuilder.create().addWhereEqualTo("_id", music.id), music)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        LogUtils.e("progress")
            if (!playAuto) {
                seekBarProgress = progress
            }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        LogUtils.e("onStartTrackingTouch")
            playAuto = false
            timer?.stopTimer()
            mediaManager!!.pause()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        LogUtils.e("onStopTrackingTouch")
            playAuto = true
            mediaManager?.let {
                it.seekTo(seekBarProgress)
                refreshSeekProgress(it.position(), it.duration())
                it.replay()
            }
            timer?.startTimer()
    }


    /**
     * 刷新播放进度。
     */
    fun refreshSeekProgress(curTime: Int, totalTime: Int) {
        var curTime = curTime
        var totalTime = totalTime
        val tempCurTime = curTime
        curTime /= 1000
        totalTime /= 1000
        val curMinute = curTime / 60
        val curSecond = curTime % 60

        val curTimeString = String.format("%02d:%02d", curMinute, curSecond)
        binding.tvMusicPlayCurTime.text = curTimeString

        var rate = 0
        if (totalTime != 0) {
            rate = (curTime.toFloat() / totalTime * 100).toInt()
        }
        binding.sbMusicPlayPlayback.progress = rate

    }

    /**
     * 刷新歌曲播放界面。
     */
    private fun refreshUI(curTime: Int, totalTime: Int, music: Music) {
        try {
            var totalTime = totalTime

            curMusic = music
            refreshFavorite(music.favorite)
            val tempTotalTime = totalTime

            totalTime /= 1000
            val totalMinute = totalTime / 60
            val totalSecond = totalTime % 60
            val totalTimeString = String.format("%02d:%02d", totalMinute,
                totalSecond)

            binding.tvMusicPlayTotalTime.text = totalTimeString

            binding.musicName.text = music.musicName
            binding.musicSinger.text = music.artist
            refreshSeekProgress(curTime, tempTotalTime)
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            //上一首
            R.id.btn_music_play_prev -> {
                if (curMusic == null) {
                    toScan()
                    return
                }
                mediaManager!!.prev()
            }
            //播放
            R.id.btn_music_play_play -> {
                if (curMusic == null) {
                    toScan()
                    return
                }
                mediaManager!!.replay()
            }
            //下一首
            R.id.btn_music_play_next -> {
                if (curMusic == null) {
                    toScan()
                    return
                }
                mediaManager!!.next()
            }
            //暂停
            R.id.btn_music_play_pause -> mediaManager!!.pause()
            //音量
            //播放模式
            R.id.btn_music_play_mode -> {
                if (curMusic == null) {
                    toScan()
                    return
                }
                playModeControl.changePlayMode(binding.btnMusicPlayMode)
            }
            //喜爱
            R.id.btn_music_play_favorite -> {
                try {
                    if (curMusic == null) {
                        toScan()
                        return
                    }
                    if (mediaManager!!.curMusic!!.favorite == 0) {
//                    startAnimation(iv_sliding_favorite_flying!!)
                        refreshFavorite(1)
                    } else {
                        refreshFavorite(0)
                    }
                }catch (_: Exception){

                }

            }
        }
    }

    private fun toScan() {
        startActivity(Intent(this, ScanPage::class.java))
        finishBack()
    }

    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            playViewBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("playViewBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("playViewBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(playViewBanner())) {
                LogUtils.e("playViewBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(playViewBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("playViewBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("playViewBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(playViewBanner())){
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
    }


}