package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.LoginRequest
import com.example.aptikma_remake.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("username") username:String,
        @Field("password") password:String
    ): Response<LoginResponse>

}