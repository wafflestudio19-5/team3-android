package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBinding
import com.wafflestudio.wafflestagram.databinding.IconUserProfileBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() ,FeedInterface {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager
    private var itemPosition : Int = -1
    private var pageNumber: Int = 0
    private var totalPage: Int = 1

    private lateinit var userProfileBinding: IconUserProfileBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        userProfileBinding = IconUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedAdapter = FeedAdapter(this)
        feedLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        binding.buttonLogo.setOnClickListener{
            binding.recyclerViewFeed.smoothScrollToPosition(0)
        }

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }
        if(pageNumber < totalPage){
            val request = FeedPageRequest(pageNumber, 10)
            pageNumber++
            viewModel.getFeeds(request)
        }



        viewModel.getMe()

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(Color.parseColor("#F6F6F6"),Color.parseColor("#D5D5D5"),Color.parseColor("#A6A6A6"),Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            feedAdapter.clearData()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
            pageNumber = 0
            val request = FeedPageRequest(pageNumber, 10)
            pageNumber++
            viewModel.getFeeds(request)
        }

        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!binding.recyclerViewFeed.canScrollVertically(1) && feedAdapter.itemCount > 9){
                    feedAdapter.deleteLoading()
                    val request = FeedPageRequest(pageNumber, 10)
                    pageNumber++
                    viewModel.getFeeds(request)
                }
            }
        })

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.addData(response.body()!!.content.toMutableList())
                totalPage = response.body()!!.totalPages
            }else if(response.code() == 401){
                // 토큰 만료시 토큰 삭제

                    Toast.makeText(context,sharedPreferences.getString(MainActivity.TOKEN, ""),Toast.LENGTH_LONG ).show()
                sharedPreferences.edit {
                    putString(TOKEN, "")

                }
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })

        viewModel.user.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.updateData(response.body()!!)
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(userProfileBinding.imageProfile)
            }else if(response.code() == 401){

            }
        })

        viewModel.likeResponse.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.changeData(response.body()!!, itemPosition)
            }
        })
    }

    fun setRecyclerviewPosition(position: Int){
        binding.recyclerViewFeed.smoothScrollToPosition(position)
    }

    override fun like(id:Int ,position: Int){
        viewModel.like(id)
        itemPosition = position
    }

    override fun unlike(id: Int, position: Int){
        viewModel.unlike(id)
        itemPosition = position
    }

    companion object{
        const val TOKEN = "token"
    }

}