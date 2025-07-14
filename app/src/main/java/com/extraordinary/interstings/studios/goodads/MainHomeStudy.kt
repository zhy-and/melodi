package com.extraordinary.interstings.studios.goodads
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.beans.CommonViewModel
import com.extraordinary.interstings.studios.beans.ViewModelRemove
import com.extraordinary.interstings.studios.beans.bannerEvent
import com.extraordinary.interstings.studios.services.NativeWeavesListener
import com.extraordinary.interstings.studios.services.ShowListener
import com.extraordinary.interstings.studios.services.StartUpListener
import com.extraordinary.interstings.studios.services.TemTranslateIntersLis
import com.extraordinary.interstings.studios.utils.CommonStudyUtils
import com.extraordinary.interstings.studios.utils.CommonStudyUtils.interWeavesIsShowing
import com.extraordinary.interstings.studios.utils.CommonTools.valueUpload
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_CACHE_TM
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_AD_DATA
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_BUT_ST
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_CLICK_CT
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_CLICK_UNIT
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_SHOW_CT
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_SHOW_ST
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_JK_SHOW_UNIT
import com.extraordinary.interstings.studios.utils.addSongsBanner
import com.extraordinary.interstings.studios.utils.addSongsInter
import com.extraordinary.interstings.studios.utils.albumDetBanner
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.albumListBanner
import com.extraordinary.interstings.studios.utils.albumListInter
import com.extraordinary.interstings.studios.utils.artistDetBanner
import com.extraordinary.interstings.studios.utils.artistDetInter
import com.extraordinary.interstings.studios.utils.artistListBanner
import com.extraordinary.interstings.studios.utils.artistListInter
import com.extraordinary.interstings.studios.utils.feedBacksInter
import com.extraordinary.interstings.studios.utils.finishPageInterOne
import com.extraordinary.interstings.studios.utils.finishPageInterTwo
import com.extraordinary.interstings.studios.utils.geDanDetBanner
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.interstings.studios.utils.geDanListBanner
import com.extraordinary.interstings.studios.utils.geDanListInter
import com.extraordinary.interstings.studios.utils.homeBanner
import com.extraordinary.interstings.studios.utils.lyricBanner
import com.extraordinary.interstings.studios.utils.lyricInter
import com.extraordinary.interstings.studios.utils.musColdStart
import com.extraordinary.interstings.studios.utils.musHotStart
import com.extraordinary.interstings.studios.utils.playViewBanner
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.interstings.studios.utils.privacysInter
import com.extraordinary.interstings.studios.utils.queueInter
import com.extraordinary.interstings.studios.utils.queueListBanner
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.searchResultBanner
import com.extraordinary.interstings.studios.utils.settingsInter
import com.extraordinary.interstings.studios.utils.styleDetBanner
import com.extraordinary.interstings.studios.utils.styleDetInter
import com.extraordinary.interstings.studios.utils.styleListBanner
import com.extraordinary.interstings.studios.utils.styleListInter
import com.extraordinary.interstings.studios.utils.trackListBanner
import com.extraordinary.interstings.studios.utils.trackListInter
import com.extraordinary.weaves.tendency.AppStart.Companion.ouUserIsCallBack
import com.extraordinary.weaves.tendency.HomeActivity
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus

class MainHomeStudy {
    private fun nativeWeavesIsLoading(status: Boolean): Boolean {
        if (status){
            if (nativeWeavesStatus){
                LogUtils.e("result ad is loading")
                return true
            }
        }else{
            if (musWeavesNaIsLoading){
                LogUtils.e( "banner ad is loading")
                return true
            }
        }
        return false
    }


    private fun notifyNative(vos: Boolean, status1 : Boolean) {
        if (vos &&  nativeWeavesStatus){
            nativeWeavesStatus = status1
        }else if (!vos &&  musWeavesNaIsLoading){
            musWeavesNaIsLoading = status1
        }
    }


    var currentPos: Long = -1

    private var tempStatus: Boolean = false



