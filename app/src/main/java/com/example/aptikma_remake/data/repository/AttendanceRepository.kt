package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.network.AttendanceApi
import javax.inject.Inject

class AttendanceRepository @Inject constructor(private val api: AttendanceApi) : BaseRepository() {
    suspend fun attendanceUser(id_pegawai: String) = safeApiCall { api.attendance(id_pegawai) }
    suspend fun attendanceList(id_pegawai: String) = safeApiCall { api.attendanceList(id_pegawai) }
    suspend fun permission(
        id_pegawai: String,
        start_izin: String,
        end: String,
        jenis: String,
        keterangan: String,
        tipe: String
    ) = safeApiCall { api.permisionApi(id_pegawai, start_izin, end, jenis, keterangan, tipe) }

    suspend fun spinnerList() = safeApiCall { api.spinnerList() }
    suspend fun scanning(id_pegawai: String, string_qr_code: String, status: String) =
        safeApiCall { api.scanning(id_pegawai, string_qr_code, status) }
}