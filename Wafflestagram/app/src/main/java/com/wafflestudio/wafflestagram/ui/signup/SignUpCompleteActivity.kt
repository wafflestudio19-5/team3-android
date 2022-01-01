package com.wafflestudio.wafflestagram.ui.signup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.wafflestudio.wafflestagram.databinding.ActivitySignUpCompleteBinding
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import java.util.regex.Pattern

class SignUpCompleteActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpCompleteBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra("phoneNumber")!!
        val name = intent.getStringExtra("name")!!
        val password = intent.getStringExtra("password")!!
        val birthday = intent.getStringExtra("birthday")!!

        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editUsername.text.isNotEmpty()){
                    binding.buttonComplete.isClickable = true
                    binding.buttonComplete.isEnabled = true
                    binding.buttonComplete.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonComplete.isClickable = false
                    binding.buttonComplete.isEnabled = false
                    binding.buttonComplete.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }

        })
        binding.editUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editEmail.text.isNotEmpty()){
                    binding.buttonComplete.isClickable = true
                    binding.buttonComplete.isEnabled = true
                    binding.buttonComplete.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonComplete.isClickable = false
                    binding.buttonComplete.isEnabled = false
                    binding.buttonComplete.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }
        })
        binding.buttonComplete.setOnClickListener{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.text).matches()){
                binding.textInputLayoutEmail.error = null
                val request = SignUpRequest(
                    binding.editEmail.text.toString(),
                    password,
                    name,
                    binding.editUsername.text.toString(),
                    birthday,
                    phoneNumber
                )
                viewModel.signUp(request)
            }else{
                binding.textInputLayoutEmail.error = "올바른 이메일 주소를 입력하세요."
            }
        }

        viewModel.tokenResponse.observe(this, {response ->
            if(response.isSuccessful){
                //token 저장
                //메인으로 이동
            }else{
                //에러 메시지
            }
        })
    }
}