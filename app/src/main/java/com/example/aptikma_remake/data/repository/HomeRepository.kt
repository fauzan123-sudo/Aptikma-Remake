package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.model.RequestBodies
import com.example.aptikma_remake.data.network.HomeApi
import javax.inject.Inject


class HomeRepository @Inject constructor(private val api: HomeApi) : BaseRepository() {

    suspend fun statistic(body: RequestBodies.IdPegawai) = safeApiCall { api.statistic(body) }
    suspend fun berita() = safeApiCall { api.berita() }


}