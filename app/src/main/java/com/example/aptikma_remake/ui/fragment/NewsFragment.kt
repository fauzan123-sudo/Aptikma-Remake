package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.NewsAdapter
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentNewsBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.BreakingNewsViewModel
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNewsBinding>(FragmentNewsBinding::inflate) {

    private val viewModel: BreakingNewsViewModel by viewModels()
    private lateinit var notificationAdapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationAdapter = NewsAdapter(requireContext())

        mySwipeRefreshLayout()
        loadData()

        binding.recNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    private fun loadData() {
        viewModel.getNews()
        viewModel.news.observe(viewLifecycleOwner) { result ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 5000)
            swipeRefreshLayout.isRefreshing = false
            hideLoading()
            when (result) {
                is NetworkResult.Loading -> {
                    showLoading()
                    swipeRefreshLayout.isRefreshing = true
                }
                is NetworkResult.Success -> {
                    val notifications = result.data ?: emptyList()
                    notificationAdapter.differ.submitList(notifications)
                    Log.d("on Success", "${result.data}")
                }
                is NetworkResult.Error -> {
                    Log.d("on Error", "${result.message}")
                    handleApiError(result.message)
                }
            }
        }
    }

    private fun mySwipeRefreshLayout() {
        swipeRefreshLayout = binding.swipe
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun showLoading() {
        Log.d("is Loading", "showLoading: ")
        progressDialog.progressHelper.barColor =
            ContextCompat.getColor(requireContext(), R.color.gradient_end_color)
        progressDialog.titleText = "Loading"
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }
}