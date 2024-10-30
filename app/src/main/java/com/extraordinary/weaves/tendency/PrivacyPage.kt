package com.extraordinary.weaves.tendency

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.privacysInter
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.weaves.tendency.databinding.FeedBackBinding
import com.extraordinary.weaves.tendency.databinding.PrivaViewBinding
import com.gyf.immersionbar.ImmersionBar

class PrivacyPage : baseActivity() {
    private lateinit var binding: PrivaViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(privacysInter(),this)

        enableEdgeToEdge()
        ImmersionBar.with(this)
            .transparentNavigationBar()
            .init()
        binding = PrivaViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "Privacy Policy"
        }
        binding.webs.loadUrl("https://oddt.melodimuic.com/privacyPolicy.html")

    }

}