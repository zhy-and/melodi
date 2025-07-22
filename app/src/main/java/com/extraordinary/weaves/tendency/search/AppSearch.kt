package com.extraordinary.weaves.tendency.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.extraordinary.weaves.tendency.AppGeDanList
import com.extraordinary.weaves.tendency.AppStyleList
import com.extraordinary.weaves.tendency.albums.AlbumListAdapter
import com.extraordinary.weaves.tendency.artist.ArtListAdapter
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.manager.MediaManager
import com.extraordinary.weaves.tendency.core.MusicScanner
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.tracks.TrackList
import com.extraordinary.weaves.tendency.tracks.TrackListAdapter
import com.extraordinary.weaves.tendency.albums.AppAlbumList
import com.extraordinary.weaves.tendency.artist.AppArtistList
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.SearchViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import dora.db.dao.DaoFactory
import dora.http.DoraHttp.net
import dora.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.CommonWeavesStyle
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.MESSAGE_MSG_CODE
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.searchResultBanner
import com.extraordinary.interstings.studios.utils.searchResultBanner
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListInter
import com.extraordinary.weaves.tendency.dialog.event_one_re_album
import com.extraordinary.weaves.tendency.dialog.event_th_albums_no
import com.extraordinary.weaves.tendency.dialog.event_th_artists_no
import com.extraordinary.weaves.tendency.dialog.event_th_playlists_no
import com.extraordinary.weaves.tendency.dialog.event_th_styles_no
import com.extraordinary.weaves.tendency.dialog.event_th_tracks_no
import com.extraordinary.weaves.tendency.dialog.event_two_search_ok
import com.extraordinary.weaves.tendency.dialog.event_two_style_click
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.google.android.gms.ads.nativead.NativeAd
import dora.util.TextUtils
import dora.util.ToastUtils
import java.util.concurrent.Executors


