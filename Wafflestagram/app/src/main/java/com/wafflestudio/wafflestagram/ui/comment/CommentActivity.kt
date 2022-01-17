package com.wafflestudio.wafflestagram.ui.comment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityCommentBinding
import com.wafflestudio.wafflestagram.databinding.DialogReconfirmBinding
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.network.dto.AddReplyRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.DELETE
import javax.inject.Inject

@AndroidEntryPoint
class CommentActivity : AppCompatActivity(), CommentInterface{

    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var commentAdapter : CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager
    private var isReply : Boolean = false
    private var commentId : Int = 0
    private var position: Int = 0

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentAdapter = CommentAdapter(this)
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
            if(!isReply){
                val request = AddCommentRequest(binding.editComment.text.toString())
                viewModel.addComment(id, request)
            }else{
                val request = AddReplyRequest(binding.editComment.text.toString())
                viewModel.addReply(commentId, request)
                isReply = false
                binding.layoutReply.visibility = View.GONE
            }
            binding.editComment.text = null
        }

        binding.buttonReplyClose.setOnClickListener {
            isReply = false
            binding.layoutReply.visibility = View.GONE
        }

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                commentAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
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

        viewModel.reply.observe(this, {response->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
            }
        })

        viewModel.myInfo.observe(this, { response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()?.profilePhotoURL).centerCrop().into(binding.imageUserProfile)
                commentAdapter.updateUser(response.body()!!)
            }
        })

        binding.buttonBack.setOnClickListener {
            finish()
        }
        viewModel.deleteCommentResponse.observe(this, {response->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
                //commentAdapter.notifyGroupRemove(position)
            }
        })

    }

    override fun deleteComment(id: Int, position: Int) {
        showDialog(DELETE_TYPE_COMMENT, id, position)
    }

    override fun addReply(comment: Comment, position: Int) {
        isReply = true
        binding.layoutReply.visibility = View.VISIBLE
        commentId = comment.id.toInt()
        binding.textReplyUsername.text = comment.writer.username
    }

    override fun deleteReply(id: Int, position: Int) {
        showDialog(DELETE_TYPE_REPLY, id, position)
    }

    private fun showDialog(type: Int, id: Int, position: Int){
        val dialogBinding = DialogReconfirmBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(this, dialog, 0.65F, 0.35F)
        when(type){
            DELETE_TYPE_COMMENT -> dialogBinding.textDialogContents.text = "이 댓글을\n삭제하시겠어요?"
            DELETE_TYPE_REPLY -> dialogBinding.textDialogContents.text = "이 답글을\n삭제하시겠어요?"
            else -> {}
        }
        dialogBinding.buttonConfirm.setOnClickListener {
            //delete Comment
            //(FragmentComponentManager.findActivity(context) as? DetailFeedActivity)?.deleteFeed(feedId)
            if(type == DELETE_TYPE_COMMENT){
                viewModel.deleteComment(id)
            }else{
                viewModel.deleteReply(id)
            }
            dialog.dismiss()
        }
        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun resizeDialog(context: Context, dialog: AlertDialog, width: Float, height: Float){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val size = Point()
            @Suppress("DEPRECATION")
            display.getSize(size)
            val window = dialog.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()

            window?.setLayout(x, y)
        }else{
            val bound = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            val x = (bound.width() * width).toInt()
            val y = (bound.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    companion object{
        const val DELETE_TYPE_COMMENT = 0
        const val DELETE_TYPE_REPLY = 1
    }
}