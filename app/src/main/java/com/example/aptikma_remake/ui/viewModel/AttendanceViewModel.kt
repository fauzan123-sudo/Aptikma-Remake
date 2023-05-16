package com.example.aptikma_remake.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aptikma_remake.data.model.AttendanceList
import com.example.aptikma_remake.data.model.AttendanceUser
import com.example.aptikma_remake.data.model.ScanResponse
import com.example.aptikma_remake.data.model.SpinnerList
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teamgravity.checkinternet.CheckInternet
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: AttendanceRepository) :
    ViewModel() {

    private val _attendanceList = MutableLiveData<NetworkResult<AttendanceList>>()
    val attendanceList: LiveData<NetworkResult<AttendanceList>>
        get() = _attendanceList

    private val _attendanceUser = MutableLiveData<NetworkResult<AttendanceUser>>()
    val attendanceUser: LiveData<NetworkResult<AttendanceUser>>
        get() = _attendanceUser

    private val _spinnerList = MutableLiveData<NetworkResult<List<SpinnerList>>>()
    val spinnerList: LiveData<NetworkResult<List<SpinnerList>>>
        get() = _spinnerList

    private val _scaUserResponse = MutableLiveData<NetworkResult<ScanResponse>>()
    val scanUser: LiveData<NetworkResult<ScanResponse>>
        get() = _scaUserResponse


    fun getAttendanceUser(id_pegawai: String) {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _attendanceUser.postValue(NetworkResult.Loading())
                _attendanceUser.postValue(repository.attendanceUser(id_pegawai))
            } else {
                _attendanceUser.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }

    fun getAttendanceList(id_pegawai: String) {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _attendanceList.postValue(NetworkResult.Loading())
                _attendanceList.postValue(repository.attendanceList(id_pegawai))
            } else {
                _attendanceUser.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }

    fun spinnerList() {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _spinnerList.postValue(NetworkResult.Loading())
                _spinnerList.postValue(repository.spinnerList())
            } else {
                _spinnerList.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }

    fun scanRequest(id_pegawai: String, string_qr_code: String, status: String) {
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected) {
                _scaUserResponse.postValue(NetworkResult.Loading())
                _scaUserResponse.postValue(repository.scanning(id_pegawai, string_qr_code, status))
            } else {
                _scaUserResponse.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }
    }
}