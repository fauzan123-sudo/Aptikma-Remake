package com.example.aptikma_remake.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.data.model.Pegawai
import com.example.aptikma_remake.data.model.RequestBodies
import com.example.aptikma_remake.data.model.StatisticResponse
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teamgravity.checkinternet.CheckInternet
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private val _statistic = MutableLiveData<NetworkResult<List<Pegawai>>>()
    val statistic: LiveData<NetworkResult<List<Pegawai>>> = _statistic

    private val _berita = MutableLiveData<NetworkResult<List<BeritaAcaraResponseItem>>>()
    val berita: LiveData<NetworkResult<List<BeritaAcaraResponseItem>>> = _berita

    fun statistic(body: RequestBodies.IdPegawai) {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _statistic.postValue(NetworkResult.Loading())
                _statistic.postValue(repository.statistic(body))
            } else {
                _statistic.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }

    fun newsRequest() {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _berita.postValue(NetworkResult.Loading())
                _berita.postValue(repository.berita())
            } else {
                _berita.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }

}