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
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.AttendanceAdapter
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentAttendanceBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AttendanceFragment :
    BaseFragment<FragmentAttendanceBinding>(FragmentAttendanceBinding::inflate) {

    @Inject
    lateinit var tokenManager: TokenManager

    lateinit var adapter: AttendanceAdapter

    private val viewModel: AttendanceViewModel by viewModels()

    private lateinit var recyclerview: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Init
        adapter = AttendanceAdapter()
        recyclerview = binding.rec
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        binding.permission.setOnClickListener {
            findNavController().navigate(R.id.action_attendanceFragment_to_bottomSheetAttendance)
        }

        val idUser = tokenManager.getToken()!!

        viewModel.getAttendanceList(idUser)
        viewModel.getAttendanceUser(idUser)

        viewModel.attendanceUser.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
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

                is NetworkResult.Loading -> showLoading()

            }
        }

        viewModel.attendanceList.observe(viewLifecycleOwner) { it ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
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