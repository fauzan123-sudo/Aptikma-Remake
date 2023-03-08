package com.example.aptikma_remake.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aptikma_remake.data.model.LoginRequest
import com.example.aptikma_remake.data.model.LoginResponse
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.data.repository.AuthRepoitory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teamgravity.checkinternet.CheckInternet
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(app: Application, private val repository: AuthRepoitory) :
    AndroidViewModel(app) {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<LoginResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = _userResponseLiveData

    fun login(username:String, password:String) {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _userResponseLiveData.postValue(NetworkResult.Loading())
                _userResponseLiveData.postValue(repository.loginUser(username, password))
            } else
                _userResponseLiveData.postValue(NetworkResult.Error("No Internet Connection"))
        }

    }

}