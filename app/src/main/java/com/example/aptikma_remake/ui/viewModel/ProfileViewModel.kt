package com.example.aptikma_remake.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aptikma_remake.data.model.ProfileUser
import com.example.aptikma_remake.data.network.NetworkResult
import com.example.aptikma_remake.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teamgravity.checkinternet.CheckInternet
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository):ViewModel() {

    private val _profile = MutableLiveData<NetworkResult<ProfileUser>>()
    val profile : LiveData<NetworkResult<ProfileUser>>
        get() = _profile


    fun getProfileUser(id_pegawai:String){
        viewModelScope.launch {
            val connected = CheckInternet().check()
            if (connected){
                _profile.postValue(NetworkResult.Loading())
                _profile.postValue(repository.profile(id_pegawai))
            }
            else{
                _profile.postValue(NetworkResult.Error("No Internet Connection"))
            }
        }

    }
}