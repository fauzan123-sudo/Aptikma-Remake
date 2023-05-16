package com.example.aptikma_remake.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentProfileBinding
import com.example.aptikma_remake.ui.activity.Login
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.util.Helper
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var tokenManager: TokenManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = binding.swipe
        loadData()

        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        binding.logout.setOnClickListener {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Konfirmasi Logout")
                .setContentText("Apakah Anda yakin ingin keluar?")
                .setConfirmText("Ya")
                .setConfirmClickListener { sweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                    tokenManager.deleteToken()
                    requireActivity().startActivity(Intent(requireContext(), Login::class.java))
                    requireActivity().finish()
                }
                .setCancelText("Tidak")
                .setCancelClickListener { sweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                }
                .show()

        }
    }

    private fun loadData() {
        val idUser = tokenManager.getToken()!!
        viewModel.getProfileUser(idUser)

        viewModel.profile.observe(viewLifecycleOwner) { it ->
            swipeRefreshLayout.isRefreshing = false
            when (it) {
                is NetworkResult.Success -> {
                    val data = it.data!!.read
                    data.map {
                        binding.name.text = it.nama
                        binding.address.text = it.alamat
                        binding.noTlp.text = it.no_tlp
                        binding.tmpLhr.text = it.tempat_lahir
                        binding.tglLhr.text = Helper().formatDate(it.tgl_lahir)
                        binding.nip.text = it.nip
                    }
                }

                is NetworkResult.Error -> {
                    Log.d("profile error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                is NetworkResult.Loading -> swipeRefreshLayout.isRefreshing = true
            }
        }
    }
}