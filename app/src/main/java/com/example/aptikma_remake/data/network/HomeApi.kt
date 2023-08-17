package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.data.model.StatisticResponse
import com.example.aptikma_remake.data.model.statistic.HomeModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {

    @GET("beritaAcara")
    suspend fun berita(
    ): Response<List<BeritaAcaraResponseItem>>

    @FormUrlEncoded
    @POST("statistic_pegawai")
    suspend fun statistic(
        @Field("id_pegawai") id_pegawai: String
    ): Response<StatisticResponse>

    @FormUrlEncoded
    @POST("statistic_pegawai")
    suspend fun homeApi(
        @Field("id_pegawai") id_pegawai: String
//    @Body body: RequestBodies.IdPegawai
    ): Response<HomeModel>

}