package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentProfileBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var tokenManager: TokenManager

    private val viewModel:ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idPegawai = tokenManager.getToken()
        if (idPegawai != null) {
            viewModel.getProfileUser(idPegawai)
            Log.d("theTok", "$idPegawai")
        }
        viewModel.profile.observe(viewLifecycleOwner){
            when(it){
                is NetworkResult.Success ->{
                    val data = it.data!!.read
                    data.map {
                        binding.name.text = it.nama
                        binding.address.text = it.alamat
                        binding.noTlp.text = it.no_tlp
                        binding.tmpLhr.text = it.tempat_lahir
                        binding.tglLhr.text = it.tgl_lahir
                        binding.nip.text = it.nip
                    }
                }

                is NetworkResult.Error ->{
                    Log.d("profile error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }
                else -> Log.d("error else", "$it")
            }
        }

    }

}