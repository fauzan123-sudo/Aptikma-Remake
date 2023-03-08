package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {

    @GET("beritaAcara")
    suspend fun berita(
    ): Response<List<BeritaAcaraResponseItem>>

//    @FormUrlEncoded
    @POST("statistic_pegawai")
    suspend fun statistic(
//        @Field("id_pegawai") id_pegawai:Int
    @Body body: RequestBodies.IdPegawai
) : Response<List<Pegawai>>

}