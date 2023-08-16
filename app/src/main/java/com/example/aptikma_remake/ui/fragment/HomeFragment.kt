package com.example.aptikma_remake.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.NewsAdapter
import com.example.aptikma_remake.data.adapter.SliderAdapter
import com.example.aptikma_remake.data.adapter.StatisticAdapter
import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentHomeBinding
import com.example.aptikma_remake.databinding.LayoutWarningDialogBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.HomeViewModel
import com.example.aptikma_remake.ui.viewModel.NewsViewModel
import com.example.aptikma_remake.ui.viewModel.ProfileViewModel
import com.example.aptikma_remake.util.Constants
import com.example.aptikma_remake.util.deleteNotificationCount
import com.example.aptikma_remake.util.getNotificationCount
import com.example.aptikma_remake.util.handleApiError
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val myProfile: ProfileViewModel by viewModels()
    private val newsViewModel: HomeViewModel by viewModels()
    private val newFireBase: NewsViewModel by viewModels()

    private lateinit var statisticAdapter: StatisticAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val sliderAdapter = SliderAdapter()
    lateinit var adapter: NewsAdapter
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var sliderView: SliderView

    private val listImageSlider = ArrayList<BeritaAcaraResponseItem>()

    lateinit var lineChart: LineChart

    private lateinit var recyclerview: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar()
        deleteBadgeValue()
        goToNotification()
        setUpPermission()
        goToProfile()

//        recyclerview = binding.recyclerView
        adapter = NewsAdapter(requireContext())
//        recyclerview.adapter = adapter
//        recyclerview.layoutManager = LinearLayoutManager(requireContext())
//        newsHandler()

//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = notificationAdapter
//        }
        Log.d("top photo", Constants.PROFILE_USER + dataUser!!.image)

        swipeRefreshLayout = binding.swipe
        loadData()
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
//        Statistic
        statisticAdapter = StatisticAdapter()

////        Image Slider
//        sliderView = binding.imageSlider
//        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
//        sliderView.setSliderAdapter(sliderAdapter)
//        sliderView.scrollTimeInSec = 3
//        sliderView.isAutoCycle = true
//        sliderView.startAutoCycle()
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.homeFragment) {
//                    activity?.finish()
                    showExitConfirmationDialog()
                } else {
                    navController.navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun goToProfile() {
        binding.include.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun setUpPermission() {
        binding.btnPermission.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bottomSheetAttendance)
        }
    }

    private fun goToNotification() {
        binding.include.badgeBeritaAcara.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
    }

//    private fun newsHandler() {
//        newsViewModel.homeData(dataUser!!.id_pegawai)
//        newsViewModel.home.observe(viewLifecycleOwner){
//            when(it){
//                is NetworkResult.Success ->{
//                    val responses = it.data!!
//                    adapter.differ.submitList(responses)
//                }
//
//                is NetworkResult.Loading ->{
//
//                }
//
//                is NetworkResult.Error ->{
//                    handleApiError(it.message)
//                }
//            }
//        }
//    }

    private fun topBar() {
        myProfile.getProfileUser(dataUser!!.id_pegawai)
        myProfile.profile.observe(viewLifecycleOwner) {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()

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
                    hideLoading()
                    handleApiError(it.message)
                }
            }

        }
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

    private fun loadData() {
        viewModel.homeData(dataUser!!.id_pegawai)
        viewModel.home.observe(viewLifecycleOwner) { it ->
            swipeRefreshLayout.isRefreshing = false
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!
                    val data = response.pegawai.map { it.jumlah }
                    statistic(data)
                    val existPermission = response.exist_izin
                    val typePermission = response.tipe_izin
                    val startPermission = response.durasi_izin?.mulai
                    val finishPermission = response.durasi_izin?.selesai

                    if (existPermission == 1) {
                        if (typePermission == "1") {
                            val dateFormatter =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            try {
                                val startDate = dateFormatter.parse(startPermission)
                                val endDate = dateFormatter.parse(finishPermission)

                                val displayFormat =
                                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val startDateFormatted = startDate?.let { it1 ->
                                    displayFormat.format(it1)
                                }
                                val endDateFormatted =
                                    endDate?.let { it1 -> displayFormat.format(it1) }
                                binding.txtStartPermission.text = "$startDateFormatted"
                                binding.txtFinishPermission.text = "$endDateFormatted"
                            } catch (e: ParseException) {
                                Log.e("Parsing Error", "Error parsing date: ${e.message}")
                            }
                        } else if (typePermission == "2") {
                            val dateFormatter =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            try {
                                val startTime = dateFormatter.parse(startPermission)
                                val endTime = dateFormatter.parse(finishPermission)

                                val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val startTimeFormatted = startTime?.let { it1 ->
                                    displayFormat.format(it1)
                                }
                                val endTimeFormatted =
                                    endTime?.let { it1 -> displayFormat.format(it1) }
                                binding.txtStartPermission.text = "$startTimeFormatted"
                                binding.txtFinishPermission.text = "$endTimeFormatted"
                            } catch (e: ParseException) {
                                Log.e("Parsing Error", "Error parsing time: ${e.message}")
                            }
                        }
                    } else if (existPermission == 0) {
                        binding.txtStartPermission.text = "-"
                        binding.txtFinishPermission.text = "-"
                    } else {
                        binding.txtStartPermission.text = "-"
                        binding.txtFinishPermission.text = "-"

                    }
                }

                is NetworkResult.Error -> {
                    Log.e("error", "${it.message}")
                    handleApiError(it.message)
                }

                is NetworkResult.Loading -> {
                    swipeRefreshLayout.isRefreshing = true
                    showLoading()
                }
            }
        }

