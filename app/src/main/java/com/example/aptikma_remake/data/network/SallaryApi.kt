package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.Sallary
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*


interface SallaryApi {

    @FormUrlEncoded
    @POST("gaji")
    suspend fun getSallary(
        @Field("id_pegawai") id_pegawai:String
    ) : Response<Sallary>


}