package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.FragmentUserMyFeedBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class UserMyFeedFragment: Fragment() {

    private lateinit var binding: FragmentUserMyFeedBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var userPhotoAdapter: UserPhotoAdapter
    private lateinit var userLayoutManager: GridLayoutManager
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false
    private var isFirst: Boolean = true
    private var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = sharedPreferences.getInt(UserFragment.CURRENT_USER_ID, -1)
        Timber.d(currentUserId.toString())

        // Setting Adapter and LayoutManager
        userPhotoAdapter = UserPhotoAdapter {
            startActivity(
                Intent(context, DetailFeedActivity::class.java)
                    .putExtra("position", it)
                    .putExtra("userId", currentUserId)
            )
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(userPhotoAdapter.getItemViewType(position)){
                        UserPhotoAdapter.VIEW_TYPE_LOADING -> 3
                        UserPhotoAdapter.VIEW_TYPE_FEED -> 1
                        else -> throw IllegalStateException("viewType must be 0 or 1")
                    }
                }
            }
        }
        binding.recyclerViewPhotos.apply {
            adapter = userPhotoAdapter
            layoutManager = userLayoutManager
        }

        /*
        binding.recyclerViewPhotos.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (userLayoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = userPhotoAdapter.itemCount-1

                if(!isLoading){
                    if(!binding.recyclerViewPhotos.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !isLast){
                        isLoading = true
                        viewModel.getMyFeeds(pageNumber, 12)
                    }
                }
            }
        })
         */
        viewModel.getMyFeeds(0, 12)

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                isLoading = false
                totalPage = response.body()!!.totalPages
                pageNumber = response.body()!!.pageNumber + 1
                if(pageNumber > 0 && userPhotoAdapter.itemCount > 0){
                    userPhotoAdapter.deleteLoading()
                    userPhotoAdapter.notifyItemRemoved(userPhotoAdapter.itemCount)
                }
                isLast = response.body()!!.last
                isFirst = response.body()!!.fisrt
                if(isFirst){
                    userPhotoAdapter.updatePhotos(response.body()!!.content.toMutableList(), context!!)
                    userPhotoAdapter.notifyDataSetChanged()
                }else{
                    userPhotoAdapter.addPhotos(response.body()!!.content.toMutableList())
                    userPhotoAdapter.notifyItemRangeInserted((response.body()!!.pageNumber) * 10, response.body()!!.numberOfElements)
                }
                if(!response.body()!!.last){
                    userPhotoAdapter.addLoading()
                    userPhotoAdapter.notifyItemInserted(userPhotoAdapter.itemCount)
                }
            }else{

            }
        })
    }

    fun getFeeds(){
        if(!isLoading){
            if(!isLast){
                isLoading = true
                viewModel.getMyFeeds(pageNumber, 12)
            }
        }
    }
}