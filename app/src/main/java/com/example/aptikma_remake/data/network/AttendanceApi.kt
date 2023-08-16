package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.data.model.*
import com.example.aptikma_remake.data.model.permission.PermisionModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AttendanceApi {

    @FormUrlEncoded
    @POST("perijinan")
    suspend fun permisionApi(
        @Field("id_pegawai") id_pegawai: String,
        @Field("start_izin") start_izin: String,
        @Field("end_izin") end_izin: String,
        @Field("jenis_izin") jenis_izin: String,
        @Field("keterangan") keterangan: String,
        @Field("tipe") tipe: String,
    ): Response<PermisionModel>


    @FormUrlEncoded
    @POST("statistic_absensi_page")
    suspend fun attendance(
        @Field("id_pegawai") id_pegawai: String,
    ): Response<AttendanceUser>

    @FormUrlEncoded
    @POST("read_absensi_personal")
    suspend fun attendanceList(
        @Field("id_pegawai") id_pegawai: String,
    ): Response<AttendanceList>

    @FormUrlEncoded
    @POST("read_absensi_personal")
    suspend fun permission(
        @Field("id_pegawai") id_pegawai: String,
    ): Response<AttendanceList>


    @GET("spinnerizin")
    suspend fun spinnerList(
    ): Response<List<SpinnerList>>

    @FormUrlEncoded
    @POST("scan")
    suspend fun scanning(
        @Field("id_pegawai") id_pegawai: String,
        @Field("string_qr_code") string_qr_code: String,
        @Field("status_scan") status_scan: String,
    ): Response<ScanResponse>
}