package com.wafflestudio.wafflestagram.ui.signup

import android.graphics.Color
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

        binding.buttonNext.isClickable = true
        binding.buttonNext.isEnabled = true
        binding.buttonNext.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.textBirthday.text = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREAN).format(System.currentTimeMillis())
        binding.datePicker.setOnDateChangedListener(object : DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, day: Int) {
                binding.textBirthday.text = ""+ year + "년" + (month+1) + "월" + day + "일"
            }

        })
        binding.datePicker.maxDate = System.currentTimeMillis()
    }
}