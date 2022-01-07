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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wafflestudio.wafflestagram.R
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

    // for Facebook Login
    private lateinit var callbackManager: CallbackManager

    // for Google Login
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 2

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

        //signup 버튼
        binding.buttonSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Facebook Login
        callbackManager = CallbackManager.Factory.create()

        // TODO: Replace deprecated code
        // TODO: 로그인 상태 체크 코드 작성(로그인 여부 등 판단)
        binding.buttonSocialLoginFacebook.setReadPermissions("email")
        binding.buttonSocialLoginFacebook.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> { // Callback registration
            override fun onSuccess(result: LoginResult) { // 로그인 성공 시
                Timber.d("facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() { // 로그인 취소 시
                Timber.d("facebook:onCancel")
            }

            override fun onError(error: FacebookException) { // 로그인 에러 시
                Timber.d("facebook:onError", error)
            }
        })

        // Google Login
        createGoogleRequest()
        binding.buttonSocialLoginGoogle.setOnClickListener { signIn() }

        // fetch Responses
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
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    // get social Login Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: distinguish Facebook and Google

        // Pass the activity result back to the Facebook SDK
        // TODO: replace deprecated code with Activity Result APIs
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.d("firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.w("Google sign in failed", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
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

    private fun handleFacebookAccessToken(token: AccessToken) {
        Timber.d("handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    // Facebook Login 성공
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Facebook Login 실패
                    Timber.w("signInwithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    // Google Login 성공
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Google Login 실패
                    Timber.w("signInwithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun createGoogleRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        // (google) Login
        val signInIntent = googleSignInClient.signInIntent
        // TODO: Replace deprecated code
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?){
        // Login 상태에 따라 UI update
        if(user != null){ // user data 있는 경우(로그인 되어있는 경우)
            viewModel.getResponseBySocialLogin(user.email!!)
        }
    }

    companion object{
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
        const val FACEBOOK = "facebook"
        const val GOOGLE = "google"
    }
}