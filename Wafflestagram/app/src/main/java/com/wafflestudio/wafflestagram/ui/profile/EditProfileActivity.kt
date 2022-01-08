package com.wafflestudio.wafflestagram.ui.profile

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amazonaws.SDKGlobalConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityEditProfileBinding
import com.wafflestudio.wafflestagram.network.dto.SetProfilePhotoRequest
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import dagger.hilt.android.AndroidEntryPoint
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.SingleListener
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class EditProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var awsCredentials: BasicAWSCredentials
    private lateinit var s3Client: AmazonS3Client
    private lateinit var transferUtility : TransferUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Edit Profile")
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        awsCredentials = BasicAWSCredentials(
            SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY,
            SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY
        )
        s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build()

        binding.buttonChangeProfile.setOnClickListener {
            InstagramPicker(this).show(1, 1, SingleListener { address ->
                upload(address)
            })
        }

        // UserFragment에서 진입할 때 Extra로 유저정보 받아오기
        binding.inputName.setText(intent.getStringExtra("name"))
        binding.inputUsername.setText(intent.getStringExtra("username"))
        binding.inputWebsite.setText(intent.getStringExtra("website"))
        binding.inputBio.setText(intent.getStringExtra("bio"))
        val id = intent.getIntExtra("id", 0)

        viewModel.getProfilePhoto(id)

        binding.buttonClose.setOnClickListener {
            finish()
        }

        binding.buttonCheck.setOnClickListener{
            val updateUserRequest = UpdateUserRequest(
            binding.inputName.text.toString(), binding.inputUsername.text.toString(),
            binding.inputWebsite.text.toString(), binding.inputBio.text.toString()
            )

            viewModel.updateUser(updateUserRequest)

            finish()
        }

        viewModel.image.observe(this, {response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(binding.buttonUserImage)
            }
        })

        viewModel.response.observe(this, {
            viewModel.getProfilePhoto(id)
        })
    }

    private fun upload(image : String){
        val fileName = System.currentTimeMillis().toString()

        val uploadObserver = transferUtility.upload("waffle-team3-bucket", fileName, File(getRealPathFromURI(this, Uri.parse(image))))


        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED){
                    //서버에 사진 변경 리퀘스트 보내기, fileName
                    val request = SetProfilePhotoRequest(fileName)
                    viewModel.setProfilePhoto(request)
                }
            }
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                if(bytesCurrent == bytesTotal){
                }
            }
            override fun onError(id: Int, ex: Exception?) {
                Timber.e(ex)
            }
        })
    }

    fun getRealPathFromURI(context: Context?, uri: Uri?): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri!!)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                } else {
                    val SDcardpath = getRemovableSDCardPath(context)?.split("/Android".toRegex())!!.toTypedArray()[0]
                    SDcardpath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id))
                return getDataColumn(context!!, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context!!, contentUri, selection,
                    selectionArgs)
            }
        } else if (uri != null) {
            if ("content".equals(uri.getScheme(), ignoreCase = true)) {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(
                    context!!, uri, null, null)
            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.getPath()
            }
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

    fun getRemovableSDCardPath(context: Context?): String? {
        val storages = ContextCompat.getExternalFilesDirs(context!!, null)
        return if (storages.size > 1 && storages[0] != null && storages[1] != null) storages[1].toString() else ""
    }
}