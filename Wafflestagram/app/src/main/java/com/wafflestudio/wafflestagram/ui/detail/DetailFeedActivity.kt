package com.wafflestudio.wafflestagram.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.aghajari.zoomhelper.ZoomHelper
import com.wafflestudio.wafflestagram.databinding.ActivityDetailFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ZoomHelper.getInstance().maxScale = 3f

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }
}