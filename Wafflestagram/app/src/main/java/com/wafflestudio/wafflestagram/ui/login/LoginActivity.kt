package com.wafflestudio.wafflestagram.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //login 버튼 활성화

        //login 버튼 클릭 이벤트


    }
}