package com.wafflestudio.wafflestagram.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchInterface {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(this)
        searchLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.recyclerViewSearch.apply {
            adapter = searchAdapter
            layoutManager = searchLayoutManager
        }

        binding.imageButton.setOnClickListener {
            viewModel.search(binding.searchView.text.toString())
        }

        viewModel.page.observe(this, {response ->
            if(response.isSuccessful){
                searchAdapter.updateData(response.body()!!.content)
            }

        })
    }

    /*override fun checkFollowing(id: Int) : Response<Boolean> {
        return viewModel.checkFollowing(id)
    }*/

    override fun follow(id: Int){
        viewModel.follow(id)
    }

    override fun unfollow(id: Int){
        viewModel.unfollow(id)
    }
}