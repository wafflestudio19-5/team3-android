package com.wafflestudio.wafflestagram.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.wafflestagram.databinding.ActivityMainBinding
import com.wafflestudio.wafflestagram.databinding.IconUserProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userProfileBinding: IconUserProfileBinding

    private val feedFragment = FeedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userProfileBinding = IconUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        */
        setFragment(FEED_FRAGMENT)

        binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
        binding.tabLayoutMain.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    FEED_FRAGMENT -> setFragment(FEED_FRAGMENT)
                    // 1 -> search, 2 -> user Fragment

                    USER_FRAGMENT -> {
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
                    // 1 -> search, 2 -> user Fragment
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

        }
    }

    companion object{
        const val FEED_FRAGMENT = 0
        const val SEARCH_FRAGMENT = 1
        const val USER_FRAGMENT = 2
    }
}