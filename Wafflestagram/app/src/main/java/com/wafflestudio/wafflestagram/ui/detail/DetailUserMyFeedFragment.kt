package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentDetailUserMyFeedBinding
import com.wafflestudio.wafflestagram.databinding.FragmentUserMyFeedBinding
import com.wafflestudio.wafflestagram.ui.main.UserFragment
import com.wafflestudio.wafflestagram.ui.main.UserPhotoAdapter
import com.wafflestudio.wafflestagram.ui.main.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserMyFeedFragment: Fragment() {

    private lateinit var binding: FragmentDetailUserMyFeedBinding
    private val viewModel: DetailFeedViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userAdapter: DetailUserAdapter
    private lateinit var userLayoutManager: GridLayoutManager
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false
    private var isFirst: Boolean = true
    private var isLoading: Boolean = false
    private var userId : Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailUserMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val id = intent.getIntExtra("id", -1)
        userId = arguments!!.getInt("id", -1)

        // Setting Adapter and LayoutManager
        userAdapter = DetailUserAdapter{startActivity(
            Intent(activity, DetailFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", userId)
        )}
        userLayoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(userAdapter.getItemViewType(position)){
                        UserPhotoAdapter.VIEW_TYPE_LOADING -> 3
                        UserPhotoAdapter.VIEW_TYPE_FEED -> 1
                        else -> throw IllegalStateException("viewType must be 0 or 1")
                    }
                }
            }
        }
        binding.recyclerViewPhotos.apply {
            adapter = userAdapter
            layoutManager = userLayoutManager
        }

        viewModel.getFeedsByUserId(userId, 0, 12)

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                isLoading = false
                totalPage = response.body()!!.totalPages
                pageNumber = response.body()!!.pageNumber + 1
                if(pageNumber > 0 && userAdapter.itemCount > 0){
                    userAdapter.deleteLoading()
                    userAdapter.notifyItemRemoved(userAdapter.itemCount)
                }
                isLast = response.body()!!.last
                isFirst = response.body()!!.fisrt
                if(isFirst){
                    userAdapter.updateData(response.body()!!.content.toMutableList())
                    userAdapter.notifyDataSetChanged()
                }else{
                    userAdapter.addData(response.body()!!.content.toMutableList())
                }
                if(!response.body()!!.last){
                    userAdapter.addLoading()
                    userAdapter.notifyItemInserted(userAdapter.itemCount)
                }
            }else{

            }
        })
    }

    fun getFeeds(){
        if(!isLoading){
            if(!isLast){
                isLoading = true
                viewModel.getFeedsByUserId(userId, pageNumber, 12)
            }
        }
    }
}