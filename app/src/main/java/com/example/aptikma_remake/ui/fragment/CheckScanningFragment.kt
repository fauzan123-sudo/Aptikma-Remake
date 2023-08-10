package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import android.view.View
import com.example.aptikma_remake.databinding.FragmentCheckScanningBinding
import com.example.aptikma_remake.ui.base.BaseFragment

class CheckScanningFragment :BaseFragment<FragmentCheckScanningBinding>(FragmentCheckScanningBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        checkTime()
        checkScan()

    }

    private fun checkScan() {
        TODO("Not yet implemented")
    }

//    private fun checkTime() {
//        val current1 = LocalDate.now()
//        val current2 = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//        val current3 = LocalDateTime.now().format(formatter)
//        val time = Calendar.getInstance().time
//
//
//    }
}