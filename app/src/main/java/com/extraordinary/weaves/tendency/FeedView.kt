package com.extraordinary.weaves.tendency

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.extraordinary.interstings.studios.goodads.MainHomeStudy
import com.extraordinary.interstings.studios.utils.feedBacksInter
import com.extraordinary.interstings.studios.utils.scansInter
import com.extraordinary.weaves.tendency.databinding.FeedBackBinding
import com.extraordinary.weaves.tendency.databinding.LyricViewBinding
import com.extraordinary.weaves.tendency.databinding.MoreViewBinding
import com.gyf.immersionbar.ImmersionBar
import dora.util.TextUtils
import dora.util.ThreadUtils
import dora.util.ToastUtils

class FeedView : baseActivity() {
    private lateinit var binding: FeedBackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHomeStudy.homeStudy.getShowCyAds(feedBacksInter(),this)

        enableEdgeToEdge()
        ImmersionBar.with(this)
            .transparentNavigationBar()
            .init()
        binding = FeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.apply {
            backBtn.setOnClickListener {
                speFinish()
            }
            titleContent.text = "FeedBack"
        }
        binding.btnFeed.setOnClickListener {
            if (TextUtils.isEmpty(binding.searchEdit.text.toString())){
                ToastUtils.showShort("Please input content")
                return@setOnClickListener
            }
            ThreadUtils.runOnUiThreadDelayed({
                ToastUtils.showShort("Submit successfully")
                finish()
            },800)

        }

    }

}