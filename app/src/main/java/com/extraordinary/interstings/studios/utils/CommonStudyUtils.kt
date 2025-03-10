package com.extraordinary.interstings.studios.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.tencent.mmkv.MMKV
import java.util.Calendar

object CommonStudyUtils  {


    fun showCountAdd(postion: Long) {
        mmkvShowRules(postion)?.run {
            var showCount = getInt("$MUSIC_CONSTANT_BD_SHOW_CT-${postion}", 0)
            showCount += 1
            LogUtils.e(basicInfoLog(postion)+"show  count ====="+showCount)
            putInt("$MUSIC_CONSTANT_BD_SHOW_CT-${postion}", showCount)
        }
    }

    fun appendClick(pos: Long) {
        mmkvClickRules(pos)?.run {
            var count = getInt("$MUSIC_CONSTANT_BD_CLICK_CT-${pos}", 0)
            count += 1
            LogUtils.e(basicInfoLog(pos)+" click count ====="+count)
            putInt("$MUSIC_CONSTANT_BD_CLICK_CT-${pos}", count)
        }
    }


    fun checkTimes(cacheTime : Long): Boolean {
        if (System.currentTimeMillis() - cacheTime   > 60 * 60 * 1000) {
            return true
        }
        return false
    }

    fun getInstallSys(): Long {
        try {
            val context = GlobalMusApp.instance ?: return 0
            val packageManager = context.packageManager
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(AppUtils.getAppPackageName(), 0)
            //应用装时间
            return packageInfo.firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }
    private fun mmkvShowRules(postion: Long): MMKV? {
        val mmkv = MMKV.mmkvWithID("txt_translate_mmkv_sp_show$postion")
        var net_show_unit =  MMKV.mmkvWithID("mmkv_music_app").getString("$MUSIC_CONSTANT_JK_SHOW_UNIT-${postion}","day")
        when(net_show_unit){
            "day" -> {

                val currentD = Calendar.getInstance().get(Calendar.DATE)
                val lastD = mmkv.getInt("txt_translate_mmkv_sp_show$postion", 0)
                if (currentD != lastD){
                    mmkv.clear()
                }
                mmkv.putInt("txt_translate_mmkv_sp_show$postion", currentD)
            }
            "hour" -> {
                val currentD = Calendar.getInstance().get(Calendar.HOUR)
                val lastD = mmkv.getInt("txt_translate_mmkv_sp_show$postion", 0)
                if (currentD != lastD){
                    mmkv.clear()
                }
                mmkv.putInt("txt_translate_mmkv_sp_show$postion", currentD)
            }
        }
        return mmkv
    }
    fun interIsCanLoad(): Boolean {
        return !posIsCanShow(searchInter())
                || !posIsCanShow(settingsInter())
                || !posIsCanShow(scansInter())
                || !posIsCanShow(finishPageInterOne())
                || !posIsCanShow(finishPageInterTwo())
                || !posIsCanShow(feedBacksInter())
                || !posIsCanShow(privacysInter())
                || !posIsCanShow(trackListInter())
                || !posIsCanShow(styleListInter())
                || !posIsCanShow(styleDetInter())
                || !posIsCanShow(albumListInter())
                || !posIsCanShow(albumDetInter())
                || !posIsCanShow(artistListInter())
                || !posIsCanShow(artistDetInter())
                || !posIsCanShow(geDanListInter())
                || !posIsCanShow(geDanDetInter())
                || !posIsCanShow(addSongsInter())
                || !posIsCanShow(playViewsInter())
                || !posIsCanShow(lyricInter())
                || !posIsCanShow(queueInter())
    }

    fun posIsCanShow(artPos: Long): Boolean {
        val weavesMuslateAnyClickCount =   MMKV.mmkvWithID("mmkv_music_app").getInt("$MUSIC_CONSTANT_JK_CLICK_CT-${artPos}",0)
        val weavesMuslateAnyShowCount = MMKV.mmkvWithID("mmkv_music_app").getInt("$MUSIC_CONSTANT_JK_SHOW_CT-${artPos}",0)
        val weavesMuslateAnyShow = MMKV.mmkvWithID("mmkv_music_app").getBoolean("$MUSIC_CONSTANT_JK_SHOW_ST-${artPos}",false)
        LogUtils.e(basicInfoLog(artPos)+"配置开关"+weavesMuslateAnyShow)
        if (!weavesMuslateAnyShow) {
            return true
        }
        mmkvClickRules(artPos)?.run {
            val nativeTranslateAnyClick =  getInt("$MUSIC_CONSTANT_BD_CLICK_CT-${artPos}",0)
            LogUtils.e(basicInfoLog(artPos)+"本地累积点击次数"+nativeTranslateAnyClick)
            if (nativeTranslateAnyClick >= weavesMuslateAnyClickCount) {
                return true
            }
        }
        mmkvShowRules(artPos)?.run {
            val nativeTranslateAnyShow = getInt("$MUSIC_CONSTANT_BD_SHOW_CT-${artPos}",0)
            LogUtils.e(basicInfoLog(artPos)+"本地累积展示次数"+nativeTranslateAnyShow)
            if (nativeTranslateAnyShow >= weavesMuslateAnyShowCount) {
                return true
            }
        }
        return false
    }

    private fun mmkvClickRules(postion: Long): MMKV? {
        val mmkv = MMKV.mmkvWithID("txt_translate_mmkv_sp_click$postion")
        var net_click_unit =   MMKV.mmkvWithID("mmkv_music_app").getString("$MUSIC_CONSTANT_JK_CLICK_UNIT-${postion}","day")
        when(net_click_unit){
            "day" -> {

                val currentD = Calendar.getInstance().get(Calendar.DATE)
                val lastD = mmkv.getInt("txt_translate_mmkv_sp_click$postion", 0)
                if (currentD != lastD){
                    mmkv.clear()
                }
                mmkv.putInt("txt_translate_mmkv_sp_click$postion", currentD)
            }
            "hour" -> {
                val currentD = Calendar.getInstance().get(Calendar.HOUR)
                val lastD = mmkv.getInt("txt_translate_mmkv_sp_click$postion", 0)
                if (currentD != lastD){
                    mmkv.clear()
                }
                mmkv.putInt("txt_translate_mmkv_sp_click$postion", currentD)
            }
        }
        return mmkv
    }

    @JvmStatic
    var interWeavesIsShowing = false
    @JvmStatic
    var weaveLauStatus = false

    fun basicInfoLog(position: Long): String {
        var name = ""
        when (position) {
            homeBanner() -> {name = "homeBanner weavesMus pos :"}
            searchResultBanner() -> {name ="searchResultBanner weavesMus pos :"}
            trackListBanner() -> { name = "trackListBanner weavesMus pos :"}
            styleListBanner() -> { name = " styleListBanner "}
            styleDetBanner() -> {name = "styleDetBanner weavesMus pos :"}
            albumListBanner() -> {name ="albumListBanner weavesMus pos :"}
            albumDetBanner() -> {name ="albumDetBanner weavesMus pos :"}
            artistListBanner() -> {name ="artistListBanner weavesMus pos :"}
            artistDetBanner() -> {name ="artistDetBanner weavesMus pos :"}
            geDanListBanner() -> {name ="geDanListBanner weavesMus pos :"}
            geDanDetBanner() -> {name ="geDanDetBanner weavesMus pos :"}
            addSongsBanner() -> {name ="addSongsBanner weavesMus pos :"}
            playViewBanner() -> {name ="playViewBanner weavesMus pos :"}
            lyricBanner() -> {name ="lyricBanner weavesMus pos :"}
            queueListBanner() -> {name ="queueListBanner weavesMus pos :"}
            searchInter() -> {name ="searchInter weavesMus pos :"}
            settingsInter() -> {name ="settingsInter weavesMus pos :"}
            scansInter() -> {name ="scansInter weavesMus pos :"}
            feedBacksInter() -> {name ="feedBacksInter weavesMus pos :"}
            privacysInter() -> {name ="privacysInter weavesMus pos :"}
            trackListInter() -> {name ="trackListInter weavesMus pos :"}
            styleListInter() -> {name ="styleListInter weavesMus pos :"}
            styleDetInter() -> {name ="styleDetInter weavesMus pos :"}
            albumListInter() -> {name ="albumListInter weavesMus pos :"}
            albumDetInter() -> {name ="albumDetInter weavesMus pos :"}
            artistListInter() -> {name ="artistListInter weavesMus pos :"}
            artistDetInter() -> {name ="artistDetInter weavesMus pos :"}
            geDanListInter() -> {name ="geDanListInter weavesMus pos :"}
            geDanDetInter() -> {name ="geDanDetInter weavesMus pos :"}
            addSongsInter() -> {name ="addSongsInter weavesMus pos :"}
            playViewsInter() -> {name ="playViewsInter weavesMus pos :"}
            lyricInter() -> {name ="lyricInter weavesMus pos :"}
            queueInter() -> {name ="queueInter weavesMus pos :"}
            finishPageInterOne() -> {name ="finishPageInterOne weavesMus pos :"}
            finishPageInterTwo() -> {name ="finishPageInterTwo weavesMus pos :"}
            musColdStart() -> {name ="weavesMusKepCold weavesMus pos :"}
            musHotStart() -> {name ="weavesMusKepHot weavesMus pos :"}
        }
        return name
    }

    fun weavesIsCanLoadStart(): Boolean {
        return !posIsCanShow(musColdStart())
                || !posIsCanShow(musHotStart())
    }



}