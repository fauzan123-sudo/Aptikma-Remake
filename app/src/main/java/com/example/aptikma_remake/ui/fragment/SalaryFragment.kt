package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.AdapterPotongan
import com.example.aptikma_remake.data.adapter.AdapterTunjangan
import com.example.aptikma_remake.data.adapter.AttendanceAdapter
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentSallaryBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.ui.viewModel.SallaryViewModel
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SalaryFragment : BaseFragment<FragmentSallaryBinding>(FragmentSallaryBinding::inflate) {

    @Inject
    lateinit var tokenManager:TokenManager

    private val viewModel: SallaryViewModel by viewModels()
    lateinit var adapter: AdapterTunjangan
    lateinit var adabter:AdapterPotongan
    private lateinit var recyclerview: RecyclerView
    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recPotongan
        recyclerview = binding.recTunjangan

        adapter = AdapterTunjangan()
        adabter = AdapterPotongan()

        recyclerview.adapter = adapter
        recyclerView.adapter = adabter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val idPegawai = tokenManager.getToken()
        if (idPegawai != null) {
            viewModel.getSallary(idPegawai)
            Log.d("theTok", "$idPegawai")
        }
        viewModel.sallary.observe(viewLifecycleOwner){
            when(it){
                is NetworkResult.Success ->{
                    val value = it.data!!.gaji_pokok
                    val myNumber = NumberFormat.getNumberInstance(Locale.US)
                        .format(value)
                        .replace(",", ".")
                    binding.nominalGajiPokok.text = "Rp. $myNumber"
                    val dataTunjangan = it.data!!.detail_tunjangan.map { it }
                    val dataPotongan = it.data!!.detail_potongan.map { it }
                    adapter.differ.submitList(dataTunjangan)
                    adabter.differ.submitList(dataPotongan)
                    val valueGajiBersih = it.data!!.gaji_bersih
                    val myNumbers = NumberFormat.getNumberInstance(Locale.US)
                        .format(valueGajiBersih)
                        .replace(",", ".")
                    binding.gajiBersih.text = "Rp. $myNumbers"

                    binding.gbN.text = "Rp. $myNumbers"

                }

                is NetworkResult.Error -> {
                    val error = it.message.toString()
                    handleApiError(error)
                }

                else ->{
                    Log.d("loading", "")
                }
            }
        }

    }

}