package com.example.aptikma_remake.ui.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aptikma_remake.R
import com.example.aptikma_remake.databinding.ActivityMainBinding
import com.example.aptikma_remake.databinding.LayoutWarningDialogBinding
import com.example.aptikma_remake.util.getData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    var dataUser = getData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val toolbarBinding = OverLayBinding.inflate(layoutInflater, view.findViewById(R.id.constraintLayout), true)
//        toolbarBinding.namas.text = "dataUser!!.nama"
//        Log.d("my name", dataUser!!.nama)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()

//        Bottom Navigation
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.scanFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    val dialogBinding = LayoutWarningDialogBinding.inflate(layoutInflater)

                    val alertDialog =
                        AlertDialog.Builder(this@MainActivity, R.style.AlertDialogTheme)
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
                        finish()

                    }
                    dialogBinding.buttonNo.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    if (alertDialog.window != null) {
                        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                    }

                    alertDialog.show()

                } else {
                    navController.navigateUp()
                }
            }

        }
//        binding.bottomNavigationView.background = null
//        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

    }

    fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
    }


}