    private fun checkNativeP(adPos: Long): Boolean {
        return false
    }


    fun loadButtons(commonWeavesView: CommonWeavesView?) {
        commonWeavesView ?: return
        val button = commonWeavesView.findViewById<TextView>(R.id.cta)
        val position = currentPos
        LogUtils.e(
            " ad btn visble =====" + CommonStudyUtils.basicInfoLog(position) + MMKV.defaultMMKV()
                .getInt("${MUSIC_CONSTANT_JK_BUT_ST}-${position}", 2)
        )
        when (MMKV.defaultMMKV().getInt("${MUSIC_CONSTANT_JK_BUT_ST}-${position}", 2)) {
            0 -> {
                button.visibility = View.GONE
            }
            1 -> {
                button.visibility = View.VISIBLE
                button.setBackgroundResource(R.drawable.btn_ad_f_shape)
            }
            2 -> {
                button.visibility = View.VISIBLE
                button.setBackgroundResource(R.drawable.btn_ad_shape)
            }
        }
    }
    private fun isUmeng() : Boolean{
        return  ouUserIsCallBack || GoogleMobileAdsConsentManager.getInstance(
            GlobalMusApp.instance
        ).canRequestAds()

    }

    fun createInter(
        interPos: Long,
        tperklyTranslateIntersLis: TemTranslateIntersLis? = null
    ) {
        try {
            var adId = ""
            // 缓存拦截 插页成功回调
            if (interPoolsIsLoading()) {
                LogUtils.e(
                    "inter is loading return=====" + CommonStudyUtils.basicInfoLog(
                        interPos
                    ) + ""
                )
                return
            }
            if (checkInterExists()) {
                LogUtils.e(
                    "inter cached has ads return=====" + CommonStudyUtils.basicInfoLog(
                        interPos
                    ) + ""
                )
                tperklyTranslateIntersLis?.interIsWeavesSuc(true)
                return
            }
            interStatus = true
            interWeavesId?.run { adId = this }
            if (TextUtils.isEmpty(adId)) {
                LogUtils.e("inter id empty=====" + CommonStudyUtils.basicInfoLog(interPos) + "")
                notifyCySts(false)
                return
            }

            val adRequest = AdRequest.Builder().build()
            val context = GlobalMusApp.instance ?: return
            InterstitialAd.load(context, adId, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        LogUtils.e(
                            "inter ad load suc =====" + CommonStudyUtils.basicInfoLog(
                                interPos
                            ) + ""
                        )

                        uniqueInter = interstitialAd
                        interWeavesTimes = System.currentTimeMillis()
                        notifyCySts(false)
                        tperklyTranslateIntersLis?.interIsWeavesSuc(true)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        LogUtils.e(
                            "inter ad load error =====" + CommonStudyUtils.basicInfoLog(
                                interPos
                            ) + ""
                        )
                        notifyCySts(false)
                        tperklyTranslateIntersLis?.interIsWeavesSuc(false)
                    }
                })
        } catch (e: Exception) {
            notifyCySts(false)
            e.printStackTrace()
        }
    }





    fun showWeaveNatives(nativePos: Long, context: Activity): NativeAd? {
        val nativeAd: NativeAd?
        if (context.isDestroyed || context.isFinishing) return null
        if (CommonStudyUtils.posIsCanShow(nativePos)){
            LogUtils.e("native  limit ------" + CommonStudyUtils.basicInfoLog(nativePos))
            return null
        }
        if (interWeavesIsShowing){
            return null
        }
        currentPos = nativePos
        if (weavesNatives != null){
            if (checkMusWeavesNaTimes(nativeWeavesTimes)){
                nativeWeavesTimes  = 0
                LogUtils.e( "curr pos${CommonStudyUtils.basicInfoLog(nativePos)}more than sixty mins ,result cache pools")
                weavesNatives = null
                return null
            }
            tempStatus = true
            nativeAd = weavesNatives
            weavesNatives = null
            return nativeAd
        }else if(weavesBanners != null){
            if (checkMusWeavesNaTimes(banWeavesTimes)){
                banWeavesTimes  = 0
                LogUtils.e( "curr pos${CommonStudyUtils.basicInfoLog(nativePos)}more than sixty mins,banner cache pools")
                weavesBanners = null
                return null
            }
            tempStatus = false
            nativeAd = weavesBanners
            weavesBanners = null
            return nativeAd
        }
        return null
    }

    private var nativeWeavesTimes: Long = 0

    private var nativeWeavesStatus: Boolean = false

    private var banWeavesIds: String? = null

    private var interWeavesId: String? = null


    private fun musWeavesTimesToLoadConfig() {
        musWeavesLoadNatives()
//        if (CommonStudyUtils.interIsCanLoad()) {
//            createInter(trackListInter())
//        }
    }
    private fun notifyCySts(sts: Boolean) {
        interStatus = sts
    }

    private var interStatus: Boolean = false

    var uniqueInter: InterstitialAd? = null

    private fun weavesMusicNatCheckExists(transSt: Boolean): Boolean {
        if (transSt && weavesNatives != null && !checkMusWeavesNaTimes(nativeWeavesTimes)){
            LogUtils.e( "result cache pools has cache,stop cac")
            return true
        }else if(!transSt && weavesBanners != null && !checkMusWeavesNaTimes(banWeavesTimes)){
            LogUtils.e( "banner cache pools has cache,stop cac")
            return true
        }
        return false
    }



    private var musKpIds: String? = null

    private var weavesBanners: NativeAd? = null

    private var weavesNatives: NativeAd? = null

    private var musWeavesNaIsLoading: Boolean = false





    fun initStartAds(baseTrans: baseActivity?) {
        if (CommonStudyUtils.weavesIsCanLoadStart()) { //kp can load
            StartWeavesTools.weavesTools.configStartIds(musKpIds)
            ThreadUtils.runOnUiThread {
                if (StartWeavesTools.weavesTools.weavesObj == null) { // null -> load
                    StartWeavesTools.weavesTools.loadStartAds(baseTrans, object :
                        StartUpListener {
                        override fun loadSuc() {
//                            initLoadInter(baseTrans)
                            if (baseTrans == null || baseTrans.isDestroyed || baseTrans.isFinishing) {
                                return
                            }
                            showStartWeaves(baseTrans)
                            //to load inter
                        }

                        override fun LoadedError() {
//                            initLoadInter(baseTrans)
                            //to load inter

                        }
                    })
                } else {
//                    initLoadInter(baseTrans)
                    // has ad -> show
                    showStartWeaves(baseTrans)
                    //to load inter
                }
            }
        }
    }

    private var banWeavesTimes: Long = 0

    private var interWeavesTimes: Long = 0


    fun initLoadInter(base: baseActivity?) {
        base ?: return
        if (CommonStudyUtils.interIsCanLoad()) {
            createInter(trackListInter())
        }
    }


    companion object {
        val homeStudy: MainHomeStudy by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MainHomeStudy()
        }
    }


    fun musWeavesLoadNatives() {
        if (!CommonStudyUtils.posIsCanShow(homeBanner())) {
            initLoadBanners(
                homeBanner(),
                object : NativeWeavesListener {
                    override fun nativeIsWeavesSuc(sucFlag: Boolean) {
                        if (sucFlag) {
                            EventBus.getDefault().postSticky(
                                bannerEvent(
                                    true
                                )
                            )
                        }
                    }

                })
        }
    }

    private var musNativesId: String? = null

    private fun checkMusWeavesNaTimes(nativeHt : Long): Boolean {
        if (System.currentTimeMillis() - nativeHt   > 60 * 60 * 1000) {
            return true
        }
        return false
    }

    private fun checkInterExists(): Boolean {
        //
        if (uniqueInter != null && !CommonStudyUtils.checkTimes(interWeavesTimes)) {
            return true
        }
        return false
    }


    private fun loadInterWeaves(
        interstitialAd: InterstitialAd?,
        pos: Long,
    ) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            var currentClicked = false
            override fun onAdClicked() {
                if (!currentClicked) {
                    currentClicked = true
                    CommonStudyUtils.appendClick(pos)
                }
                GlobalMusApp.isClicked = true
            }

            override fun onAdDismissedFullScreenContent() {
                interWeavesIsShowing = false
            }

            override fun onAdFailedToShowFullScreenContent(fa: AdError) {
                // Called when ad fails to show.
                interWeavesIsShowing = false

            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                interWeavesIsShowing = true
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                interWeavesIsShowing = true
                CommonStudyUtils.showCountAdd(pos)
//                ThreadUtils.runOnUiThreadDelayed({
//                    LogUtils.e(CommonStudyUtils.basicInfoLog(pos) + "load a new cy ad")
//                    createInter(pos)
//                }, 1500)
            }
        }
        interstitialAd?.setOnPaidEventListener {
            valueUpload(it,"Interstitial", interstitialAd.adUnitId,pos)
        }

    }







    fun initMusConfig(
        activity: baseActivity?, load: Boolean = false
    ) {
        try {
            var dataBean : List<CommonViewModel.YvbgjmfbteobhoBean.XgyvsjeazynBean.VscruffsqgprwaBean.NyjxynieiuBean.EfkqmycrBean.UiiugudcealzBean> ?= null
            if (TextUtils.isEmpty(
                    MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_AD_DATA, "")
                )
            ) {
                MMKV.defaultMMKV().putLong(MUSIC_CONSTANT_APP_AD_CACHE_TM, 0)
                return
            }

            val commonViewModel = Gson().fromJson(
                MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_AD_DATA, ""),
                CommonViewModel::class.java
            ) ?: return

            if (commonViewModel.yvbgjmfbteobho != null && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn != null 
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa != null
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu != null
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu.isNotEmpty()
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0] != null
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr != null
                && commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.uiiugudcealz != null
                ) {
                dataBean = commonViewModel.yvbgjmfbteobho.xgyvsjeazyn.vscruffsqgprwa.nyjxynieiu[0].efkqmycr.uiiugudcealz
            }
            if (dataBean.isNullOrEmpty()) {
                return
            }
           dataBean.forEach {
               mmkvInit(it)
               when (it.cfzmyjoj) {
                   musColdStart(), musHotStart()
                   -> {
                       if (!TextUtils.isEmpty(it.fayiiebmhdhz)) {
                           musKpIds = it.fayiiebmhdhz
                       }
                   }

                   homeBanner(), searchResultBanner(), trackListBanner(),
                   styleListBanner(),  styleDetBanner(), albumListBanner(), albumDetBanner(), artistListBanner(),
                   artistDetBanner(),  geDanListBanner(), geDanDetBanner(), addSongsBanner(), playViewBanner(),
                   lyricBanner(),  queueListBanner()
                   -> {
                       if (!TextUtils.isEmpty(it.fayiiebmhdhz)) {

                            banWeavesIds = it.fayiiebmhdhz

                       }
                   }

                   searchInter(), settingsInter(), scansInter(),
                   feedBacksInter(),  privacysInter(), trackListInter(), styleListInter(), styleDetInter(),
                   albumListInter(),  albumDetInter(), artistListInter(), artistDetInter(), geDanListInter(),
                   geDanDetInter(),  addSongsInter(), playViewsInter(), lyricInter(), queueInter(), finishPageInterOne(), finishPageInterTwo()
                   -> {
                       if (!TextUtils.isEmpty(it.fayiiebmhdhz)) {

                            interWeavesId = it.fayiiebmhdhz

                       }

                   }
               }
           }

            if (!load && (ouUserIsCallBack || GoogleMobileAdsConsentManager.getInstance(
                    GlobalMusApp.instance
                ).canRequestAds())) {
                LogUtils.e("load um  true ----  ")
                initStartAds(activity)
            }

            if (isHomePage(load)) return
            if (!isUmeng()){
                LogUtils.e("skip  ----  ")
                return
            }
            if (!CommonStudyUtils.weavesIsCanLoadStart()){
//                initLoadInter(activity) // can't load kp to load inter
            }
            musWeavesLoadNatives()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initLoadBanners(
        showAdPosition: Long,
        resultAdLoadBack: NativeWeavesListener? = null
    ) {
        val weavesMusicNatPools = checkNativeP(showAdPosition)
        try {
            var postion = showAdPosition
            var unitVoxyId = ""
            if (nativeWeavesIsLoading(weavesMusicNatPools) ){
                return
            }
            if (weavesMusicNatCheckExists(weavesMusicNatPools)){
                resultAdLoadBack?.nativeIsWeavesSuc(true)
                return
            }
            if (weavesMusicNatPools){
                nativeWeavesStatus = true
                musNativesId?.let { unitVoxyId = it }
            }else{
                musWeavesNaIsLoading = true
                banWeavesIds?.let { unitVoxyId = it }
            }
            if (TextUtils.isEmpty(unitVoxyId)){
                notifyNative(weavesMusicNatPools, false)
                LogUtils.e("native id error------"+ CommonStudyUtils.basicInfoLog(postion))
                return
            }
            val context = GlobalMusApp.instance ?: return
            val loader = AdLoader.Builder(context, unitVoxyId)
                .forNativeAd { nativeAd ->
                    nativeAd.setOnPaidEventListener {
                        valueUpload(it,"Native",unitVoxyId,postion)
                    }
                    if (weavesMusicNatPools) {
                        LogUtils.e( "result ad load suc${unitVoxyId}curr pos is ${CommonStudyUtils.basicInfoLog(postion)}")
                        weavesNatives = nativeAd
                        nativeWeavesTimes = System.currentTimeMillis()
                    } else {
                        LogUtils.e( "ban ad load suc${unitVoxyId}curr pos is ${CommonStudyUtils.basicInfoLog(postion)}")
                        weavesBanners = nativeAd
                        banWeavesTimes = System.currentTimeMillis()
                    }
                    notifyNative(weavesMusicNatPools, false)
                    resultAdLoadBack?.nativeIsWeavesSuc(true)
                }
                .withAdListener(object : AdListener() {
                    var clicked = false
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        LogUtils.e(
                            "native  onAdFailedToLoad ------" + CommonStudyUtils.basicInfoLog(
                                postion
                            )
                        )
                        notifyNative(weavesMusicNatPools,false)
                        resultAdLoadBack?.nativeIsWeavesSuc(false)
                    }

                    override fun onAdClicked() {
                        //前一条广告失败时position处理
                        try {
                            if (currentPos.toInt() != -1) {
                                postion = currentPos
                            }
                            if (!clicked) {
                                CommonStudyUtils.appendClick(postion)
                                clicked = true
                            }
                            GlobalMusApp.isClicked = true
                        }catch (e : Exception){
                            e.printStackTrace()
                        }

                    }

                    override fun onAdClosed() {
                    }

                    override fun onAdImpression() {
                        try {
                            if (currentPos.toInt() != -1) {
                                postion = currentPos
                            }
                            CommonStudyUtils.showCountAdd(postion)
                            ThreadUtils.runOnUiThreadDelayed({
                                LogUtils.e(
                                    "native  load a new ad ------" + CommonStudyUtils.basicInfoLog(
                                        postion
                                    )
                                )
                                initLoadBanners(postion)
                            }, 1500)
                        }catch (e : Exception){
                            e.printStackTrace()
                        }

                    }

                    override fun onAdOpened() {
                    }
                })
                .build()

            loader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            notifyNative(weavesMusicNatPools,false)
            e.printStackTrace()
        }
    }



    private fun showStartWeaves(activity: baseActivity?) {
        StartWeavesTools.weavesTools.showStartAds(activity,
            object : ShowListener {
                override fun dispathSuc() {
                    if (activity == null || activity.isFinishing || activity.isDestroyed) {
                        return
                    }
                    activity.startActivity(Intent(activity,HomeActivity::class.java))
                    activity.finish()
                }

                override fun dispathError() {
                    if (activity == null || activity.isFinishing || activity.isDestroyed) {
                        return
                    }
                    EventBus.getDefault().post(
                        ViewModelRemove(
                            true
                        )
                    )
                }
            })
    }

    private fun isHomePage(
        jump: Boolean
    ): Boolean {
        if (jump) {
            ThreadUtils.runOnUiThread {
                musWeavesTimesToLoadConfig()
            }
            return true
        }
        return false
    }





    fun getShowCyAds(recAds: Long, context: Activity) {
        if (CommonStudyUtils.posIsCanShow(recAds)) {
            LogUtils.e("inter flag limit ------" + CommonStudyUtils.basicInfoLog(recAds))
            return
        }
        if (uniqueInter != null) {
            if (CommonStudyUtils.checkTimes(interWeavesTimes)) {
                LogUtils.e("inter flag limit ------" + CommonStudyUtils.basicInfoLog(recAds))
                uniqueInter = null
                interWeavesTimes = 0
                return
            }
            interWeavesIsShowing = true
            uniqueInter?.show(context)
            LogUtils.e("inter showing current pos ===== " + CommonStudyUtils.basicInfoLog(recAds))
            loadInterWeaves(uniqueInter, recAds)
            uniqueInter = null
        } else {
            LogUtils.e("inter cache no to load new  ===== " + CommonStudyUtils.basicInfoLog(recAds))
            createInter(recAds, object : TemTranslateIntersLis {
                override fun interIsWeavesSuc(inSucFlag: Boolean) {
                    if (inSucFlag) {
                        LogUtils.e(
                            "inter cache no to load new suc  ===== " + CommonStudyUtils.basicInfoLog(
                                recAds
                            )
                        )
                    } else {
                        LogUtils.e(
                            "inter cache no to load new  error  ===== " + CommonStudyUtils.basicInfoLog(
                                recAds
                            )
                        )
                    }
                }

            })
        }

    }



    fun showCachedInter(recAds: Long, context: Activity) {
        if (uniqueInter != null) {
            if (CommonStudyUtils.checkTimes(interWeavesTimes)) {
                LogUtils.e("inter flag limit ------" + CommonStudyUtils.basicInfoLog(recAds))
                uniqueInter = null
                interWeavesTimes = 0
                return
            }
            interWeavesIsShowing = true
            uniqueInter?.show(context)
            LogUtils.e("inter showing current pos ===== " + CommonStudyUtils.basicInfoLog(recAds))
            uniqueInter = null
        }
    }


    private fun interPoolsIsLoading(): Boolean {
        if (interStatus) {
            return true
        }
        return false
    }


    private fun mmkvInit(dataViewModel: CommonViewModel.YvbgjmfbteobhoBean.XgyvsjeazynBean.VscruffsqgprwaBean.NyjxynieiuBean.EfkqmycrBean.UiiugudcealzBean) {
        MMKV.mmkvWithID("mmkv_music_app").putBoolean("$MUSIC_CONSTANT_JK_SHOW_ST-${dataViewModel.cfzmyjoj}",
            "0" == dataViewModel.uewlmauz
        )
        MMKV.mmkvWithID("mmkv_music_app").putInt("$MUSIC_CONSTANT_JK_CLICK_CT-${dataViewModel.cfzmyjoj}", dataViewModel.lpxlfylpdvv)
        MMKV.mmkvWithID("mmkv_music_app").putString("$MUSIC_CONSTANT_JK_CLICK_UNIT-${dataViewModel.cfzmyjoj}", dataViewModel.xvthivk)


        MMKV.mmkvWithID("mmkv_music_app").putInt("$MUSIC_CONSTANT_JK_SHOW_CT-${dataViewModel.cfzmyjoj}", dataViewModel.evtpohd)
        MMKV.mmkvWithID("mmkv_music_app").putString("$MUSIC_CONSTANT_JK_SHOW_UNIT-${dataViewModel.cfzmyjoj}", dataViewModel.lovfchcehwx)
    }





}