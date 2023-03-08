package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val idPegawai = tokenManager.getToken()
        if (idPegawai != null) {
            viewModel.getAttendanceList(idPegawai)
            viewModel.getAttendanceUser(idPegawai)
            Log.d("theTok", "$idPegawai")
        }
        viewModel.attendanceUser.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {

                    binding.hadir.text = it.data!!.hadir.toString()
                    binding.alpa.text  = it.data!!.alfa.toString()
                    binding.izin.text  = it.data!!.izin.toString()
                }

                is NetworkResult.Error -> {
                    Log.d("attendance error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                else -> Log.d("attendance user else", "$it")

            }
        }

        viewModel.attendanceList.observe(viewLifecycleOwner){
            when(it){
                is NetworkResult.Success ->{
                    val data = it.data!!.map { it }
                    adapter.differ.submitList(data)
                }

                is NetworkResult.Error ->{
                    Log.d("attendance list error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                else -> Log.d("attendance list else", "$it")
            }
        }
    }

}