package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.aghajari.zoomhelper.ZoomHelper
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.wafflestagram.databinding.ActivityMainBinding
import com.wafflestudio.wafflestagram.databinding.IconUserProfileBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userProfileBinding: IconUserProfileBinding

    private val feedFragment = FeedFragment()
    private val searchFragment = SearchFragment()
    private val userFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userProfileBinding = IconUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        */

        // set max zoom scale
        ZoomHelper.getInstance().maxScale = 3f

        setFragment(FEED_FRAGMENT)
        binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
        binding.tabLayoutMain.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    FEED_FRAGMENT -> setFragment(FEED_FRAGMENT)
                    // 1 -> search, 2 -> user Fragment
                    SEARCH_FRAGMENT -> setFragment(SEARCH_FRAGMENT)
                    USER_FRAGMENT -> {
                        setFragment(USER_FRAGMENT)
                        userProfileBinding.imageProfile.borderColor= Color.parseColor("#FF000000")
                        binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if(tab!!.position == 2){
                    userProfileBinding.imageProfile.borderColor = Color.parseColor("#00ff0000")
                    binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    FEED_FRAGMENT -> feedFragment.setRecyclerviewPosition(0)

                }
            }

        })
    }

    private fun setFragment(fragmentNum : Int){
        val fb = supportFragmentManager.beginTransaction()
        when(fragmentNum){
            FEED_FRAGMENT -> {
                fb.replace(binding.fragmentContainerViewMain.id, feedFragment).commit()
            }
            SEARCH_FRAGMENT -> {
                fb.replace(binding.fragmentContainerViewMain.id, searchFragment).commit()
            }
            USER_FRAGMENT -> {
                //fb.replace(binding.fragmentContainerViewMain.id, userFragment).commit()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    companion object{
        const val FEED_FRAGMENT = 0
        const val SEARCH_FRAGMENT = 1
        const val USER_FRAGMENT = 2
    }
}