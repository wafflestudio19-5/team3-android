package com.wafflestudio.wafflestagram.ui.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //login 버튼 활성화

        //login 버튼 클릭 이벤트
        binding.buttonLogin.setOnClickListener {
            viewModel.getResponseByLogin()
            Toast.makeText(applicationContext, viewModel.fetchResponse, Toast.LENGTH_SHORT).show()
        }
    }
}