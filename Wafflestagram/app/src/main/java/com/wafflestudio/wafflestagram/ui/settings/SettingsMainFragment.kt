package com.wafflestudio.wafflestagram.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentSettingsMainBinding
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsMainFragment: Fragment() {

    private lateinit var binding: FragmentSettingsMainBinding
    private lateinit var callback: OnBackPressedCallback

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var _context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _context = container!!.context
        binding = FragmentSettingsMainBinding.inflate(inflater, container, false)

        (activity as SettingsActivity).replaceTitle(SettingsActivity.SETTINGS_MAIN_FRAGMENT)

        /** fragments in setting (implemented) **/
        binding.buttonPersonalInfo.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SETTING_PERSONAL_INFO_FRAGMENT, 1)
        }

        binding.buttonSecurity.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SETTING_SECURITY_FRAGMENT, 1)
        }

        binding.buttonAccount.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SETTING_ACCOUNT_FRAGMENT, 1)
        }

        /** other menu in setting (not yet implemented) **/

        // sign out button (include social login sign out)
        binding.buttonSignout.setOnClickListener {
            // Remove Token(Sign Out)
            sharedPreferences.edit{
                putString(TOKEN, "")
                putInt(CURRENT_USER_ID, -1)
            }

            // Facebook Sign Out
            val accessToken = AccessToken.getCurrentAccessToken()
            if(accessToken != null && !accessToken.isExpired) {
                LoginManager.getInstance().logOut()
            }

            // Google Sign Out
            if(GoogleSignIn.getLastSignedInAccount(_context) != null) {
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_web_client_id))
                    .requestEmail()
                    .build()

                // Build a GoogleSignInClient with the options specified by gso.
                val googleSignInClient = GoogleSignIn.getClient(_context, gso)
                googleSignInClient.signOut()
            }

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val SETTING_MAIN_FRAGMENT = 0
        const val SETTING_PERSONAL_INFO_FRAGMENT = 1
        const val SETTING_SECURITY_FRAGMENT = 2
        const val SETTING_ACCOUNT_FRAGMENT = 3

        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
    }
}