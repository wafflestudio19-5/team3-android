package com.wafflestudio.wafflestagram.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.wafflestudio.wafflestagram.databinding.ActivityLoginBinding
import com.wafflestudio.wafflestagram.databinding.DialogBinding
import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //login 버튼 활성화
        binding.editUsername.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editPassword.text.isNotEmpty()){
                    binding.buttonLogin.isClickable = true
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonLogin.isClickable = false
                    binding.buttonLogin.isEnabled = false
                    binding.buttonLogin.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }

        })
        binding.editPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editUsername.text.isNotEmpty()){
                    binding.buttonLogin.isClickable = true
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonLogin.isClickable = false
                    binding.buttonLogin.isEnabled = false
                    binding.buttonLogin.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }

        })

        //login 버튼 클릭 이벤트
        binding.buttonLogin.setOnClickListener {
            val username = binding.editUsername.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            Timber.d(username, password)

            viewModel.getResponseByLogin(LoginRequest(username, password))
            currentFocus?.hideKeyboard()
        }

        // facebook login
        binding.buttonSocialLoginFacebook.setOnClickListener {
            viewModel.getResponseBySocialLogin(FACEBOOK)
        }

        // google login
        binding.buttonSocialLoginGoogle.setOnClickListener {
            viewModel.getResponseBySocialLogin(GOOGLE)
        }

        //signup 버튼
        binding.buttonSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // fetch Responses

        viewModel.fetchPingResponse.observe(this,{
            showDialog(it)
        })

        viewModel.fetchLoginResponse.observe(this, { response ->
            if(response.isSuccessful){
                sharedPreferences.edit {
                    putString(TOKEN, response.headers()["Authentication"])
                    putInt(CURRENT_USER_ID, response.body()!!.string().toInt())
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                Toast.makeText(this, "이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchSocialLoginUrl.observe(this, { response ->
            if(response.isSuccessful){
                val intent =  Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(response.raw().request.url.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error code: " + response.code(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

    private fun showDialog(contents: String){
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(this, dialog, 0.65F, 0.35F)
        dialogBinding.textDialogContents.text = contents
        dialogBinding.buttonDialog.setOnClickListener {
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

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    companion object{
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
        const val FACEBOOK = "facebook"
        const val GOOGLE = "google"
    }
}