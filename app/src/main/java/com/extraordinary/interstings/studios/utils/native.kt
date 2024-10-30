package com.extraordinary.interstings.studios.utils

import com.blankj.utilcode.util.LogUtils
import java.util.Locale

fun homeBanner(): Long {
    return 1846437596580286464
}
fun searchResultBanner(): Long {
    return 1846438365290303488
}
fun trackListBanner(): Long {
    return 1846438548904611840
}
fun styleListBanner(): Long {
    return 1846438627517927424
}
fun styleDetBanner(): Long {
    return 1846438677532774400
}
fun albumListBanner(): Long {
    return 1846438740220317696
}
fun albumDetBanner(): Long {
    return 1846438805379616768
}
fun artistListBanner(): Long {
    return 1846438882641186816
}
fun artistDetBanner(): Long {
    return 1846438924869439488
}
fun geDanListBanner(): Long {
    return 1846438989672878080
}
fun geDanDetBanner(): Long {
    return 1846439053705482240
}

fun addSongsBanner(): Long {
    return 1846439148044161024
}
fun playViewBanner(): Long {
    return 1846439212973428736
}

fun lyricBanner(): Long {
    return 1846439281263120384
}
fun queueListBanner(): Long {
    return 1846439337749647360
}

fun searchInter(): Long {
    return  1846435371514073088
}
fun settingsInter(): Long {
    return  1846435460935847936
}

fun scansInter(): Long {
    return  1846435527173607424
}

fun feedBacksInter(): Long {
    return  1846435758316195840
}

fun privacysInter(): Long {
    return  1846435912964902912
}

fun trackListInter(): Long {
    return  1846435968710873088
}

fun styleListInter(): Long {
    return  1846436024958849024
}

fun styleDetInter(): Long {
    return  1846436070247108608
}

fun albumListInter(): Long {
    return  1846436124147716096
}

fun albumDetInter(): Long {
    return   1846436169061634048
}

fun artistListInter(): Long {
    return  1846436212383944704
}

fun artistDetInter(): Long {
    return  1846436266264236032
}

fun geDanListInter(): Long {
    return  1846436317128298496
}

fun geDanDetInter(): Long {
    return  1846436367464665088
}

fun addSongsInter(): Long {
    return  1846436417585549312
}

fun playViewsInter(): Long {
    return  1846436465765650432
}

fun lyricInter(): Long {
    return  1846436513447809024

}

fun queueInter(): Long {
    return  1846436586939879424
}

fun finishPageInterOne(): Long {
    return  1846437092280897536
}

fun finishPageInterTwo(): Long {
    return  1846437144865804288
}


fun musColdStart(): Long {
    return 1846435010288422912
}

fun musHotStart(): Long {
    return 1846435242342879232
}

const  val MUSIC_CONSTANT_JK_CLICK_CT = "tweaves_mus_network_click_count"
const  val MUSIC_CONSTANT_JK_CLICK_UNIT = "tweaves_mus_network_click_unit"

const  val MUSIC_CONSTANT_JK_SHOW_CT = "tweaves_mus_network_show_count"
const  val MUSIC_CONSTANT_JK_SHOW_UNIT = "tweaves_mus_network_show_unit"

fun checkOUsers(): Boolean {
    val currentCountry =  Locale.getDefault().country.toString().lowercase()
    LogUtils.e("currentCountry--- $currentCountry")
    for (i in countryCode){
        if (i == currentCountry){
            LogUtils.e("currentCountry true")
            return  true
        }
    }
    LogUtils.e("currentCountry false")
    return  false
}

private var countryCode = arrayOf("ie", "ee", "at", "bg", "be", "pl", "dk", "de", "fr",
    "fi", "nl", "cz", "hr", "lv", "lt", "lu", "ro", "mt",
    "pt", "se", "cy", "sk", "si", "es", "gr", "hu", "it","gb","ch")


const val MUSIC_CONSTANT_JK_BUT_ST = "tweaves_mus_network_button_status"
const val MUSIC_CONSTANT_JK_SHOW_ST = "tweaves_mus_network_is_show"
const val MUSIC_CONSTANT_BD_CLICK_CT = "tweaves_mus_native_click_count"
const val MUSIC_CONSTANT_BD_SHOW_CT = "tweaves_mus_native_show_count"
const val MUSIC_CONSTANT_APP_AD_CACHE_TM = "weaves_mus_ads_cm"
const val MUSIC_CONSTANT_APP_APP_CACHE_TM = "weaves_mus_app_cm"
const val MUSIC_CONSTANT_APP_REFRESH = "weaves_mus_refresh"
const val MUSIC_CONSTANT_APP_REFER_INFO = "weaves_mus_re_infos"
const val MUSIC_CONSTANT_APP_AD_DATA = "weaves_mus_ads_data"
const val MUSIC_CONSTANT_APP_IS_AUTO_PLAY = "weaves_mus_is_auto_play"

const val MESSAGE_MSG_CODE = 0x100


