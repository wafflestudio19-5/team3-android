package com.wafflestudio.wafflestagram.ui.comment

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityCommentBinding
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var commentAdapter : CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentAdapter = CommentAdapter()
        commentLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewComment.apply {
            adapter = commentAdapter
            layoutManager = commentLayoutManager
        }

        val id = intent.getIntExtra("id", 0)

        viewModel.getMyInfo()
        viewModel.getFeedById(id)

        binding.editComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0){
                    binding.buttonComment.isClickable = true
                    binding.buttonComment.isEnabled = true
                    binding.buttonComment.setTextColor(Color.parseColor("#368AFF"))
                }else{
                    binding.buttonComment.isClickable = false
                    binding.buttonComment.isEnabled = false
                    binding.buttonComment.setTextColor(Color.parseColor("#9DCFFF"))
                }
            }
        })

        binding.buttonComment.setOnClickListener {
            val request = AddCommentRequest(binding.editComment.text.toString())
            viewModel.addComment(id, request)
            binding.editComment.text = null
        }

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                commentAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){

            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.comment.observe(this , {response ->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.myInfo.observe(this, { response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()?.profilePhotoURL).centerCrop().into(binding.imageUserProfile)
            }
        })

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}