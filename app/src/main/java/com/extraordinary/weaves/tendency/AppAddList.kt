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
import com.extraordinary.interstings.studios.utils.addSongsBanner
import com.extraordinary.interstings.studios.utils.addSongsInter
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.bean.GeDanData
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.AddSongsListViewBinding
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail
import com.extraordinary.weaves.tendency.event.AppEvent
import com.extraordinary.weaves.tendency.global.DataKeeper.createGeDanData
import com.extraordinary.weaves.tendency.global.DataKeeper.loadGeDanData
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.util.ToastUtils
import java.text.SimpleDateFormat
import java.util.Date

class AppAddList : baseActivity(), TrackListAdapter.RefreshPlayView,
    AddListAdapter.RefreshPlayView {
    private var isSelect = false
    private var currentBean: GeDanData?= null
    private  var musicArrayList: ArrayList<Music> = arrayListOf()
        private  var nativeList: ArrayList<Music> = arrayListOf()
    private lateinit var mediaManager: MediaManager
    private  lateinit var playListBinding: AddSongsListViewBinding
    private  var playAdapter: AddListAdapter?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(addSongsInter(),this)
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .navigationBarColor(R.color.black)
            .init()
        playListBinding = AddSongsListViewBinding.inflate(layoutInflater)
        setContentView(playListBinding.root)
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        playListBinding.includeTitles.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Add Songs"
        }
        val gedanId = intent?.getLongExtra("gedan_id",-1)
        val addStatus = intent?.getBooleanExtra("add",false) ?: false
            try {
                if (gedanId != -1L){
                    val oldGeDanListData = loadGeDanData(GeDanData())
                    for (i in oldGeDanListData){
                        if (i.geDanId == gedanId){
                            currentBean = i
                            break
                        }
                    }
                    }
            }catch (e: Exception){
                e.printStackTrace()
            }
        playListBinding.btnOk.setOnClickListener {
            if (gedanId != -1L && addStatus){
                //update data
                playAdapter?.getCreateData()?.apply {
                    if (isNotEmpty()){
                        nativeList.clear()
                        for (i in this){
                            if (i.isDeleteStatus){
                                nativeList.add(i)
                            }
                        }
                        val oldGeDanListData = loadGeDanData(GeDanData())
                        for (index in oldGeDanListData.indices){
                            if (oldGeDanListData[index].geDanId == gedanId){
                                for (j in nativeList){
                                    oldGeDanListData[index].musicList.add(j)
                                }
                                break
                            }
                        }
                        createGeDanData(oldGeDanListData)

                        startInterActivity(Intent(this@AppAddList, AppGeDanDetail::class.java).apply {
                            putExtra("det_type",1)
                            putExtra("gedan_id",gedanId)
                        }, geDanDetInter())
                    }
                }
                return@setOnClickListener
            }
            // first add  data
            playAdapter?.getCreateData()?.apply {
                if (isNotEmpty()){
                    nativeList.clear()
                    for (i in this){
                        if (i.isDeleteStatus){
                            nativeList.add(i)
                        }
                    }
                    val oldGeDanListData = loadGeDanData(GeDanData())
                                                        for (index in oldGeDanListData.indices){
                                        if (oldGeDanListData[index].geDanId == gedanId){
                                            oldGeDanListData[index].musicList = nativeList
                                            break
                                        }
                                    }
                                    createGeDanData(oldGeDanListData)
                    startInterActivity(Intent(this@AppAddList, AppGeDanDetail::class.java).apply {
                        putExtra("det_type",1)
//                putExtra("create",true)
                        putExtra("gedan_id",gedanId)
                    }, geDanDetInter())

            }
        }
        }
        playListBinding.selectBg.setOnClickListener {
            selectAll()
        }
        playListBinding.selectTxt.setOnClickListener {
            selectAll()
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
                        initRecyleList()
                        checkEmpty()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }
        }else{
            initRecyleList()
        }

      checkEmpty()
    }
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            addSongsBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("addSongsBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("addSongsBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(addSongsBanner())) {
                LogUtils.e("addSongsBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(addSongsBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("addSongsBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("addSongsBanner  ban show  suc------")
            playListBinding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(addSongsBanner())){
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
    private fun selectAll() {
        isSelect = !isSelect
        if (isSelect) {
            playListBinding.selectBg.setImageResource(R.mipmap.ivg_select)
        } else {
            playListBinding.selectBg.setImageResource(R.mipmap.ivg_un_select)
        }
        playAdapter?.upCreateData(isSelect)
    }

    // 将时间戳转成字符串
    fun getDateToString(time: Long): String {
        val d: Date = Date(time)
        return   SimpleDateFormat("yyyy-MM-dd").format(d)
    }
    private  fun checkPermission() {
        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
            .request(object : OnPermissionCallback {
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
        if (musicArrayList.isEmpty()){
            playListBinding.bottomMusic.visibility = View.GONE
            playListBinding.recycleHislist.visibility = View.GONE
            playListBinding.noContentPic.visibility = View.VISIBLE
        }else{
            playListBinding.recycleHislist.visibility = View.VISIBLE
            playListBinding.noContentPic.visibility = View.GONE
            playListBinding.bottomMusic.visibility = View.VISIBLE
        }
    }
  
    private val musicDao = DaoFactory.getDao(Music::class.java)

    override fun onResume() {
        super.onResume()
    showBanners()
    }

    private fun initRecyleList() {
        try {
            currentBean?.musicList?.forEach {
                it.isDeleteStatus = false
            }
            musicArrayList.clear()

            musicDao.selectAll()?.apply{
                if (currentBean != null && currentBean?.musicList?.isNotEmpty() == true){
                    for (i in this){
                        if (!currentBean!!.musicList.contains(i)){
                            musicArrayList.add(i)
                        }
                    }

                }else{
                    forEach {
                        musicArrayList.add(it)
                    }
                }
            }
            playAdapter = AddListAdapter(musicArrayList,4,this@AppAddList,this)
            playListBinding.recycleHislist.run {
                layoutManager = LinearLayoutManager(this@AppAddList)
                adapter = playAdapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun refresh() {
        if (isFinishing || isDestroyed){
            return
        }
        try {

        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}