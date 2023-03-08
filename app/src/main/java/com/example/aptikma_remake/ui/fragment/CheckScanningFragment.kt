package com.example.aptikma_remake.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aptikma_remake.R
import com.example.aptikma_remake.databinding.FragmentCheckScanningBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CheckScanningFragment :BaseFragment<FragmentCheckScanningBinding>(FragmentCheckScanningBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkTime()
        checkScan()

    }

    private fun checkScan() {
        TODO("Not yet implemented")
    }

    private fun checkTime() {
        val current1 = LocalDate.now()
        val current2 = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current3 = LocalDateTime.now().format(formatter)
        val time = Calendar.getInstance().time


    }
}