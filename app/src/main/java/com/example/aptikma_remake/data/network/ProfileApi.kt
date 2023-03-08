package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.ProfileUser
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileApi {

    @FormUrlEncoded
    @POST("read")
    suspend fun getProfile(
        @Field("id_pegawai") id_pegawai:String
    ) : Response<ProfileUser>
}