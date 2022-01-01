package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.databinding.ActivityAddBirthdayBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddBirthdayActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBirthdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBirthdayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra("phoneNumber")
        val name = intent.getStringExtra("name")
        val password = intent.getStringExtra("password")

        binding.textBirthday.text = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREAN).format(System.currentTimeMillis())
        binding.datePicker.setOnDateChangedListener(object : DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, day: Int) {
                binding.textBirthday.text = ""+ year + "년" + (month+1) + "월" + day + "일"
            }

        })
        binding.datePicker.maxDate = System.currentTimeMillis()

        binding.buttonNext.setOnClickListener{
            val intent = Intent(this, SignUpCompleteActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            intent.putExtra("name", name)
            intent.putExtra("password", password)
            intent.putExtra("birthday", binding.textBirthday.text.toString())
            startActivity(intent)
        }
    }
}