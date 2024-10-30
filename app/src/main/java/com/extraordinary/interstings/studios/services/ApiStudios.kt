package com.extraordinary.interstings.studios.services

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.beans.CommonViewModel
import com.extraordinary.interstings.studios.beans.ProductBean
import com.extraordinary.interstings.studios.goodads.GoogleMobileAdsConsentManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.CommonTools.getCountryInfo
import com.extraordinary.interstings.studios.utils.CommonTools.loadIds
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_CACHE_TM
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_DATA
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_APP_CACHE_TM
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_IS_AUTO_PLAY
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.workmanager.CommonRequestTools
import com.extraordinary.interstings.studios.workmanager.TransSpesInters
import com.extraordinary.weaves.tendency.AppStart.Companion.ouUserIsCallBack
import com.extraordinary.weaves.tendency.HomeActivity
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.application.GlobalMusApp.Companion.referStatusCallBack
import com.extraordinary.weaves.tendency.baseActivity
import com.google.android.gms.ads.AdValue
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

class ApiStudios {
    companion object{
        @JvmStatic
        fun commonConfig(): String {
            val ctx = GlobalMusApp.instance ?: return ""
            return ctx.getString(R.string.uni_y)
        }

        private var  common_basic_info=
            "${commonConfig()}/centre/bench/module/edd/getAdvs?qydyve=${
                loadIds(
                    GlobalMusApp.instance
                )
            }&fkossfbtweikm=${commonUniIds()}"

