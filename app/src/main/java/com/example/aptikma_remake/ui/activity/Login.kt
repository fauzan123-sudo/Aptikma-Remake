package com.example.aptikma_remake.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.databinding.ActivityLoginBinding
import com.example.aptikma_remake.ui.viewModel.AuthViewModel
import com.example.aptikma_remake.util.Constans.TAG
import com.example.aptikma_remake.util.TokenManager
import com.example.aptikma_remake.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Login : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "tokenya: ${tokenManager.getToken()}")

        if (tokenManager.getToken() != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.login.setOnClickListener {
            val userName = binding.username.text.toString()
            val password = binding.password.text.toString()
            Log.d(TAG, "masuk $userName $password")
            when {
                userName.isEmpty() -> Toast.makeText(this, "Harap isi Username", Toast.LENGTH_SHORT)
                    .show()
                password.isEmpty() -> Toast.makeText(this, "Harap isi Password", Toast.LENGTH_SHORT)
                    .show()
                else -> viewModel.login(userName, password)
            }
        }

        responseLogin()


    }

    private fun responseLogin() {
        viewModel.userResponseLiveData.observe(this) {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "simpan : ${it.data}")
                    Log.d(TAG, "simpan : ${it.data!!.id}")

                    val check = it.data.success
                    if (check == 0) {
                        Log.d("wrong username or password", "redirect to login again")
                    } else {
                        Log.d("successfully login", it.data.username)
                        tokenManager.saveToken(it.data.id)
                        startActivity(Intent(this, MainActivity::class.java))
                    }

                }
                is NetworkResult.Error -> {
                    val error = it.message.toString()
                    handleApiError(error)
                    Log.d(TAG, (it.message.toString()))
                }

                else -> Toast.makeText(this, "Hey ada masalah i ${it.data}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}