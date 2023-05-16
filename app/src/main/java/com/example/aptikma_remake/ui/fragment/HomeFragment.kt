package com.example.aptikma_remake.ui.fragment

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
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.adapter.SliderAdapter
import com.example.aptikma_remake.data.adapter.StatisticAdapter
import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentHomeBinding
import com.example.aptikma_remake.databinding.LayoutWarningDialogBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.HomeViewModel
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var tokenManager: TokenManager
    lateinit var statisticAdapter: StatisticAdapter

    private val sliderAdapter = SliderAdapter()
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var sliderView: SliderView

    private val listImageSlider = ArrayList<BeritaAcaraResponseItem>()

    lateinit var lineChart: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        Statistic
        statisticAdapter = StatisticAdapter()

//        Image Slider
        sliderView = binding.imageSlider
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)


        val idUser = tokenManager.getToken()
        if (idUser != null) {
            viewModel.statistic(idUser)
            Log.d("theTok", "$idUser")
        }

        viewModel.statistic.observe(viewLifecycleOwner) { it ->
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    val response = it.data!!
                    val data = response.pegawai.map { it.jumlah }
                    statistic(data)
                }

                is NetworkResult.Error -> {
                    Log.e("error", "${it.message}")
                    handleApiError(it.message)
                }

                is NetworkResult.Loading -> showLoading()
            }
        }

        viewModel.newsRequest()
        viewModel.berita.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    listImageSlider.clear()
                    it.data?.let { it1 -> listImageSlider.addAll(it1) }
                    sliderView.setSliderAdapter(sliderAdapter)
                    sliderAdapter.renewItems(listImageSlider)
                }
                is NetworkResult.Error -> {
                    handleApiError(it.message)
                }

                is NetworkResult.Loading -> showLoading()
            }
        }

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

        val date = ArrayList<String>()
        date.add("Jan")
        date.add("Feb")
        date.add("Mar")
        date.add("April")
        date.add("Mei")
        date.add("Juni")
        date.add("Juli")
        date.add("Agust")
        date.add("Sept")
        date.add("Okt")
        date.add("Nov")
        date.add("Des")

        val values: ArrayList<Entry> = ArrayList()


        for ((index, value) in xData.withIndex()) {
            val valueX = index.toFloat() + 1
            val valueY = value.toFloat()
            values.add(Entry(valueX, valueY))
            Log.d("valuer", "value x is : $valueX /n value Y is : $valueY")
            println("the element at ${index + 1} is $value")
        }

        for (i in xData) {
            Log.d("bulan ke - ", "${i.toFloat()}")
        }

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val set1 = LineDataSet(values, "Statistik hadir pegawai")
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


        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(date)

        val data = LineData(dataSets)
        val leftAxis = lineChart.axisLeft


        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 40f
        lineChart.extraTopOffset
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1f
        leftAxis.labelCount = 2

//        true will shift the hole from line
        lineChart.xAxis.setLabelCount(6, false)

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
}

