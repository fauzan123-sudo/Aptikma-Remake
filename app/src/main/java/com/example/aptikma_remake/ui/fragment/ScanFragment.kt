package com.example.aptikma_remake.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentScanBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {
    private lateinit var codeScanner: CodeScanner

//    @Inject
//    lateinit var tokenManager: TokenManager
    private val viewModel: AttendanceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScanner()
        checkCameraPermission()
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                scanUser(it.toString())
                Log.d("result", "$it")
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun scanUser(resultScan: String) {
        viewModel.scanRequest(dataUser!!.id_pegawai,resultScan, "1")
        viewModel.scanUser.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            when (it) {
                is NetworkResult.Success -> {
                    viewModel.scanUser.removeObservers(viewLifecycleOwner)
                    val response = it.data!!
                    if (response.success == 1) {
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Berhasil Absen")
                            .setContentText("Anda berhasil absensi hari ini")
                            .setConfirmClickListener { sweetAlertDialog ->
                                sweetAlertDialog.dismissWithAnimation()
//                                findNavController().navigate(R.id.action_scanFragment_to_attendanceFragment)
                                val action =
                                    ScanFragmentDirections.actionScanFragmentToAttendanceFragment()
                                findNavController().navigate(action)
                            }
                            .show()
                    } else {
                        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(response.message)
                            .setContentText("Absen lagi")
                            .setConfirmText("Ya")
                            .setConfirmClickListener { sweetAlertDialog ->
                                sweetAlertDialog.dismissWithAnimation()
                                codeScanner.startPreview()
                            }
                            .setCancelText("Tidak")
                            .setCancelClickListener { sweetAlertDialog ->
                                sweetAlertDialog.dismissWithAnimation()
                                findNavController().navigate(R.id.action_scanFragment_to_attendanceFragment)
                            }
                            .show()
                    }
                }

                is NetworkResult.Loading -> showLoading()

                is NetworkResult.Error -> {
                    viewModel.scanUser.removeObservers(viewLifecycleOwner)
                    handleApiError(it.message)
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

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startScanner()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startScanner()
                } else {
                    showPermissionDeniedDialog()
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showPermissionDeniedDialog() {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Permission Denied")
            .setContentText("Camera permission is required to use this feature.")
            .setConfirmClickListener {
                it.dismissWithAnimation()
            }
            .show()
    }

    private fun startScanner() {
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}
