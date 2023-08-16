package com.example.aptikma_remake.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.SpinnerAdapter
import com.example.aptikma_remake.data.model.SpinnerList
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentBottomSheetAttendanceBinding
import com.example.aptikma_remake.ui.viewModel.AttendanceViewModel
import com.example.aptikma_remake.util.getData
import com.example.aptikma_remake.util.handleApiError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class BottomSheetAttendance : BottomSheetDialogFragment() {

    lateinit var progressDialog: SweetAlertDialog

    private var _binding: FragmentBottomSheetAttendanceBinding? = null

    private val binding get() = _binding!!

    private var dataUser = getData()

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState).apply {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val spinner = binding.spinnerType
//        val id_pegawai = dataUser?.id_pegawai

        setTextViewColorGrey()

        viewModel.spinnerList()
        viewModel.spinnerList.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 500)
            when (it) {
                is NetworkResult.Success -> {

                    val dataFromApi = it.data!!
                    if (dataFromApi.isNotEmpty()) {
                        val spinnerData: MutableList<SpinnerList> = dataFromApi.map {
                            SpinnerList(
                                it.created_by,
                                it.created_date,
                                it.edited_by,
                                it.edited_date,
                                it.id_jenis_izin,
                                it.nama
                            )
                        }.toMutableList()

                        val defaultItem = SpinnerList(
                            created_by = "",
                            created_date = "",
                            edited_by = "",
                            edited_date = "",
                            id_jenis_izin = "",
                            nama = "Pilih Jenis Perizinan"
                        )

                        spinnerData.add(0, defaultItem)

                        val spinnerAdapter = SpinnerAdapter(requireContext(), spinnerData)
                        spinner.adapter = spinnerAdapter
                        spinner.prompt = "Pilih Jenis Perizinan"
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.d("TAG", "onNothingSelected: ")
//                                    spinner.setSelection(0)
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = spinnerAdapter.getItem(position)
                                    Log.d("idSpinner", selectedItem!!.id_jenis_izin)
                                }

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

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (R.id.radioHour == checkedId) {
                with(binding) {
                    txtFrom.text = "-- : --"
                    txtUntil.text = "-- : --"
                    binding.txtFrom.setTextColor(requireContext().getColor(R.color._grey))
                    binding.txtUntil.setTextColor(requireContext().getColor(R.color._grey))
                    txtFrom.isEnabled = true
                    txtFrom.setOnClickListener {
                        val min = 8
                        val max = 15
                        val calendar = Calendar.getInstance()
                        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = calendar.get(Calendar.MINUTE)
                        showMaterialTimePicker(
                            currentHour,
                            currentMinute + 1,
                            min,
                            max
                        ) { selectTime ->
                            txtFrom.text = selectTime
                            if (txtFrom.text.toString() != "-- : --") {
                                binding.txtFrom.setTextColor(requireContext().getColor(R.color.black))
                            } else {
                                binding.txtFrom.setTextColor(requireContext().getColor(R.color._grey))
                            }
                        }
                    }
                    txtUntil.setOnClickListener {
                        if (txtFrom.text.toString() != "-- : --") {
                            val getStart = txtFrom.text.toString()
                            val getHour = getStart.split(":")[0].toInt()
                            showMaterialTimePicker(
                                getHour + 1,
                                0,
                                getHour + 1,
                                16
                            ) { selectTime ->
                                txtUntil.text = selectTime
                                if (txtUntil.text.toString() != "-- : --") {
                                    binding.txtUntil.setTextColor(requireContext().getColor(R.color.black))
                                } else {
                                    binding.txtUntil.setTextColor(requireContext().getColor(R.color._grey))
                                }
                            }
                        }
                    }
                }

            } else if (R.id.radioDay == checkedId) {
                with(binding) {
                    txtFrom.text = "-- : --"
                    txtUntil.text = "-- : --"
                    binding.txtFrom.setTextColor(requireContext().getColor(R.color._grey))
                    binding.txtUntil.setTextColor(requireContext().getColor(R.color._grey))
                    txtFrom.isEnabled = true
                    txtUntil.isEnabled = false
                    txtFrom.setOnClickListener {
                        val today = Calendar.getInstance()
                        val oneWeekLater = Calendar.getInstance()
                        oneWeekLater.add(Calendar.DAY_OF_MONTH, 7)
                        Log.d("check min", "${today.timeInMillis}")
                        Log.d("check max", "${oneWeekLater.timeInMillis}")
                        showDatePicker(
                            today.timeInMillis,
                            oneWeekLater.timeInMillis
                        ) { selectedDate ->
                            txtFrom.text = selectedDate
                            if (txtFrom.text.toString() != "-- : --") {
                                binding.txtFrom.setTextColor(requireContext().getColor(R.color.black))
                            } else {
                                binding.txtFrom.setTextColor(requireContext().getColor(R.color._grey))
                            }
                            txtUntil.isEnabled = txtFrom.text.toString() != "-- : --"
                        }
                    }
                    txtUntil.setOnClickListener {
                        if (txtFrom.text.toString() != "-- : --") {
                            val selectedDateText = binding.txtFrom.text.toString()
                            val selectedDateFormat =
                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val selectedDate = selectedDateFormat.parse(selectedDateText)

                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.time = selectedDate
                            val selectedDateInMillis = selectedCalendar.timeInMillis

                            val oneMonthLater = Calendar.getInstance()
                            oneMonthLater.timeInMillis = selectedDateInMillis
                            oneMonthLater.add(Calendar.MONTH, 1) // Menambah satu bulan

                            val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                            Log.d("check min", dateFormatter.format(selectedDateInMillis))
                            Log.d(
                                "check max one month later",
                                dateFormatter.format(oneMonthLater.timeInMillis)
                            )

                            showDatePicker(
                                selectedDateInMillis + (24 * 60 * 60 * 1000),
                                oneMonthLater.timeInMillis
                            ) { selectedDate ->
                                txtUntil.text = selectedDate
                                if (txtUntil.text.toString() != "-- : --") {
                                    binding.txtUntil.setTextColor(requireContext().getColor(R.color.black))
                                } else {
                                    binding.txtUntil.setTextColor(requireContext().getColor(R.color._grey))
                                }
                            }
                        }
                    }
                }
            } else {
                binding.txtFrom.isEnabled = false
                binding.txtUntil.isEnabled = false
            }
        }

        binding.btnSend.setOnClickListener {
            val reason = binding.txtReason.text.toString()
            val start = binding.txtFrom.text.toString()
            val finish = binding.txtUntil.text.toString()
            val selectedSpinnerItem = binding.spinnerType.selectedItem as SpinnerList
            val selectedId = selectedSpinnerItem.id_jenis_izin
            val radioOption1 = binding.radioHour.isChecked
            val radioOption2 = binding.radioDay.isChecked
            val selectedRadioButtonValue =
                if (radioOption1) "2" else if (radioOption2) "1" else "Tidak ada yang dipilih"
            if (selectedId == "" || selectedId.isEmpty()) {
                Log.d("id spinner ", "harap pilih jenis izin!!")
            } else if (start == "-- : --") {
                Log.d("start perizinan", "harap isi awal perizinan")
            } else if (finish == "-- : --") {
                Log.d("end perizinan", "harap isi akhir perizinan")
            } else {
                viewModel.getPermissionUser(
                    id_pegawai = dataUser!!.id_pegawai,
                    start = start,
                    end = finish,
                    keterangan = reason,
                    jenis = selectedId,
                    tipe = selectedRadioButtonValue
                )

                viewModel.permission.observe(viewLifecycleOwner){
                   hideLoading()
                    when(it){
                        is NetworkResult.Success ->{
                            dismiss()
                        }

                        is NetworkResult.Loading ->{
                            showLoading()
                        }

                        is NetworkResult.Error ->{
                            handleApiError(it.message)
                        }
                    }
                }

                Log.d(
                    "kirim data", "jenis izin $selectedId \n" +
                            "mulai $start \n " +
                            "berakhir $finish \n" +
                            "dengan alasan $reason \n" +
                            "dan opsi terpilih $selectedRadioButtonValue "
                )
            }
        }
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

    private fun convertStringToDate(dateStr: String): Date {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.parse(dateStr) ?: Date()
    }

    private fun showMaterialTimePicker(
        defaultHour: Int,
        defaultMinute: Int,
        minHour: Int,
        maxHour: Int,
        callback: (String) -> Unit
    ) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(defaultHour)
            .setMinute(defaultMinute)
            .build()

        picker.addOnPositiveButtonClickListener {
            val selectedHour = picker.hour
            val selectedMinute = picker.minute
            val selectedTime = "$selectedHour:$selectedMinute"

            if (selectedHour < minHour || selectedHour > maxHour ||
                (selectedHour == minHour && selectedMinute < 0) ||
                (selectedHour == maxHour && selectedMinute > 0)
            ) {
                callback("-- : --")
                Toast.makeText(
                    requireContext(),
                    "Waktu harus antara $minHour:00 dan $maxHour:00",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                callback(String.format("%02d:%02d", selectedHour, selectedMinute))
            }
        }

        val fragmentManager = parentFragmentManager
        picker.show(fragmentManager, "MaterialTimePicker")
    }

    private fun setTextViewColorGrey() {
        binding.txtFrom.setTextColor(requireContext().getColor(R.color._grey))
        binding.txtUntil.setTextColor(requireContext().getColor(R.color._grey))
    }

    private fun showDatePicker(minDate: Long, maxDate: Long, onDateSelected: (String) -> Unit) {
        val mCalendar = Calendar.getInstance()
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = mCalendar.timeInMillis

        val mDialog = DatePickerDialog(
            requireContext(),
            { _, mYear, mMonth, mDay ->
                mCalendar[Calendar.YEAR] = tempCalendar[Calendar.YEAR]
                mCalendar[Calendar.MONTH] = tempCalendar[Calendar.MONTH]
                mCalendar[Calendar.DAY_OF_MONTH] = tempCalendar[Calendar.DAY_OF_MONTH]

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar[Calendar.YEAR] = mYear
                selectedCalendar[Calendar.MONTH] = mMonth
                selectedCalendar[Calendar.DAY_OF_MONTH] = mDay
                if (selectedCalendar.timeInMillis in minDate..maxDate) {
                    mCalendar[Calendar.YEAR] = selectedCalendar[Calendar.YEAR]
                    mCalendar[Calendar.MONTH] = selectedCalendar[Calendar.MONTH]
                    mCalendar[Calendar.DAY_OF_MONTH] = selectedCalendar[Calendar.DAY_OF_MONTH]
                }

                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    .format(Date(mCalendar.timeInMillis))
                onDateSelected(formattedDate)
            },
            tempCalendar[Calendar.YEAR],
            tempCalendar[Calendar.MONTH],
            tempCalendar[Calendar.DAY_OF_MONTH]
        )

        mDialog.datePicker.minDate = minDate
        mDialog.datePicker.maxDate = maxDate

        mDialog.setOnCancelListener {
            Log.d("cancel", "calendar is cancel")
        }

        mDialog.show()
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