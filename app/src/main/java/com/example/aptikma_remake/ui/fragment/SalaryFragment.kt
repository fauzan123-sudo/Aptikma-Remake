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
import com.example.aptikma_remake.data.adapter.AdapterPotongan
import com.example.aptikma_remake.data.adapter.AdapterTunjangan
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentSallaryBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.ui.viewModel.SallaryViewModel
import com.example.aptikma_remake.util.Constants
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class SalaryFragment : BaseFragment<FragmentSallaryBinding>(FragmentSallaryBinding::inflate) {

    private val myProfile: ProfileViewModel by viewModels()
    private val viewModel: SallaryViewModel by viewModels()
    lateinit var adapter: AdapterTunjangan
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapterCut: AdapterPotongan
    private lateinit var recyclerview: RecyclerView
    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        swipeRefreshLayout = binding.swipe

        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        topBar()
        goToProfile()
        goToNotification()

    }

    private fun goToNotification() {
        binding.include.badgeBeritaAcara.setOnClickListener {
            findNavController().navigate(R.id.action_salaryFragment_to_notificationFragment)
        }
    }


    private fun goToProfile() {
        binding.include.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_salaryFragment_to_profileFragment)
        }
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

        recyclerView = binding.recPotongan
        recyclerview = binding.recTunjangan

        adapter = AdapterTunjangan()
        adapterCut = AdapterPotongan()

        recyclerview.adapter = adapter
        recyclerView.adapter = adapterCut
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val idUser = dataUser!!
        viewModel.getSallary(idUser.id_pegawai)
        viewModel.sallary.observe(viewLifecycleOwner) { it ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            swipeRefreshLayout.isRefreshing = false
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!
                    if (response.status == 0) {
                        handleApiError(getString(R.string.no_data))
                    } else {
                        val value = response.gaji_pokok
                        val potongan = response.detail_tunjangan
                        Log.d("potongan ", "$potongan")
                        val myNumber = NumberFormat.getNumberInstance(Locale.US)
                            .format(value)
                            .replace(",", ".")
                        binding.nominalGajiPokok.text = "Rp. $myNumber"
                        val allowanceSalary = response.detail_tunjangan.map { it }
                        val salaryCuts = response.detail_potongan.map { it }
                        if (allowanceSalary.isEmpty()) {
                            Log.d("tunjangan kosong", "tunjangan sedang kosong")
                            binding.recTunjangan.visibility = View.GONE
                            binding.tvNoDataTunjangan.visibility = View.VISIBLE
                        } else {
                            binding.recTunjangan.visibility = View.VISIBLE
                            binding.tvNoDataTunjangan.visibility = View.GONE
                            Log.d("data tunjangan", "$allowanceSalary")
                            adapter.differ.submitList(allowanceSalary)
                        }
                        if (salaryCuts.isEmpty()) {
                            binding.tvNoDataPotongan.visibility = View.VISIBLE
                            binding.recPotongan.visibility = View.GONE
                        } else {
                            binding.tvNoDataPotongan.visibility = View.GONE
                            binding.recPotongan.visibility = View.VISIBLE
                            adapterCut.differ.submitList(salaryCuts)
                        }
                        val netSalary = response.gaji_bersih
                        val myNumbers = NumberFormat.getNumberInstance(Locale.US)
                            .format(netSalary)
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

                is NetworkResult.Loading -> {
                    showLoading()
                    swipeRefreshLayout.isRefreshing = true
                }
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

    private fun hideLoading() {
        progressDialog.show()
    }

}