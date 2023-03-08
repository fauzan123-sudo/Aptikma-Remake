package com.example.aptikma_remake.ui.fragment

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aptikma_remake.data.adapter.SliderAdapter
import com.example.aptikma_remake.data.adapter.StatisticAdapter
import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.data.model.RequestBodies
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.FragmentHomeBinding
import com.example.aptikma_remake.ui.base.BaseFragment
import com.example.aptikma_remake.ui.viewModel.HomeViewModel
import com.example.aptikma_remake.util.Constans.BASE_URL
import com.example.aptikma_remake.util.MyFirebaseInstanceIDService.Companion.CHANNEL_ID
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
    var url = "statistic_pegawai"
    lateinit var statisticAdapter: StatisticAdapter

    //    lateinit var sliderAdapter: SliderAdapter
    private val sliderAdapter = SliderAdapter()

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var sliderView: SliderView

    private val listImageSlider = ArrayList<BeritaAcaraResponseItem>()

    lateinit var lineChart: LineChart

    private fun getIncomeEntries(): List<Entry> {
        val incomeEntries: ArrayList<Entry> = ArrayList()
        incomeEntries.add(Entry(1f, 0f))
        incomeEntries.add(Entry(2f, 0f))
        incomeEntries.add(Entry(3f, 0f))
        incomeEntries.add(Entry(4f, 0f))
        incomeEntries.add(Entry(5f, 0f))
        incomeEntries.add(Entry(6f, 0f))
        incomeEntries.add(Entry(7f, 0f))
        incomeEntries.add(Entry(8f, 0f))
        incomeEntries.add(Entry(9f, 25f))
        incomeEntries.add(Entry(10f, 0f))
        incomeEntries.add(Entry(11f, 0f))
        incomeEntries.add(Entry(12f, 0f))
        return incomeEntries.subList(1, 12)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        createNotificationChannel()

        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        val request = JsonObjectRequest(Request.Method.POST, BASE_URL + url, null, { response ->
            try {
                val courseName: String = response.getString("pegawai")
                Log.d("sasak", "$response")

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, { error ->
            Log.e("TAG", "RESPONSE IS $error")
        })
        queue.add(request)

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

        val idPegawai = tokenManager.getToken()
        if (idPegawai != null) {
            val body = RequestBodies.IdPegawai(
                idPegawai
            )
            viewModel.statistic(body)
            Log.d("theTok", "$idPegawai")
        }

        viewModel.statistic.observe(viewLifecycleOwner) { it ->
            Log.d("TwAG", "${it}")
            when (it) {
                is NetworkResult.Success -> {
                    val data = it.data!!.map { it.jumlah }
                    Log.d("statistik", "$data")
                    statistic(data)
                }

                is NetworkResult.Error -> {
                    Log.d("home error", "${it.data}")
                    val error = it.message.toString()
                    Log.d("home error statistik", error)
                    handleApiError(error)
                }

//                is NetworkResult.Loading -> {
//                    binding.progressbar.visible(true)
//                }

                else -> Log.d("image else", "$it")
            }
        }

        viewModel.newsRequest()
        viewModel.berita.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    listImageSlider.clear()
                    it.data?.let { it1 -> listImageSlider.addAll(it1) }
                    sliderView.setSliderAdapter(sliderAdapter)
                    sliderAdapter.renewItems(listImageSlider)
                    Log.d("images", "${it.data}")
//                    updateSliderUI(it.data)
                }
                is NetworkResult.Error -> {
                    Log.d("error berita", "${it.data}")
                }
                else -> {
                    Log.d("else home", "${it.data}")
                }
            }
        }


//        binding.btnLogout.setOnClickListener {
//            tokenManager.deleteToken()
//            startActivity(Intent(activity, Login::class.java))
//            requireActivity().startActivity(Intent(this, Login::class.java))
//        }


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Peringatan")
                        .setMessage("Anda yakin ingin keluar")
                        //    .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("Ya") { _, _ ->
//                        Toast.makeText(requireContext(), "Ya", Toast.LENGTH_SHORT).show()
                            requireActivity().finish()
                        }
                        .setNegativeButton("Kembali") { _, _ ->
//                        Toast.makeText(requireContext(), "Kembali", Toast.LENGTH_SHORT).show()
                        }
                        .create().show()
                }
            })
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


        val xAxisValues = ArrayList(
            listOf(
                "Jan",
                "Feb",
                "March",
                "April",
                "May",
                "June",
                "July",
                "Aug",
                "Sep",
                "Okt",
                "Nov",
                "Des"
            )
        )
        val values: ArrayList<Entry> = ArrayList()


        for ((index, value) in xData.withIndex()) {
            val valueX = index.toFloat() + 1
            val valueY = value.toFloat()
            values.add(Entry(valueX, valueY))
//            entries.add(valueX,valueY)
            Log.d("valuer", "value x is : $valueX /n value Y is : $valueY")
//            entries.add()
            println("the element at ${index + 1} is $value")
        }

        for (i in xData) {
            Log.d("bulan ke - ", "${i.toFloat()}")
//            Log.d("kehadiran bulan ini ", "${i.toFloat()}")
//        entries.add(Entry(i.toFloat(), xData))
        }

        val incomeEntries = getIncomeEntries()

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

//        Todo in here this problem zan ..
//        val rightAxis = lineChart.axisRight
//
//
//        lineChart.axisRight.axisMinimum = 2f


        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 40f
        lineChart.extraTopOffset
//        lineChart.xAxis.labelCount = 12
//        lineChart.extraBottomOffset
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

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name: CharSequence = "firebaseNotifChannel"
//            val description = "Receve Firebase notification"
//            val importance = NotificationManager.IMPORTANCE_MAX
//            @SuppressLint("WrongConstant") val channel =
//                NotificationChannel(CHANNEL_ID, name, importance)
//            channel.description = description
//            val notificationManager: NotificationManager =
//                getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }


}

