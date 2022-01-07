package com.wafflestudio.wafflestagram.ui.profile

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EditProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Edit Profile")
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // UserFragment에서 진입할 때 Extra로 유저정보 받아오기
    }
}