package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.network.ProfileApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: ProfileApi): BaseRepository() {
    suspend fun profile(id_pegawai:String) = safeApiCall { api.getProfile(id_pegawai) }
}