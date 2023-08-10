package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.AttendanceAdapter
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentAttendanceBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.util.Constants
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceFragment :
    BaseFragment<FragmentAttendanceBinding>(FragmentAttendanceBinding::inflate) {

    lateinit var adapter: AttendanceAdapter
    private val myProfile: ProfileViewModel by viewModels()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var recyclerview: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = binding.swipe

        loadData()
        topBar()

        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        adapter = AttendanceAdapter()
        recyclerview = binding.rec
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        binding.permission.setOnClickListener {
            findNavController().navigate(R.id.action_attendanceFragment_to_bottomSheetAttendance)
        }


    }

    private fun hideLoading() {
        progressDialog.show()
    }

    private fun topBar() {
        myProfile.getProfileUser(dataUser!!.id_pegawai)
        myProfile.profile.observe(viewLifecycleOwner) {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!.read
                    response.map { data ->
                        binding.include.namas.text = data.nama
                        binding.include.jabatan.text = data.jabatan
                        Glide.with(this)
                            .load(Constants.PROFILE_USER + data.image)
                            .into(binding.include.profileImage)
                    }
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Error -> {
                    handleApiError(it.message)
                }
            }
        }

    }

    private fun loadData() {
        viewModel.getAttendanceList(dataUser!!.id_pegawai)
        viewModel.getAttendanceUser(dataUser!!.id_pegawai)

        viewModel.attendanceUser.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            swipeRefreshLayout.isRefreshing = false
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!
                    binding.hadir.text = response.hadir.toString()
                    binding.alpa.text = response.alfa.toString()
                    binding.izin.text = response.izin.toString()
                }

                is NetworkResult.Error -> {
                    Log.d("attendance error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                is NetworkResult.Loading -> {
                    swipeRefreshLayout.isRefreshing = true
                    showLoading()
                }

            }
        }

        viewModel.attendanceList.observe(viewLifecycleOwner) { it ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            swipeRefreshLayout.isRefreshing = false
            when (it) {
                is NetworkResult.Success -> {
                    val data = it.data!!.map { it }
                    adapter.differ.submitList(data)
                }

                is NetworkResult.Error -> {
                    Log.d("attendance list error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                is NetworkResult.Loading -> showLoading()
            }
        }
    }

    private fun showLoading() {
        progressDialog.progressHelper.barColor =
            ContextCompat.getColor(requireContext(), R.color.gradient_end_color)
        progressDialog.titleText = "Loading"
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

}