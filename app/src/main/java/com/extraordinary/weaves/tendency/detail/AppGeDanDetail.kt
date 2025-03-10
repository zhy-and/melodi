package com.extraordinary.weaves.tendency.detail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.extraordinary.weaves.tendency.AppAddList
import com.extraordinary.weaves.tendency.bean.GeDanData
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.DiyPlayListDetViewBinding
import com.extraordinary.weaves.tendency.dialog.showComDialog
import com.extraordinary.weaves.tendency.event.AppEvent.Companion.REFRESH_NAME
import com.extraordinary.weaves.tendency.global.AppConfig
import com.extraordinary.weaves.tendency.global.DataKeeper.createGeDanData
import com.extraordinary.weaves.tendency.global.DataKeeper.loadGeDanData
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.performScanMusic
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.playlist.AppPlayerList
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter.RefreshPlayView
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.addSongsInter
import com.extraordinary.interstings.studios.utils.geDanDetBanner
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.styleDetBanner
import com.extraordinary.interstings.studios.utils.styleDetInter
import com.google.android.gms.ads.nativead.NativeAd
import dora.util.TextUtils
import dora.util.ToastUtils


class AppGeDanDetail : baseActivity(){

    private  var currentBean: GeDanData?= null
    private lateinit var  binding : DiyPlayListDetViewBinding
    private  var listAdapter: TrackListAdapter?= null
    fun showPlay(flag: Boolean) {
        if (flag) {
            if (binding.music.isAnimating){
                binding.music.pauseAnimation()
            }
            if ( binding.btnHomeBottomPlay.visibility == View.GONE){
                binding.btnHomeBottomPlay.visibility = View.VISIBLE
            }
            if ( binding.btnHomeBottomPause.visibility == View.VISIBLE){
                binding.btnHomeBottomPause.visibility = View.GONE
            }
        } else {
            if (!binding.music.isAnimating){
                binding.music.resumeAnimation()
            }
            if ( binding.btnHomeBottomPlay.visibility == View.VISIBLE){
                binding.btnHomeBottomPlay.visibility = View.GONE
            }
            if ( binding.btnHomeBottomPause.visibility == View.GONE){
                binding.btnHomeBottomPause.visibility = View.VISIBLE
            }
        }
    }
    private fun showBanners() {
            val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
                bannerPos, this)
            if (normalBan == null) {
                if (CommonStudyUtils.interWeavesIsShowing) {
                    LogUtils.e("ListBanner  ban inter is showing return------")
                    return
                }
                if (isFinishing || isDestroyed) {
                    LogUtils.e("ListBanner  ban destroy  return------")
                    return
                }
                if (CommonStudyUtils.posIsCanShow(bannerPos)) {
                    LogUtils.e("ListBanner  ban limit  return------")
                    return
                }
                MainHomeStudy.homeStudy.initLoadBanners(bannerPos,object :
                    NativeWeavesListener {
                    override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                        if (isFinishing || isDestroyed) {
                            return
                        }
                        if (sucFlag) {
                            LogUtils.e("ListBanner  ban load  suc------")
                            showBanners()
                        }
                    }
                })
            } else {
                LogUtils.e("ListBanner  ban show  suc------")
                binding?.homeBans?.let {
                    it.visibility = View.VISIBLE
                    it.setStyles(CommonWeavesStyle.Builder().build())
                    it.setNativeAd(normalBan)
                    MainHomeStudy.homeStudy.loadButtons(it)
                    if(!CommonStudyUtils.posIsCanShow(bannerPos)){
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
                showPlay(true)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    private var gedanId = -1L
    private var bannerPos = geDanDetBanner()
   private var interPos = geDanDetInter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaManager = GlobalMusApp?.instance?.mediaManager
        binding = DiyPlayListDetViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .fullScreen(false)
            .titleBar(binding.title)
            .init()
        val detType = intent?.getIntExtra("det_type",-1)
        val style = intent?.getIntExtra("style",-1)
         gedanId = intent?.getLongExtra("gedan_id",-1L) ?: -1L
        val create = intent?.getBooleanExtra("create",false) ?: false
            try {
                    if (create){
                        val oldGeDanListData = loadGeDanData(GeDanData())
                        val id =    System.currentTimeMillis()
                        gedanId = id
                       handler?.post {
                            val dialog =   showComDialog(this, R.layout.pop_create_gedan,false, Gravity.CENTER)
                            val editText =   dialog.findViewById<EditText>(R.id.input_name)
                            val texts =   dialog.findViewById<TextView>(R.id.txt)
                            editText?.setText("New PlayList"+(oldGeDanListData.size+1))
                            texts?.text = ("${editText?.text.toString().length}/40")
                            editText?.addTextChangedListener(object :TextWatcher {
                                override fun beforeTextChanged(
                                    p0: CharSequence?,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int
                                ) {
                                }

                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }

                                override fun afterTextChanged(p0: Editable?) {
                                    texts?.text = ("${editText.text.toString().length}/40")

                                }
                            })

                            dialog.findViewById<View>(R.id.child_ok)?.setOnClickListener {
                                if (TextUtils.isEmpty(editText?.text.toString())){
                                    ToastUtils.showShort("Please input name")
                                    return@setOnClickListener
                                }

                                binding.albumName.text = editText?.text.toString()
                                oldGeDanListData.add(
                                    GeDanData(
                                        tempAddList,
                                        editText?.text.toString(),
                                        id
                                    )
                                )
                                createGeDanData(oldGeDanListData)
                                dialog?.dismiss()
                                ToastUtils.showShort("Create successful！")
                            }

                            dialog.findViewById<View>(R.id.child_cancel)?.setOnClickListener {
                                binding.albumName.text = editText?.text.toString()
                                oldGeDanListData.add(
                                    GeDanData(
                                        tempAddList,
                                        editText?.text.toString(),
                                        id
                                    )
                                )
                                createGeDanData(oldGeDanListData)
                                dialog?.dismiss()
                            }
                        }
                    }
            }catch (e: Exception){
                e.printStackTrace()
            }

        if (detType == -1) {
            //default gedan
           when(style){
               0 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.style_det_two)
               }
               1 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.style_det_two)
               }
               2 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.style_det_thr)
               }
               3 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.style_det_sx)
               }
               4 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.style_det_for)
               }
               5 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()

                   binding.bg.setImageResource(R.mipmap.style_det_one)
               }
               6 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()

                   binding.bg.setImageResource(R.mipmap.style_det_fv)
               }
               7 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()

                   binding.bg.setImageResource(R.mipmap.default_det_play_list_sx)
               }
               8 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()

                   binding.bg.setImageResource(R.mipmap.default_det_play_list_thr)
               }
               9 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()

                   binding.bg.setImageResource(R.mipmap.default_det_play_list_one)
               }
               10 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   binding.bg.setImageResource(R.mipmap.default_det_play_list_for)
                   musicArrayList.clear()
                   musicDao?.selectAll()?.forEach {
                       if (it.favorite == 1){
                           musicArrayList.add(it)
                       }
                   }
               }
               11 ->{
                   shuffleDatas()
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   binding.bg.setImageResource(R.mipmap.default_det_play_list_tw)
               }
               12 ->{
                   bannerPos = styleDetBanner()
                   interPos = styleDetInter()
                   shuffleDatas()
                   binding.bg.setImageResource(R.mipmap.default_det_play_list_fv)
               }

           }
        }else if (detType == 1){
            // diy gedan
            bannerPos = geDanDetBanner()
            interPos = geDanDetInter()
            binding.deleteBtn.visibility = View.VISIBLE
            binding.addBtn.visibility = View.VISIBLE
            binding.addBtn.setOnClickListener {
                startInterActivity(Intent(this@AppGeDanDetail, AppAddList::class.java).apply {
                    putExtra("gedan_id",gedanId)
                    putExtra("add",true)
                }, addSongsInter())
            }

            binding.deleteBtn.setOnClickListener {
                try {
                    binding.bg.post{
                        showComDialog(this, R.layout.pop_delete_gedan,true,Gravity.CENTER).apply {
                            findViewById<View>(R.id.child_ok)?.setOnClickListener {
                                if (gedanId == -1L){
                                    return@setOnClickListener
                                }
                                val oldGeDanListData = loadGeDanData(GeDanData())
                                for (index in oldGeDanListData.indices){
                                    if (oldGeDanListData[index].geDanId == gedanId){
                                        oldGeDanListData.removeAt(index)
                                        break
                                    }
                                }
                                createGeDanData(oldGeDanListData)
                                dismiss()
                                finish()
                            }
                            findViewById<View>(R.id.child_cancel)?.setOnClickListener {
                                dismiss()
                            }
                        }
                    }

                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            if (gedanId != 1L){
                    val oldGeDanListData = loadGeDanData(GeDanData())
                    for (i in oldGeDanListData.indices){
                        if (oldGeDanListData[i].geDanId == gedanId){
                            currentBean = oldGeDanListData[i]
                            diyImage(i)
                            break
                        }
                    }
                        binding.albumName.text = currentBean?.geDanName
                    currentBean?.musicList?.apply {
                        musicArrayList.clear()
                        musicArrayList.addAll(this)
                    }
            }
        }
        MainHomeStudy.homeStudy.getShowCyAds(interPos,this)
        binding.backBtn.setOnClickListener {
            speFinish()
        }
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
            checkPermission()
            LiveEventBus
                .get(REFRESH_NAME, String::class.java)
                .observe(this) {
                    if (isFinishing || isDestroyed){
                        return@observe
                    }
                    if (detType == -1) {
                        bannerPos = styleDetBanner()
                        interPos = styleDetInter()
                        //default gedan
                        when(style){
                            0 ->{
                                shuffleDatas()
                                binding.bg.setImageResource(R.mipmap.style_det_two)
                            }
                            1 ->{
                                shuffleDatas()
                                binding.bg.setImageResource(R.mipmap.style_det_two)
                            }
                            2 ->{
                                shuffleDatas()
                                binding.bg.setImageResource(R.mipmap.style_det_thr)
                            }
                            3 ->{
                                shuffleDatas()
                                binding.bg.setImageResource(R.mipmap.style_det_sx)
                            }
                            4 ->{
                                shuffleDatas()


                                binding.bg.setImageResource(R.mipmap.style_det_for)
                            }
                            5 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.style_det_one)
                            }
                            6 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.style_det_fv)
                            }
                            7 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.default_det_play_list_sx)
                            }
                            8 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.default_det_play_list_thr)
                            }
                            9 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.default_det_play_list_one)
                            }
                            10 ->{

                                binding.bg.setImageResource(R.mipmap.default_det_play_list_for)
                                musicArrayList.clear()
                                musicDao?.selectAll()?.forEach {
                                    if (it.favorite == 1){
                                        musicArrayList.add(it)
                                    }
                                }
                            }
                            11 ->{
                                shuffleDatas()

                                binding.bg.setImageResource(R.mipmap.default_det_play_list_tw)
                            }
                            12 ->{
                                shuffleDatas()
                                binding.bg.setImageResource(R.mipmap.default_det_play_list_fv)
                            }

                        }
                        initRecyleList()
                        checkEmpty()
                    }
                }
        }else{
            initRecyleList()
        }
        checkEmpty()
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
        binding.addPic.setOnClickListener {
            startInterActivity(Intent(this, AppAddList::class.java)
                .apply {
                    putExtra("gedan_id",gedanId)
                }, addSongsInter())
        }

        binding.playBtn.setOnClickListener {
            try {
                GlobalMusApp.instance?.mediaManager?.also {
                    if (musicArrayList.isNotEmpty()){
                        it.refreshPlaylist(musicArrayList)
                        it.playById(musicArrayList[0].songId)
                    }
                }
                startInterActivity(Intent(this, PlayerView::class.java), playViewsInter())
            }catch (e : Exception){
                e.printStackTrace()
            }

        }

    }

    private fun checkEmpty() {
        if (musicArrayList.isEmpty() && gedanId == -1L) {
            binding.recycleHislist.visibility = View.GONE
            binding.noContentPic.visibility = View.VISIBLE
        }else{
            binding.recycleHislist.visibility = View.VISIBLE
            binding.noContentPic.visibility = View.GONE
        }
        if (gedanId != -1L && musicArrayList.isEmpty()) {
            binding.recycleHislist.visibility = View.GONE
            binding.addPic.visibility = View.VISIBLE
        }else{
            binding.recycleHislist.visibility = View.VISIBLE
            binding.addPic.visibility = View.GONE
        }
    }

    private  var tempAddList: ArrayList<Music> = arrayListOf()

    private fun shuffleDatas() {
            musicArrayList.clear()
            musicArrayList.addAll(musicDao.selectAll())
            musicArrayList.shuffle()
    }

    private fun diyImage(position: Int) {
        when(position%4){
            0 -> {
                binding.bg.setBackgroundResource(R.mipmap.diy_det_play_list_one)
            }
            1 -> {
                binding.bg.setBackgroundResource(R.mipmap.diy_det_play_list_for)
            }
            2 -> {
                binding.bg.setBackgroundResource(R.mipmap.diy_det_play_list_tw)
            }
            3 -> {
                binding.bg.setBackgroundResource(R.mipmap.diy_det_play_list_th)
            }

        }
    }

    private  var musicArrayList: ArrayList<Music> = arrayListOf()

    private fun initRecyleList() {
        listAdapter = TrackListAdapter(musicArrayList,5,this@AppGeDanDetail,object :
            RefreshPlayView{
            override fun refresh() {
                if (isFinishing || isDestroyed){
                    return
                }
                binding?.addPic?.visibility = View.VISIBLE
                binding?.recycleHislist?.visibility = View.GONE
            }

        }, gedanId)
        binding.recycleHislist.run {
            layoutManager = LinearLayoutManager(this@AppGeDanDetail)
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

    private  var mediaManager: MediaManager?= null

}