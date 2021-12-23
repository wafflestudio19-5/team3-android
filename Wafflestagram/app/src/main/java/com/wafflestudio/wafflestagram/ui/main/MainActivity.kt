package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.wafflestagram.databinding.ActivityMainBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val feedFragment = FeedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        */
        setFragment(0)
        binding.tabLayoutMain.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> setFragment(0)
                    // 1 -> search, 2 -> user Fragment
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> feedFragment.setRecyclerviewPosition(0)
                    // 1 -> search, 2 -> user Fragment
                }
            }

        })
    }

    private fun setFragment(fragmentNum : Int){
        val fb = supportFragmentManager.beginTransaction()
        when(fragmentNum){
            0 -> {
                fb.replace(binding.fragmentContainerViewMain.id, feedFragment).commit()
            }

        }
    }
}