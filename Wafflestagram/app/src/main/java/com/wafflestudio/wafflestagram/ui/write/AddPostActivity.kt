package com.wafflestudio.wafflestagram.ui.write

import android.R.attr
import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBunFileProvider
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityAddPostBinding
import dagger.hilt.android.AndroidEntryPoint
import android.R.attr.path
import android.app.Activity
import android.content.ContentProviderClient
import android.net.Credentials
import android.os.Parcelable
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.MultiListener
import java.io.File
import java.lang.Exception


@AndroidEntryPoint
class AddPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddPostBinding
    private lateinit var awsCredentials: BasicAWSCredentials
    private lateinit var s3Client: AmazonS3Client
    private lateinit var transferUtility : TransferUtility
    private lateinit var images : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InstagramPicker(this).show(4,3,10, MultiListener { addresses ->
            //저장
            images = addresses
            binding.imageSelected.setImageURI(Uri.parse(addresses[0]))
        })

        awsCredentials = BasicAWSCredentials("AKIAWGOGMA6KRIASEPQ5","6ReuUu3jRvcaZYv00DUSdtpfmzkGFzW/Uk6Qr/dw")
        s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build()

        binding.buttonComplete.setOnClickListener {

        }

        binding.imageSelected.setOnClickListener {
            ImageDialogFragment().show(
                supportFragmentManager, "ImageDialog"
            )
        }
        /*
        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setActionBarColor(Color.parseColor("#00ff0000"))
            .setActionBarTitleColor(Color.parseColor("#FF000000"))
            .setActionBarTitle("새 게시물")
            .setAllViewTitle("새 게시물")
            .textOnImagesSelectionLimitReached("최대 10개의 사진을 포함할 수 있습니다.")
            .textOnNothingSelected("사진을 선택해주세요.")
            .hasCameraInPickerPage(true)
            .isStartInAllView(true)
            .setIsShowCount(true)
            .setIsUseDetailView(true)
            .setPickerSpanCount(3)
            .setAlbumThumbnailSize(90)
            .setIsUseDetailView(false)
            .setDoneButtonDrawable(ContextCompat.getDrawable(this, R.drawable.icon_continue))
            .startAlbum()

         */

    }

    private fun upload(){
        for(image in images){
            val uploadObserver = transferUtility.upload("waffle-team3-bucket", File(image))
            uploadObserver.setTransferListener(object :TransferListener{
                override fun onStateChanged(id: Int, state: TransferState?) {
                    TODO("Not yet implemented")
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    TODO("Not yet implemented")
                }

                override fun onError(id: Int, ex: Exception?) {
                    TODO("Not yet implemented")
                }

            })

        }

    }
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FishBun.FISHBUN_REQUEST_CODE ->{
                if(resultCode == RESULT_OK){
                    val path = data?.data

                    binding.imageSelected.setImageURI(Uri.parse(path?.toString()))
                    Toast.makeText(this, path?.toString(), Toast.LENGTH_SHORT).show()
                }else if(resultCode == RESULT_CANCELED){

                }
            }
        }
    }*/
}