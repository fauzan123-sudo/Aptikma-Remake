package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.model.RequestBodies
import com.example.aptikma_remake.data.network.HomeApi
import javax.inject.Inject


class HomeRepository @Inject constructor(private val api: HomeApi) : BaseRepository() {

    suspend fun statistic(id_pegawai:String) = safeApiCall { api.statistic(id_pegawai) }
    suspend fun berita() = safeApiCall { api.berita() }


}