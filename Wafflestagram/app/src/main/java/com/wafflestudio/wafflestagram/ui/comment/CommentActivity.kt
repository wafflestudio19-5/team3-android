package com.wafflestudio.wafflestagram.ui.comment

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var commentAdapter : CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager

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

        viewModel.getFeedById(id)

        binding.editComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! == 6){
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

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                commentAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){

            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT).show()
            }
        })
    }
}