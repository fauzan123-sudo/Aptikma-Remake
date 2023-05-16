package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.AdapterPotongan
import com.example.aptikma_remake.data.adapter.AdapterTunjangan
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentSallaryBinding
import com.example.aptikma_remake.ui.base.BaseFragment
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
    lateinit var tokenManager: TokenManager
    private val viewModel: SallaryViewModel by viewModels()
    lateinit var adapter: AdapterTunjangan
    private lateinit var adabter: AdapterPotongan
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

        val idUser = tokenManager.getToken()
        viewModel.getSallary(idUser!!)
        viewModel.sallary.observe(viewLifecycleOwner) { it ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!
                    if (response.status == 0) {
                        handleApiError("belum ada data")
                    } else {
                        val value = response.gaji_pokok
                        val myNumber = NumberFormat.getNumberInstance(Locale.US)
                            .format(value)
                            .replace(",", ".")
                        binding.nominalGajiPokok.text = "Rp. $myNumber"
                        val dataTunjangan = response.detail_tunjangan.map { it }
                        val dataPotongan = response.detail_potongan.map { it }
                        if (dataTunjangan.isEmpty()) {
                            binding.recTunjangan.visibility = View.GONE
                            binding.tvNoDataTunjangan.visibility = View.VISIBLE
                        } else {
                            binding.recTunjangan.visibility = View.VISIBLE
                            binding.tvNoDataTunjangan.visibility = View.GONE
                            adapter.differ.submitList(dataTunjangan)
                        }
                        if (dataPotongan.isEmpty()) {
                            binding.tvNoDataPotongan.visibility = View.VISIBLE
                            binding.recPotongan.visibility = View.GONE
                        } else {
                            binding.tvNoDataPotongan.visibility = View.GONE
                            binding.recPotongan.visibility = View.VISIBLE
                            adabter.differ.submitList(dataPotongan)
                        }
                        val valueGajiBersih = response.gaji_bersih
                        val myNumbers = NumberFormat.getNumberInstance(Locale.US)
                            .format(valueGajiBersih)
                            .replace(",", ".")
                        binding.gajiBersih.text = "Rp. $myNumbers"

                        binding.gbN.text = "Rp. $myNumbers"
                    }
                }

                is NetworkResult.Error -> {
                    val error = it.message.toString()
                    handleApiError(error)
                    Log.e("myErrorResponse", error)
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