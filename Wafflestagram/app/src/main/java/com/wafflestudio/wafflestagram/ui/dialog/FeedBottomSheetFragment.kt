package com.wafflestudio.wafflestagram.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.wafflestagram.databinding.DialogReconfirmBinding
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBottomSheetBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.post.EditPostActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager

@AndroidEntryPoint
class FeedBottomSheetFragment(context: Context) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFeedBottomSheetBinding
    private var feedId: Int = 0
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedId = arguments?.getInt("feedId")!!
        val userId = arguments?.getInt("userId")
        val currUserId = arguments?.getInt("currUserId")
        position = arguments?.getInt("position")!!


        if(userId == currUserId){
            binding.buttonDelete.visibility = View.VISIBLE
            binding.buttonEdit.visibility = View.VISIBLE
        }else{
            binding.buttonDelete.visibility = View.GONE
            binding.buttonEdit.visibility = View.GONE
        }

        binding.buttonShare.setOnClickListener {
            Toast.makeText(activity, "아직 구현 중입니다.", Toast.LENGTH_LONG).show()
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            showDialog()
            dismiss()
        }

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(activity, EditPostActivity::class.java)
            intent.putExtra("feedId", feedId)
            intent.putExtra("userId", userId)
            intent.putExtra("position", position)
            startActivity(intent)
            dismiss()
        }
    }

    private fun showDialog(){
        val dialogBinding = DialogReconfirmBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(context!!)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(context!!, dialog, 0.65F, 0.35F)
        dialogBinding.buttonConfirm.setOnClickListener {
            //delete feed
            //(FragmentComponentManager.findActivity(context) as? DetailFeedActivity)?.deleteFeed(feedId)
            onClickedListener.onClicked(feedId, position)
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

    interface ButtonClickListener{
        fun onClicked(id: Int, position: Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }

    companion object {
        const val TAG = "FeedBottomSheet"
    }
}