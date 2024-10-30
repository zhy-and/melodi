package com.extraordinary.weaves.tendency.scan

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.extraordinary.weaves.tendency.core.MusicScanner
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.databinding.ScanPageBinding
import com.extraordinary.weaves.tendency.prepareSongs
import com.extraordinary.weaves.tendency.dialog.showComDialog
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.interstings.studios.utils.settingsInter
import dora.util.ThreadUtils
import dora.util.ToastUtils
import java.util.concurrent.Executors


class ScanPage : baseActivity() {
    private lateinit var  binding : ScanPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(scansInter(),this)

        enableEdgeToEdge()
        ImmersionBar.with(this)
            .transparentNavigationBar()
            .init()
        binding = ScanPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.titleContent.text = "Scan Music"
        binding.title.backBtn.setOnClickListener { speFinish() }
        binding.scan.setOnClickListener {
            initPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.music.cancelAnimation()
    }

    private fun initPermission() {
            XXPermissions.with(this).permission(Permission.READ_MEDIA_AUDIO)
                .request(object :OnPermissionCallback{
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (allGranted) {
                            MMKV.defaultMMKV().putBoolean("is_first_auth",true)
                            binding.music.visibility = View.VISIBLE
                            binding.scan.text = "Scanning"
                            binding.scanStart.visibility = View.GONE
                            binding.scan.setBackgroundResource(R.drawable.feed_grey_btn_shape)
                            binding.scan.isEnabled = false
                            performScanMusic()
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




    /**
     * 扫描歌曲。
     */
    fun performScanMusic() {
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
        try {
            Executors.newCachedThreadPool().submit {
                try {
                    val playlist = MusicScanner.scan(this@ScanPage) as MutableList<Music>
                    for (i in playlist){
                        LogUtils.e(""+i.songId+"---"+i.albumId+"name ="+i.musicName+"dura ="+i.duration+"art ="+i.artist)
                    }
                    GlobalMusApp.instance!!.mediaManager!!.refreshPlaylist(playlist)
                    prepareSongs(playlist)
                } finally {
                }
            }
            ThreadUtils.runOnUiThreadDelayed({
                binding.scan.isEnabled = true
                binding.music.visibility = View.GONE
                binding.scanStart.visibility = View.VISIBLE
                binding.scan.setBackgroundResource(R.drawable.feed_btn_shape)
                binding.scan.text = "Start"
                showComDialog(this, R.layout.pop_add,false,Gravity.CENTER).also {
                    it.findViewById<View>(R.id.btn_feed)?.setOnClickListener {
                        finish() }
                }
            },2000)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }




}