package com.wafflestudio.wafflestagram.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class ViewPagerDetailUserAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = listOf<Fragment>(DetailUserMyFeedFragment(), DetailUserTaggedFeedFragment())
    private var id = -1

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]
        val bundle = Bundle()

        bundle.putInt("id", id)
        fragment.arguments = bundle

        return fragment
    }

    fun setId(id: Int){
        this.id = id
        this.notifyDataSetChanged()
    }
}