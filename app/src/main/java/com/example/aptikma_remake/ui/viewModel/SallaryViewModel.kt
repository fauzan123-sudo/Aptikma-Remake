package com.example.aptikma_remake.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aptikma_remake.data.model.Sallary
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.data.repository.SallaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teamgravity.checkinternet.CheckInternet
import javax.inject.Inject

@HiltViewModel
class SallaryViewModel @Inject constructor(val repository: SallaryRepository):ViewModel() {

    private val _sallary = MutableLiveData<NetworkResult<Sallary>>()
    val sallary : LiveData<NetworkResult<Sallary>>
        get() = _sallary


    fun getSallary(id_pegawai:String){
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected){
                _sallary.postValue(NetworkResult.Loading())
                _sallary.postValue(repository.sallary(id_pegawai))
            }
            else{
                _sallary.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }

    }




}