//        viewModel.newsRequest()
//        viewModel.berita.observe(viewLifecycleOwner) {
//            swipeRefreshLayout.isRefreshing = false
//            progressDialog.dismiss()
//            when (it) {
//                is NetworkResult.Success -> {
//                    listImageSlider.clear()
//                    it.data?.let { it1 -> listImageSlider.addAll(it1) }
//                    sliderView.setSliderAdapter(sliderAdapter)
//                    sliderAdapter.renewItems(listImageSlider)
//                }
//                is NetworkResult.Error -> {
//                    handleApiError(it.message)
//                }
//
//                is NetworkResult.Loading -> {
//                    swipeRefreshLayout.isRefreshing = true
//                    showLoading()
//                }
//            }
//        }
    }

    private fun showExitConfirmationDialog() {
        val dialogBinding = LayoutWarningDialogBinding.inflate(layoutInflater)

        val alertDialog =
            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setView(dialogBinding.root)
                .create()

        dialogBinding.textTitle.text = "Konfirmasi Keluar"
        dialogBinding.textMessage.text =
            "Anda yakin Ingin keluar dari this aplikasi"
        dialogBinding.buttonYes.text = "Ya"
        dialogBinding.buttonNo.text = "Batal"
        dialogBinding.imageIcon.setImageResource(R.drawable.ic_baseline_warning_24)

        dialogBinding.buttonYes.setOnClickListener {
            alertDialog.dismiss()
            requireActivity().finish()

        }
        dialogBinding.buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()
    }

    private fun statistic(xData: List<Int>) {
        lineChart = binding.chart1
        val mLineGraph = lineChart.xAxis

//        val date = listOf("jan", "mar", "jun", "sept", "des")
        val date = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )

        val values: ArrayList<Entry> = ArrayList()
        for ((index, value) in xData.withIndex()) {
            val valueX = index.toFloat() + 1
            val valueY = value.toFloat()
            values.add(Entry(valueX, valueY))
            Log.d("valuer", "value x is : $valueX /n value Y is : $valueY")
        }

        for (i in xData) {
            Log.d("bulan ke - ", "${i.toFloat()}")
        }

        lineChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey_text)
        lineChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.grey_text)
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val set1 = LineDataSet(values, "")
        mLineGraph.position = XAxis.XAxisPosition.BOTTOM
        dataSets.add(set1)

        set1.lineWidth = 3f
        set1.circleRadius = 3f
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.setDrawValues(true)
        set1.circleHoleColor = Color.BLACK
        set1.color = Color.RED
        set1.setCircleColor(Color.BLACK)
        set1.highLightColor = Color.rgb(255, 0, 0)

        lineChart.setHardwareAccelerationEnabled(false)

        lineChart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = (value - 1).toInt()
                return if (index in date.indices) {
                    date[index]
                } else {
                    ""
                }
            }
        }

        val data = LineData(dataSets)
        val leftAxis = lineChart.axisLeft


        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 40f
        lineChart.extraTopOffset
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1f
        leftAxis.labelCount = 2

//        true will shift the hole from line


        lineChart.xAxis.setLabelCount(4, false)

        lineChart.data = (data)
        lineChart.animateX(2000)
        lineChart.invalidate()
//        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false

        //Setup Legend
        val legend = lineChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(true)
    }

    private fun showLoading() {
        progressDialog.progressHelper.barColor =
            ContextCompat.getColor(requireContext(), R.color.gradient_end_color)
        progressDialog.titleText = "Loading"
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    override fun onResume() {
        super.onResume()
//        updateBadge()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    private fun updateBadge() {
        val count = getNotificationCount()
        binding.include.badgeBeritaAcara.badgeValue = count!!
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter("update_badge_action")
        requireContext().registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "update_badge_action") {
                val count = intent.getIntExtra("badge_count", 0)
                updateBadgeValue(count)
            }
        }
    }

    private fun updateBadgeValue(count: Int) {
        binding.include.badgeBeritaAcara.badgeValue = count
    }

    private fun deleteBadgeValue() {
        binding.include.badgeBeritaAcara.setOnClickListener {
            deleteNotificationCount()
//            updateBadge()
        }
    }


}

