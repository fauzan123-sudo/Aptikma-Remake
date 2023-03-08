package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.network.ProfileApi
import com.example.aptikma_remake.data.network.SallaryApi
import javax.inject.Inject

class SallaryRepository @Inject constructor(private val api: SallaryApi): BaseRepository() {
    suspend fun sallary(id_pegawai:String) = safeApiCall { api.getSallary(id_pegawai) }
}