        fun loadInsTimes(): Long {
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
        var value_info =
            "${commonConfig()}/shard/ground/weblogic/edc/logBook/saving"


        val app_info =
            "${commonConfig()}/sys/bottom/weblogic/vers/plan/querys?olzjqsbbha=${loadIds(
                GlobalMusApp.instance
            )}&hlebvallupzd=${commonUniIds()}"


        @JvmStatic
        fun commonKys(): String {
            val ctx = GlobalMusApp.instance ?: return ""
            return ctx.getString(R.string.uni_ky)
        }

        @JvmStatic
        fun commonUniIds(): String {
            val ctx = GlobalMusApp.instance ?: return ""
            return ctx.getString(R.string.uni_id)

        }

        fun loadBaseInfos(view: baseActivity, flag : Boolean = false) {
            if (System.currentTimeMillis() - MMKV.defaultMMKV()
                    .getLong(MUSIC_CONSTANT_APP_AD_CACHE_TM, 0) > 60 * 60 * 1000
            ) {
                CommonRequestTools.get().requestConfigInfo(
                    HashMap(),
                    common_basic_info,
                    object : TransSpesInters.BasicRequestListener {

                        override fun loadFailed(message: String) {
                        }

                        override fun loadSuccessConfig(configInfo: String) {
                            try {
                                Gson().fromJson(
                                    configInfo,
                                    CommonViewModel::class.java
                                )?.run {
                                    if (yvbgjmfbteobho != null && yvbgjmfbteobho.xgyvsjeazyn != null && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa != null
                                        &&  yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu != null &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu.isNotEmpty() &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr != null &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.szvrcilxij == 200) {
                                        if (yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.uiiugudcealz.isNullOrEmpty()) {
                                            if (!flag){
                                                musToMain(view)
                                            }
                                            return@run
                                        } else {
                                            LogUtils.e("vense succcc","vense suc ----- $configInfo")
                                            MMKV.defaultMMKV()
                                                .putString(MUSIC_CONSTANT_APP_AD_DATA, configInfo)
                                            MMKV.defaultMMKV().putLong(
                                                MUSIC_CONSTANT_APP_AD_CACHE_TM,
                                                System.currentTimeMillis()
                                            )
                                        }
                                        MainHomeStudy.homeStudy.initMusConfig(view,flag)
                                    } else {
                                        if (!flag){
                                            musToMain(view)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                MMKV.defaultMMKV().putLong(MUSIC_CONSTANT_APP_AD_CACHE_TM, 0)
                                e.printStackTrace()
                            }

                        }
                    })
            } else {
                if (!flag){
                    MainHomeStudy.homeStudy.initMusConfig(view)
                }
            }
            getProductInfos()
        }



        private fun jsonInfo(value: AdValue, adId: String, adsType: String): String? {
            val catPa: HashMap<String, String> = java.util.HashMap()
            try {
                catPa["ukpftne"] = adId.replace("/","-")
                catPa["hpcglvecibwfy"] =  "Show"
                catPa["qsnfwhubrfsw"] = if(CommonStudyUtils.weaveLauStatus)"Cold" else("Warm")
                catPa["ilerobbxgh"] = adsType
                catPa["icfntudf"] = value.valueMicros.toString()
                catPa["xdwemjn"] = value.currencyCode
                catPa["lwuhpkcjpyv"] = value.precisionType.toString()
                catPa["myxntynkf"] =  getCountryInfo(
                    GlobalMusApp.instance
                ).lowercase()
                return catPa.toString()
            }catch (e : Exception){
                e.printStackTrace()
            }

            return catPa.toString()
        }

        fun handleValues(adsId: String, type: String, value: AdValue){
            try {
                CommonRequestTools.get().upValues(
                    stringHashMap(adsId, type, value),
                    value_info,
                    object : TransSpesInters.BasicRequestListener {

                        override fun loadFailed(message: String) {
                            LogUtils.e(" message",message)

                        }

                        override fun loadSuccessConfig(configInfo: String) {
                            LogUtils.e(" message",configInfo)

                        }


                    })
            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        private fun stringHashMap(
            adsId: String,
            adsType: String,
            value: AdValue
        ): HashMap<String, String> {
            val stringStringHashMap: HashMap<String, String> = java.util.HashMap()
            try {
                stringStringHashMap["dppkptxndmx"] = AppUtils.getAppName()
                stringStringHashMap["sikrappt"] = "Android"
                stringStringHashMap["trwzbv"] = Build.MODEL
                stringStringHashMap["tkrexyg"] = "10001"
                stringStringHashMap["ezepfefmz"] = ""+System.currentTimeMillis()
                stringStringHashMap["dvgifjyicaj"] = ""+System.currentTimeMillis()
                stringStringHashMap["dunlda"] = ActivityUtils.getTopActivity().localClassName
                stringStringHashMap["lrrlox"] = MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO,"")+""
                stringStringHashMap["tjlgkz"] =  JsonUtils.formatJson(jsonInfo(value,adsId,adsType))
                stringStringHashMap["znddwtnxae"] = commonUniIds()
                stringStringHashMap["vmafwyxxq"] = "" +   loadIds(
                    GlobalMusApp.instance
                )
                stringStringHashMap["njnfqva"] = Build.VERSION.RELEASE
                stringStringHashMap["pzhdjs"] = Build.MODEL
                stringStringHashMap["cywuhlkmm"] = AppUtils.getAppVersionName()
                LogUtils.e(stringStringHashMap.toString())
                return stringStringHashMap
            }catch (e : Exception){
                e.printStackTrace()
            }
            return stringStringHashMap
        }


        fun musToMain(infoCtx: baseActivity?) {
            try {
                if (infoCtx == null || infoCtx.isFinishing || infoCtx.isDestroyed){
                    return
                }
                if ((!ouUserIsCallBack && !GoogleMobileAdsConsentManager.getInstance(
                        GlobalMusApp.instance
                    ).canRequestAds())
                ){
                    LogUtils.e("---- canRequestAds ---- error")
                    return
                }
                infoCtx.runOnUiThread {
                    infoCtx.startActivity(Intent(infoCtx, HomeActivity::class.java))
                    infoCtx.finish()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        fun referInfoLoads(view: baseActivity, flag : Boolean = false) {
            if (System.currentTimeMillis() - MMKV.defaultMMKV()
                    .getLong(MUSIC_CONSTANT_APP_AD_CACHE_TM, 0) > 60 * 60 * 1000
                || referStatusCallBack) {
                referStatusCallBack = false
                CommonRequestTools.get().referTranslateInfo(
                    HashMap(),
                    common_basic_info,
                    object : TransSpesInters.BasicRequestListener {

                        override fun loadFailed(message: String) {
                        }

                        override fun loadSuccessConfig(configInfo: String) {
                            try {
                                Gson().fromJson(
                                    configInfo,
                                    CommonViewModel::class.java
                                )?.run {
                                    LogUtils.e("refer pares before","vense suc ----- $configInfo")

                                    if (yvbgjmfbteobho != null && yvbgjmfbteobho.xgyvsjeazyn != null && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa != null
                                        &&  yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu != null &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu.isNotEmpty() &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr != null &&
                                        yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.szvrcilxij == 200) {
                                        cleanBasicInfo()
                                        if (yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.uiiugudcealz.isNullOrEmpty()) {
                                            if (!flag){
                                                musToMain(view)
                                            }
                                            return@run
                                        } else {
                                            LogUtils.e("data suc ----- $configInfo")
                                            MMKV.defaultMMKV()
                                                .putString(MUSIC_CONSTANT_APP_AD_DATA, configInfo)
                                            MMKV.defaultMMKV().putLong(
                                                MUSIC_CONSTANT_APP_AD_CACHE_TM,
                                                System.currentTimeMillis()
                                            )
                                        }
                                        MainHomeStudy.homeStudy.initMusConfig(view,flag)
                                    } else {
                                        if (!flag){
                                            musToMain(view)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                MMKV.defaultMMKV().putLong(MUSIC_CONSTANT_APP_AD_CACHE_TM, 0)
                                e.printStackTrace()
                            }

                        }
                    })
            } else {
                if (!flag){
                    MainHomeStudy.homeStudy.initMusConfig(view)
                }
            }
            getProductInfos()
        }

        private fun cleanBasicInfo() {
            MMKV.mmkvWithID("mmkv_music_app").clear()
            MMKV.defaultMMKV()
                .putString(MUSIC_CONSTANT_APP_AD_DATA, "")
            MMKV.defaultMMKV().putLong(
                MUSIC_CONSTANT_APP_AD_CACHE_TM,
                0
            )
        }

        private fun getProductInfos() {
            if (System.currentTimeMillis() - MMKV.defaultMMKV()
                    .getLong(MUSIC_CONSTANT_APP_APP_CACHE_TM, 0) > 60 * 60 * 1000
            ) {
                CommonRequestTools.get().requestConfigInfo(
                    HashMap(),
                    app_info,
                    object : TransSpesInters.BasicRequestListener {

                        override fun loadFailed(message: String) {
                            LogUtils.e(message)

                        }

                        override fun loadSuccessConfig(configInfo: String) {
                            try {
                                LogUtils.e(configInfo)
                                Gson().fromJson(
                                    configInfo,
                                    ProductBean::class.java
                                )?.run {
                                    LogUtils.e("product info before","product ----- $configInfo")
                                    if (yvbgjmfbteobho != null && yvbgjmfbteobho.xgyvsjeazyn != null && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa != null
                                        && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu.isNotEmpty()
                                        && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr != null
                                        && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.dluulegqxfu  == 200
                                        ) {
                                        if (yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.qhsjpca != null) {
                                            MMKV.defaultMMKV()
                                                .putInt(MUSIC_CONSTANT_APP_REFRESH, yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.qhsjpca.wohgaosfhzn)
                                            MMKV.defaultMMKV().putLong(
                                                MUSIC_CONSTANT_APP_APP_CACHE_TM,
                                                System.currentTimeMillis()
                                            )
                                            yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.qhsjpca.zrwtzmvkj?.apply {
                                                if (isNullOrEmpty()){
                                                    return@apply
                                                }
                                                for (i in this){
                                                    if ("music_auto_play" == i.lddmfgjtn){
                                                        MMKV.defaultMMKV()
                                                            .putBoolean(
                                                                MUSIC_CONSTANT_APP_IS_AUTO_PLAY, i.boifrtru == "1")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                MMKV.defaultMMKV().putLong(MUSIC_CONSTANT_APP_APP_CACHE_TM, 0)
                                e.printStackTrace()
                            }

                        }
                    })
            }
        }


    }

}