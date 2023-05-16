package com.example.aptikma_remake.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.SpinnerAdapter
import com.example.aptikma_remake.data.model.SpinnerList
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentBottomSheetAttendanceBinding
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class BottomSheetAttendance : BottomSheetDialogFragment(){

    lateinit var progressDialog: SweetAlertDialog

    private var _binding: FragmentBottomSheetAttendanceBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState).apply {
            // window?.setDimAmount(0.2f) // Set dim amount here
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBottomSheetAttendanceBinding.inflate(inflater, container, false)
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        return binding.root
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val spinner = binding.spinnerType
        val id_pegawai = tokenManager.getToken()

        viewModel.spinnerList()
        viewModel.spinnerList.observe(viewLifecycleOwner) { it ->
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            when (it) {
                is NetworkResult.Success -> {
                    val list: ArrayList<SpinnerList> = ArrayList()

                    it.data!!.map {
                        list.add(
                            SpinnerList(
                                it.created_by,
                                it.created_date,
                                it.edited_by,
                                it.edited_date,
                                it.id_jenis_izin,
                                it.nama
                            )
                        )
                    }
                    val spinnerAdapter = SpinnerAdapter(requireContext(), list)
                    spinner.adapter = spinnerAdapter
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val clickedItem: SpinnerList =
                                parent?.getItemAtPosition(position) as SpinnerList
                            val idSpinner = clickedItem.id_jenis_izin
                            Log.d("idSpinner", idSpinner)
                            Toast.makeText(requireContext(), idSpinner, Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                is NetworkResult.Error -> {
                    Log.d("attendance list error", "${it.data}")
                    val error = it.message.toString()
                    handleApiError(error)
                }

                is NetworkResult.Loading -> showLoading()
            }

        }


//        val offsetFromTop = 200
//        (dialog as? BottomSheetDialog)?.behavior?.apply {
//            isFitToContents = false
//            expandedOffset = offsetFromTop
//            state = BottomSheetBehavior.STATE_EXPANDED
//        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (R.id.radioHour == checkedId) {
                setTimePicker()
            } else if(R.id.radioDay == checkedId){
                setTimeCalendar()
            }else{
                binding.txtFrom.isEnabled = false
                binding.txtUntil.isEnabled = false
            }

        }

    }

    private fun setTimePicker() {
        binding.txtFrom.isEnabled = true
        binding.txtUntil.isEnabled = true
        binding.txtFrom.setOnClickListener {
        openTimePickerFrom()
        }
        binding.txtUntil.setOnClickListener {
            openTimePickerUntil()
        }
    }

    private fun setTimeCalendar(){
        binding.txtFrom.isEnabled = true
        binding.txtUntil.isEnabled = true
        binding.txtFrom.setOnClickListener {
            openCalendarFrom()
        }
        binding.txtUntil.setOnClickListener {
            openCalendarUntil()
        }
    }

    private fun openCalendarFrom() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("")
            .build()
        datePicker.show(childFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
            val date = dateFormatter.format(Date(it))
            binding.txtFrom.text = date
            Toast.makeText(requireContext(), "$date is selected", Toast.LENGTH_LONG).show()

        }

        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(
                requireContext(),
                "${datePicker.headerText} is cancelled",
                Toast.LENGTH_LONG
            ).show()
        }

        datePicker.addOnCancelListener {
            Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun openCalendarUntil() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("")
            .build()
        datePicker.show(childFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
            val date = dateFormatter.format(Date(it))
            binding.txtUntil.text = date
            Toast.makeText(requireContext(), "$date is selected", Toast.LENGTH_LONG).show()

        }

        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(
                requireContext(),
                "${datePicker.headerText} is cancelled",
                Toast.LENGTH_LONG
            ).show()
        }

        datePicker.addOnCancelListener {
            Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun openTimePickerFrom() {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(8)
            .setMinute(0)
            .setTitleText("")
            .build()
        picker.show(childFragmentManager, "MyTag")

        picker.addOnPositiveButtonClickListener {
            val h = picker.hour
            val m = picker.minute
            binding.txtFrom.text = "$h.$m"
        }
    }

    private fun openTimePickerUntil() {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(8)
            .setMinute(0)
            .setTitleText("")
            .build()
        picker.show(childFragmentManager, "MyTag")

        picker.addOnPositiveButtonClickListener {
            val h = picker.hour
            val m = picker.minute
            binding.txtUntil.text = "$h.$m"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        progressDialog.progressHelper.barColor =
            ContextCompat.getColor(requireContext(), R.color.gradient_end_color)
        progressDialog.titleText = "Loading"
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

}