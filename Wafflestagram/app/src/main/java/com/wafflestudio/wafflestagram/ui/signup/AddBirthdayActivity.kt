package com.wafflestudio.wafflestagram.ui.signup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.DatePicker
import com.wafflestudio.wafflestagram.databinding.ActivityAddBirthdayBinding

class AddBirthdayActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBirthdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBirthdayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNext.isClickable = true
        binding.buttonNext.isEnabled = true
        binding.buttonNext.setTextColor(Color.parseColor("#FFFFFFFF"))

        binding.datePicker.setOnDateChangedListener(object : DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, day: Int) {
                binding.textBirthday.text = ""+ year + "년" + month + "월" + day + "일"
            }

        })
    }
}