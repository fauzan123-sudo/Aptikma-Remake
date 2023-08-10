package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aptikma_remake.data.adapter.NewsAdapter
import com.example.aptikma_remake.databinding.FragmentNewsBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.NewsViewModel
import com.example.aptikma_remake.util.extension.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNewsBinding>(FragmentNewsBinding::inflate) {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var notificationAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationAdapter = NewsAdapter(requireContext())

        binding.recNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }

        viewModel.loadNotificationsByNip(dataUser!!.id_pegawai)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationsByNip.collect {
                    when(it){
                        is Resource.Loading -> {
                            // Handle loading state
                        }
                        is Resource.Success -> {
                            val notifications = it.data ?: emptyList()

                        }
                        is Resource.Error -> {
                            // Handle error state
                        }
                    }

                }
            }
        }

    }
}