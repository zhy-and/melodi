package com.extraordinary.interstings.studios.services

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.beans.CommonViewModel
import com.extraordinary.interstings.studios.beans.ProductBean
import com.extraordinary.interstings.studios.goodads.GoogleMobileAdsConsentManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.CommonStudyUtils.getInstallSys
import com.extraordinary.interstings.studios.utils.CommonTools.getCountryInfo
import com.extraordinary.interstings.studios.utils.CommonTools.loadIds
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_CACHE_TM
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_DATA
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_APP_CACHE_TM
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_IS_AUTO_PLAY
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFRESH
import com.extraordinary.interstings.studios.utils.MMKV_COEFS
import com.extraordinary.interstings.studios.utils.MMKV_SYS
import com.extraordinary.interstings.studios.utils.MMKV_LINES
import com.extraordinary.interstings.studios.utils.musColdStart
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

        var value_info =
            "${commonConfig()}/muanpw/ozzyj/csqrub/rendition2/umnvx"


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



        private fun jsonInfo(value: AdValue, adId: String, adsType: String, fbIpValue: Double,pos: Long): String? {
            val catPa: HashMap<String, String> = java.util.HashMap()
            try {
                catPa["xukqep"] = adId.replace("/","-")
                catPa["daah"] =  "Show"
                catPa["snbg"] = if(CommonStudyUtils.weaveLauStatus)"Cold" else("Warm")
                catPa["ehtpzm"] = adsType
                catPa["acxv"] = pos.toString()
                catPa["miu"] = value.valueMicros.toString()
                catPa["rbto"] = value.currencyCode
                catPa["ctacw"] = value.precisionType.toString()
                catPa["dbbi"] =  getCountryInfo(
                    GlobalMusApp.instance
                ).lowercase()
                catPa["zwh"] = "AdMob"

                catPa["musicFInstall"] =  getInstallSys().toString()
                catPa["musicFValue"] = fbIpValue.toString()
                catPa["musicFOpens"] = MMKV.defaultMMKV().getLong("first_open",0L).toString()
                catPa["musicPastTm"] = ((System.currentTimeMillis() - getInstallSys()) / 1000).toString()

                return catPa.toString()
            }catch (e : Exception){
                e.printStackTrace()
            }

            return catPa.toString()
        }

        private fun lauArgs(): String? {
            val catPa: HashMap<String, String> = java.util.HashMap()
            try {
                catPa["snbg"] = if(CommonStudyUtils.weaveLauStatus)"Cold" else("Warm")
                return catPa.toString()
            }catch (e : Exception){
                e.printStackTrace()
            }

            return catPa.toString()
        }

        fun handleValues(adsId: String, type: String, value: AdValue, fbIpValue: Double,pos: Long){
            try {
                CommonRequestTools.get().upValues(
                    stringHashMap(adsId, type, value, fbIpValue,pos),
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


        private fun installEventsUpload(){
            try {
                if (!MMKV.defaultMMKV().getBoolean("install_event",false)){
                    CommonRequestTools.get().upValues(
                        installEvents(),
                        value_info,
                        object : TransSpesInters.BasicRequestListener {

                            override fun loadFailed(message: String) {

                            }

                            override fun loadSuccessConfig(configInfo: String) {
//                                LogUtils.e(configInfo)
                                try {
                                    Gson().fromJson(
                                        configInfo,
                                        CommonViewModel::class.java
                                    )?.run {
                                        if (yvbgjmfbteobho != null && yvbgjmfbteobho.xgyvsjeazyn != null && yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa != null
                                            &&  yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu != null &&
                                            yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu.isNotEmpty() &&
                                            yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr != null &&
                                            yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.gcn == 200) {
                                            MMKV.defaultMMKV().putBoolean("install_event",true)
//                                            LogUtils.e("install_event suc -----")
                                        }
                                    }
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }

                            }


                        })
                }

            }catch (e : Exception){
                e.printStackTrace()
            }

        }


        fun lauchEventsUpload(){
            try {
                    CommonRequestTools.get().upValues(
                        lauchEvents(),
                        value_info,
                        object : TransSpesInters.BasicRequestListener {

                            override fun loadFailed(message: String) {

                            }

                            override fun loadSuccessConfig(configInfo: String) {
                            }


                        })

            }catch (e : Exception){
                e.printStackTrace()
            }

        }
        private fun stringHashMap(
            adsId: String,
            adsType: String,
            value: AdValue, fbIpValue: Double,pos: Long
        ): HashMap<String, String> {
            val stringStringHashMap: HashMap<String, String> = java.util.HashMap()
            try {
                stringStringHashMap["gxooeg"] = AppUtils.getAppName()
                stringStringHashMap["bhc"] = "Android"
                stringStringHashMap["qoy"] = Build.BRAND
                stringStringHashMap["twudq"] = Build.MODEL
                stringStringHashMap["icxpb"] = "10001"
                stringStringHashMap["cod"] = ""+System.currentTimeMillis()
                stringStringHashMap["rwxv"] = ActivityUtils.getTopActivity().localClassName
                stringStringHashMap["gkwgi"] = MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO,"")+""
                stringStringHashMap["bfdoc"] =  JsonUtils.formatJson(jsonInfo(
                    value,
                    adsId,
                    adsType,
                    fbIpValue,pos
                ))
                stringStringHashMap["wnysk"] = commonUniIds()
                stringStringHashMap["kfrfw"] = "" +   loadIds(
                    GlobalMusApp.instance
                )
                stringStringHashMap["rgcv"] = Build.VERSION.RELEASE
                stringStringHashMap["jtetd"] = AppUtils.getAppVersionName()
                if(!TextUtils.isEmpty(MMKV.defaultMMKV().getString("ad_id",""))){
                    stringStringHashMap["quoo"] = MMKV.defaultMMKV().getString("ad_id","")+""
                }
                LogUtils.e(stringStringHashMap.toString())
                return stringStringHashMap
            }catch (e : Exception){
                e.printStackTrace()
            }
            return stringStringHashMap
        }


        private fun installEvents(

        ): HashMap<String, String> {
            val stringStringHashMap: HashMap<String, String> = java.util.HashMap()
            try {
                stringStringHashMap["gxooeg"] = AppUtils.getAppName()
                stringStringHashMap["bhc"] = "Android"
                stringStringHashMap["qoy"] = Build.BRAND
                stringStringHashMap["twudq"] = Build.MODEL
                stringStringHashMap["icxpb"] = "10003"
                stringStringHashMap["cod"] = ""+System.currentTimeMillis()
                stringStringHashMap["rwxv"] = ActivityUtils.getTopActivity().localClassName
                stringStringHashMap["gkwgi"] = MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO,"")+""
                stringStringHashMap["wnysk"] = commonUniIds()
                stringStringHashMap["kfrfw"] = "" +   loadIds(
                    GlobalMusApp.instance
                )
                stringStringHashMap["rgcv"] = Build.VERSION.RELEASE
                stringStringHashMap["jtetd"] = AppUtils.getAppVersionName()
                if(!TextUtils.isEmpty(MMKV.defaultMMKV().getString("ad_id",""))){
                    stringStringHashMap["quoo"] = MMKV.defaultMMKV().getString("ad_id","")+""
                }
                LogUtils.e(stringStringHashMap.toString())
            }catch (e : Exception){
                e.printStackTrace()
            }
            return stringStringHashMap
        }


        private fun lauchEvents(

        ): HashMap<String, String> {
            val stringStringHashMap: HashMap<String, String> = java.util.HashMap()
            try {
                stringStringHashMap["gxooeg"] = AppUtils.getAppName()
                stringStringHashMap["bhc"] = "Android"
                stringStringHashMap["qoy"] = Build.BRAND
                stringStringHashMap["twudq"] = Build.MODEL
                stringStringHashMap["icxpb"] = "10005"
                stringStringHashMap["cod"] = ""+System.currentTimeMillis()
                stringStringHashMap["rwxv"] = ActivityUtils.getTopActivity().localClassName
                stringStringHashMap["gkwgi"] = MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO,"")+""
                stringStringHashMap["wnysk"] = commonUniIds()
                stringStringHashMap["kfrfw"] = "" +   loadIds(
                    GlobalMusApp.instance
                )
                stringStringHashMap["rgcv"] = Build.VERSION.RELEASE
                stringStringHashMap["bfdoc"] =  JsonUtils.formatJson(lauArgs())
                stringStringHashMap["jtetd"] = AppUtils.getAppVersionName()
                if(!TextUtils.isEmpty(MMKV.defaultMMKV().getString("ad_id",""))){
                    stringStringHashMap["quoo"] = MMKV.defaultMMKV().getString("ad_id","")+""
                }
                stringStringHashMap["etqpmn"] =  System.currentTimeMillis().toString()+ loadIds(GlobalMusApp.instance)
                LogUtils.e(stringStringHashMap.toString())
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
                                                    if ("standard_line" == i.lddmfgjtn){
                                                        MMKV.defaultMMKV()
                                                            .putString(MMKV_LINES, i.boifrtru)
                                                    }
                                                    if ("upload_coef" == i.lddmfgjtn){
                                                        MMKV.defaultMMKV()
                                                            .putString(MMKV_COEFS, i.boifrtru)
                                                    }
                                                    if ("window_period_time" == i.lddmfgjtn){
                                                        MMKV.defaultMMKV()
                                                            .putString(MMKV_SYS, i.boifrtru)
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
            installEventsUpload()
        }


    }

}