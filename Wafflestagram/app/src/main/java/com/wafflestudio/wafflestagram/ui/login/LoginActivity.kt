package com.wafflestudio.wafflestagram.ui.login

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityLoginBinding
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

    // for replace startActivityForResult()
    private lateinit var facebookResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleResultLauncher: ActivityResultLauncher<Intent>

    // Google Login Request Code
    private val FACEBOOK_RC_SIGN_IN = 1
    private val GOOGLE_RC_SIGN_IN = 2

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

        /** facebook login **/
        // Create Callback Manager
        val facebookCallbackManager = CallbackManager.Factory.create()

        // Set Permission
        binding.buttonSocialLoginFacebook.setPermissions("email", "public_profile")

        // Callback registration
        binding.buttonSocialLoginFacebook.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(result: LoginResult?) {
                Timber.d("facebook:onSuccess:$result")
                loginWithFacebookIdToken(result!!.accessToken)
            }

            override fun onCancel() {
                Timber.d("facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Timber.e(error)
            }
        })

        facebookResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            val requestCode = intent!!.getIntExtra("requestCode", 0)
            val resultCode = it.resultCode
            val data = it.data
            if (requestCode == FACEBOOK_RC_SIGN_IN && resultCode == RESULT_OK){
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }

        /** google login **/
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Activity Callback Function(instead of startActivityForResult)
        googleResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    Timber.d("Login Success")
                    val account = task.getResult(ApiException::class.java)
                    // Signed in successfully
                    loginWithGoogleIdToken(account)
                } catch (e: ApiException) {
                    Timber.w("signInResult:failed code=" + e.statusCode)
                    loginWithGoogleIdToken(null)
                }
            }
            // result canceled됨...
        }

        binding.buttonSocialLoginGoogle.setOnClickListener { result ->
            when (result.id) {
                R.id.button_social_login_google -> {
                    val signInIntent = googleSignInClient.signInIntent
                    googleResultLauncher.launch(signInIntent)
                    // facebook login 버튼처럼 따로 logout 기능이 없어서 로그인 하자마자 로그아웃 시켰습니다
                    googleSignInClient.signOut()
                }
                else -> {
                    Timber.w("버튼 터치 과정에서 오류가 발생했습니다.")
                }
            }
        }

        //signup 버튼
        binding.buttonSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }

        // fetch Responses
        viewModel.fetchLoginResponse.observe(this, { response ->
            if(response.isSuccessful){
                sharedPreferences.edit {
                    putString(TOKEN, response.headers()["Authentication"])
                    putInt(CURRENT_USER_ID, response.body()!!.string().toInt())
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
                //Toast.makeText(this, "Error code: " + response.code(), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchDummy.observe(this, {
            Timber.d("SignInToken:$it")
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        finishAffinity()
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

    private fun loginWithGoogleIdToken(account: GoogleSignInAccount?) {
        if(account != null){
            setClipboard(this, account.idToken!!)
            viewModel.getResponseByGoogleLogin(account.idToken!!)
        }
    }

    private fun loginWithFacebookIdToken(token: AccessToken){
        setClipboard(this, token.token)
        viewModel.getResponseByFacebookLogin(token.token)
    }

    private fun setClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    companion object{
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
    }
}