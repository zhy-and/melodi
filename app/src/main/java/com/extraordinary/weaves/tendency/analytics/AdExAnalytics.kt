package com.extraordinary.weaves.tendency.analytics

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.extraordinary.interstings.studios.services.ApiStudios
import com.extraordinary.interstings.studios.utils.CommonTools
import com.extraordinary.interstings.studios.utils.MUSIC_CONSTANT_APP_REFER_INFO
import com.extraordinary.interstings.studios.workmanager.CommonRequestTools
import com.extraordinary.interstings.studios.workmanager.TransSpesInters
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.thinkup.core.api.TUAdInfo
import java.math.BigDecimal
import java.util.Locale

/** Advertising exposure revenue events added from krode's ad_ex integration. */
object AdExAnalytics {
    // TODO: Replace these event values when Melodi's final values are supplied.
    private const val EVENT_ADMOB_AD_EX = "3zlf0w"
    private const val EVENT_TOPON_AD_EX = "2ei3m8"
    private const val MMKV_UA = "melodi_ad_ex_ua"

    fun prefetchDeviceInfo(context: Context) {
        cacheUserAgentIfNeeded(context.applicationContext)
        // GlobalMusApp already fetches and caches GAID under "ad_id" at startup.
    }

    @JvmStatic
    fun reportAdmob(adValue: AdValue?) {
        if (adValue == null) return
        val context = GlobalMusApp.instance ?: return
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<Boolean>() {
            override fun doInBackground(): Boolean {
                runCatching {
                    val value = BigDecimal(adValue.valueMicros.toString())
                        .divide(BigDecimal("1000000")).toDouble()
                    val fields = linkedMapOf<String, Any>(
                        "ad_ex_app_id" to context.packageName,
                        "ad_ex_gaid" to resolveGaid(context),
                        "ad_ex_user_agent" to cachedUserAgent(context),
                        "ad_ex_brand" to Build.BRAND.orEmpty(),
                        "ad_ex_model" to Build.MODEL.orEmpty(),
                        "ad_ex_platform" to "admob",
                        "ad_ex_value" to value,
                        "ad_ex_currency" to (adValue.currencyCode ?: "USD"),
                        "ad_ex_country" to CommonTools.getCountryInfo(context).uppercase(Locale.ROOT),
                    )
                    postAdExLog(context, EVENT_ADMOB_AD_EX, fields)
                }.onFailure { LogUtils.e("ad_ex admob error: $it") }
                return true
            }

            override fun onSuccess(result: Boolean?) = Unit
        })
    }

    @JvmStatic
    fun reportTopOn(adInfo: TUAdInfo?) {
        if (adInfo == null) return
        val context = GlobalMusApp.instance ?: return
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<Boolean>() {
            override fun doInBackground(): Boolean {
                runCatching {
                    val country = adInfo.country?.takeIf { it.isNotEmpty() }
                        ?: CommonTools.getCountryInfo(context)
                    val fields = linkedMapOf<String, Any>(
                        "ad_ex_app_id" to context.packageName,
                        "ad_ex_gaid" to resolveGaid(context),
                        "ad_ex_user_agent" to cachedUserAgent(context),
                        "ad_ex_brand" to Build.BRAND.orEmpty(),
                        "ad_ex_model" to Build.MODEL.orEmpty(),
                        "ad_ex_platform" to "topon",
                        "ad_ex_network_name" to (adInfo.networkName ?: ""),
                        "ad_ex_ecpm" to adInfo.ecpm,
                        "ad_ex_currency" to (adInfo.currency ?: "USD"),
                        "ad_ex_country" to country.uppercase(Locale.ROOT),
                    )
                    postAdExLog(context, EVENT_TOPON_AD_EX, fields)
                }.onFailure { LogUtils.e("ad_ex topon error: $it") }
                return true
            }

            override fun onSuccess(result: Boolean?) = Unit
        })
    }

    private fun postAdExLog(context: Context, eventId: String, fields: Map<String, Any>) {
        val params = hashMapOf(
            "gxooeg" to AppUtils.getAppName(),
            "bhc" to "Android",
            "qoy" to Build.BRAND.orEmpty(),
            "twudq" to Build.MODEL.orEmpty(),
            "icxpb" to eventId,
            "cod" to System.currentTimeMillis().toString(),
            "rwxv" to runCatching { ActivityUtils.getTopActivity()?.localClassName.orEmpty() }.getOrDefault(""),
            "gkwgi" to MMKV.defaultMMKV().getString(MUSIC_CONSTANT_APP_REFER_INFO, "").orEmpty(),
            "bfdoc" to Gson().toJson(fields),
            "wnysk" to ApiStudios.commonUniIds(),
            "kfrfw" to CommonTools.loadIds(context),
            "rgcv" to Build.VERSION.RELEASE.orEmpty(),
            "jtetd" to AppUtils.getAppVersionName(),
            "quoo" to MMKV.defaultMMKV().getString("ad_id", "").orEmpty(),
        )
        CommonRequestTools.get().upValues(params, ApiStudios.value_info,
            object : TransSpesInters.BasicRequestListener {
                override fun loadFailed(message: String) { LogUtils.e("ad_ex error: $message") }
                override fun loadSuccessConfig(configInfo: String) { LogUtils.e("ad_ex suc: $configInfo") }
            })
    }

    private fun resolveGaid(context: Context): String {
        val cached = MMKV.defaultMMKV().getString("ad_id", "").orEmpty()
        if (cached.isNotEmpty()) return cached
        val gaid = runCatching { AdvertisingIdClient.getAdvertisingIdInfo(context).id.orEmpty() }
            .getOrDefault("")
        if (gaid.isNotEmpty()) MMKV.defaultMMKV().putString("ad_id", gaid)
        return gaid
    }

    private fun cachedUserAgent(context: Context): String {
        val cached = MMKV.defaultMMKV().getString(MMKV_UA, "").orEmpty()
        if (cached.isNotEmpty()) return cached
        cacheUserAgentIfNeeded(context)
        return MMKV.defaultMMKV().getString(MMKV_UA, "").orEmpty()
    }

    private fun cacheUserAgentIfNeeded(context: Context) {
        if (MMKV.defaultMMKV().getString(MMKV_UA, "").orEmpty().isNotEmpty()) return
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post { cacheUserAgentIfNeeded(context) }
            return
        }
        runCatching {
            val webView = WebView(context)
            val ua = webView.settings.userAgentString.orEmpty()
            webView.destroy()
            if (ua.isNotEmpty()) MMKV.defaultMMKV().putString(MMKV_UA, ua)
        }.onFailure { LogUtils.e("ad_ex user agent error: $it") }
    }
}