class AppSearch : baseActivity(){
    override fun onResume() {
        super.onResume()
        showBanners()
    }
    private var isFlag: Boolean = false
    private lateinit var  binding : SearchViewBinding
    private fun showBanners() {
        val normalBan: NativeAd? = MainHomeStudy.homeStudy.showWeaveNatives(
            searchResultBanner(), this)
        if (normalBan == null) {
            if (CommonStudyUtils.interWeavesIsShowing) {
                LogUtils.e("searchResultBanner  ban inter is showing return------")
                return
            }
            if (isFinishing || isDestroyed) {
                LogUtils.e("searchResultBanner  ban destroy  return------")
                return
            }
            if (CommonStudyUtils.posIsCanShow(searchResultBanner())) {
                LogUtils.e("searchResultBanner  ban limit  return------")
                return
            }
            MainHomeStudy.homeStudy.initLoadBanners(searchResultBanner(),object :
                NativeWeavesListener {
                override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    if (sucFlag) {
                        LogUtils.e("searchResultBanner  ban load  suc------")
                        showBanners()
                    }
                }
            })
        } else {
            LogUtils.e("searchResultBanner  ban show  suc------")
            binding?.homeBans?.let {
                it.visibility = View.VISIBLE
                it.setStyles(CommonWeavesStyle.Builder().build())
                it.setNativeAd(normalBan)
                MainHomeStudy.homeStudy.loadButtons(it)
                if(!CommonStudyUtils.posIsCanShow(searchResultBanner())){
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(searchInter(),this)
        enableEdgeToEdge()
        ImmersionBar.with(this)
            .transparentNavigationBar()
            .init()
        binding = SearchViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.titleContent.text = "Search"
        binding.title.backBtn.setOnClickListener { speFinish() }
        mediaManager = GlobalMusApp.instance!!.mediaManager!!
        if (!MMKV.defaultMMKV().getBoolean("is_first_auth",false)){
//            checkPermission()
        }
//        else{
            initView()
//        }
        binding.rlTrack.setOnClickListener {
            startInterActivity(Intent(this, TrackList::class.java),trackListInter())
            logMusicEvents(event_th_tracks_no)
        }
        binding.rlAlbums.setOnClickListener {
            startInterActivity(Intent(this, AppAlbumList::class.java), albumListInter())
            logMusicEvents(event_th_albums_no)
        }
        binding.rlArtist.setOnClickListener {
            startInterActivity(Intent(this, AppArtistList::class.java), artistListInter())
            logMusicEvents(event_th_artists_no)

        }
        binding.rlStyles.setOnClickListener {
            startInterActivity(Intent(this, AppStyleList::class.java), styleListInter())
            logMusicEvents(event_th_styles_no)

        }

        binding.rlPlayList.setOnClickListener {
            startInterActivity(Intent(this, AppGeDanList::class.java), geDanListInter())
            logMusicEvents(event_th_playlists_no)

        }
        binding.searchBtns.setOnClickListener {
            logMusicEvents(event_two_search_ok)

            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }
            if (TextUtils.isEmpty(binding.searchEdit.text.toString())){
                    ToastUtils.showShort("Please input content")
                return@setOnClickListener
                }
            if (isFlag) {
                ToastUtils.showLong(R.string.permission_never_denyd)
            }
            searchTracklist.clear()
            val allmusic =  musicDao.selectAll()
            for (i in allmusic){
                if (i.musicName.lowercase().contains(binding.searchEdit.text.toString().lowercase())){
                    searchTracklist.add(i)
                }
            }

            searchAlbumslist.clear()
            val allAlbum =  album.selectAll()
            for (i in allAlbum){
                if (i.album_name.lowercase().contains(binding.searchEdit.text.toString().lowercase())){
                    searchAlbumslist.add(i)
                }
            }

            artistList.clear()
            val allArtist =  art.selectAll()
            for (i in allArtist){
                if (i.name.lowercase().contains(binding.searchEdit.text.toString().lowercase())){
                    artistList.add(i)
                }
            }
            trackListAdapter?.notifyDataSetChanged()
            artListAdapter?.notifyDataSetChanged()
            albumListAdapter?.notifyDataSetChanged()

            if (searchTracklist.isEmpty() && searchAlbumslist.isEmpty() && artistList.isEmpty()){
                binding.emptySearch.visibility = View.VISIBLE
                binding.allResult.visibility = View.GONE
            }else{
                if (searchTracklist.isEmpty()){
                    binding.tracksSe.visibility = View.GONE
                    binding.recycleTrackList.visibility = View.GONE
                }else{
                    binding.tracksSe.visibility = View.VISIBLE
                    binding.recycleTrackList.visibility = View.VISIBLE
                }
                if (searchAlbumslist.isEmpty()){
                    binding.albumsSe.visibility = View.GONE
                    binding.recycleAlbumList.visibility = View.GONE
                }else{
                    binding.albumsSe.visibility = View.VISIBLE
                    binding.recycleAlbumList.visibility = View.VISIBLE
                }
                if (artistList.isEmpty()){
                    binding.recycleArtList.visibility = View.GONE
                    binding.artists.visibility = View.GONE
                }else{
                    binding.recycleArtList.visibility = View.VISIBLE
                    binding.artists.visibility = View.VISIBLE
                }
                binding.emptySearch.visibility = View.GONE
                binding.allResult.visibility = View.VISIBLE
            }
        }
    }
    val searchTracklist = arrayListOf<Music>()
    val searchAlbumslist = arrayListOf<Album>()
    val artistList = arrayListOf<Artist>()
    private  var albumListAdapter: AlbumListAdapter?= null
    private  var artListAdapter: ArtListAdapter?= null

    private fun initView() {
        trackListAdapter = TrackListAdapter(searchTracklist, 3, this@AppSearch)
        binding.recycleTrackList.run {
            layoutManager = LinearLayoutManager(this@AppSearch)
            adapter = trackListAdapter
        }
        albumListAdapter = AlbumListAdapter(searchAlbumslist,this@AppSearch,3)
        binding.recycleAlbumList.run {
            layoutManager = LinearLayoutManager(this@AppSearch)
            adapter = albumListAdapter
        }
        artListAdapter = ArtListAdapter(artistList,this@AppSearch,3)
        binding.recycleArtList.run {
            layoutManager = LinearLayoutManager(this@AppSearch)
            adapter = artListAdapter
        }
    }
    private  var trackListAdapter: TrackListAdapter?= null


    /**
     * 扫描歌曲。
     */
    fun scanMusic1() {
//        val builder = QueryBuilder.create().where(
//            WhereBuilder.create()
//                .addWhereEqualTo(Music.COLUMN_FAVORITE, 1))
//        val favoriteCount = DaoFactory.getDao(Music::class.java).count(builder)
//        if (favoriteCount > 0) { //有收藏的歌曲
//        } else {
            scanMusic()
//        }
    }
    /**
     * 扫描歌曲。
     */
    private fun scanMusic() {
        net {
//            val dialog = DoraLoadingDialog(this).show("正在扫描...") {
//                setCancelable(false)
//                setCanceledOnTouchOutside(false)
//            }
//            DoraHttp.request {
            Executors.newCachedThreadPool().submit {
                try {
                    val playlist = MusicScanner.scan(this@AppSearch) as MutableList<Music>
                    for (i in playlist){
                        i.albumId
                        LogUtils.e(""+i.songId+"---"+i.albumId+"name ="+i.musicName+"dura ="+i.duration+"art ="+i.artist)
                    }
                    GlobalMusApp.instance!!.mediaManager!!.refreshPlaylist(playlist)
                } finally {
//                        it.releaseLock(th)
                }
            }
//            }
//            homeFragment.onRefreshLocalMusic()
//            dialog.dismiss()
        }
    }

  private  fun checkPermission() {
        XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
            .request(object :OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (allGranted) {
                        MMKV.defaultMMKV().getBoolean("is_first_auth",true)
                        scanMusic1()
                    }
                }
                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        isFlag = true
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