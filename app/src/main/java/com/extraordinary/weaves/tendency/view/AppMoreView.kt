package com.extraordinary.weaves.tendency.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.blankj.utilcode.util.AppUtils
import com.extraordinary.interstings.studios.goodads.GoogleMobileAdsConsentManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.feedBacksInter
import com.extraordinary.interstings.studios.utils.privacysInter
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.interstings.studios.utils.searchInter
import com.extraordinary.interstings.studios.utils.settingsInter
import com.extraordinary.weaves.tendency.FeedView
import com.extraordinary.weaves.tendency.PrivacyPage
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.databinding.MoreViewBinding
import com.extraordinary.weaves.tendency.dialog.event_two_feedback_setting
import com.extraordinary.weaves.tendency.dialog.event_two_privacy_policy_setting
import com.extraordinary.weaves.tendency.dialog.event_two_scan_setting
import com.extraordinary.weaves.tendency.dialog.event_two_search_ok
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.scan.ScanPage
import com.extraordinary.weaves.tendency.widget.WidgetPinHelper
import com.extraordinary.weaves.tendency.widget.WidgetPinSource
import com.gyf.immersionbar.ImmersionBar

class AppMoreView : baseActivity() {
    var tempCount = 0
    private lateinit var binding: MoreViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(settingsInter(),this)

        enableEdgeToEdge()
        ImmersionBar.with(this)
            .transparentNavigationBar()
            .init()
        binding = MoreViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Settings"
        }
        if (GoogleMobileAdsConsentManager.getInstance(GlobalMusApp.instance).isPrivacyOptionsRequired){
            binding.privacys.apply {
                paint?.flags = Paint.UNDERLINE_TEXT_FLAG
                paint?.isAntiAlias = true
                visibility = View.VISIBLE
                setOnClickListener {
                    GoogleMobileAdsConsentManager.getInstance(GlobalMusApp.instance).showPrivacyOptionsForm(this@AppMoreView,null)
                }
            }
        }
        binding.scanSettings.setOnClickListener {
            logMusicEvents(event_two_scan_setting)

            startInterActivity(Intent(this, ScanPage::class.java), scansInter())
//            finish()
        }
        binding.widgetSettings.setOnClickListener {
            WidgetPinHelper.requestPin(this, WidgetPinSource.SETTINGS)
        }
        binding.feedSettings.setOnClickListener {
            logMusicEvents(event_two_feedback_setting)

            startInterActivity(Intent(this, FeedView::class.java), feedBacksInter())
//            finish()

        }
        binding.version.text = "V "+AppUtils.getAppVersionName()

        binding.priSettings.setOnClickListener {
            logMusicEvents(event_two_privacy_policy_setting)

            startInterActivity(Intent(this, PrivacyPage::class.java), privacysInter())

//            finish()
        }
    }

}
