package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.AttendanceList
import com.example.aptikma_remake.data.model.AttendanceUser
import com.example.aptikma_remake.data.model.LoginResponse
import com.example.aptikma_remake.data.model.SpinnerList
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AttendanceApi {

    @FormUrlEncoded
    @POST("statistic_absensi_page")
    suspend fun attendance(
        @Field("id_pegawai") id_pegawai:String,
    ): Response<AttendanceUser>

    @FormUrlEncoded
    @POST("read_absensi_personal")
    suspend fun attendanceList(
        @Field("id_pegawai") id_pegawai:String,
    ): Response<AttendanceList>

    @FormUrlEncoded
    @POST("read_absensi_personal")
    suspend fun permission(
        @Field("id_pegawai") id_pegawai:String,
    ): Response<AttendanceList>


    @GET("spinnerizin")
    suspend fun spinnerList(
    ): Response<List<SpinnerList>>
}