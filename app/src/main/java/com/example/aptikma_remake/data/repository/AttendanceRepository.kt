package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.network.AttendanceApi
import com.example.aptikma_remake.data.network.ProfileApi
import javax.inject.Inject

class AttendanceRepository @Inject constructor(private val api: AttendanceApi): BaseRepository() {
    suspend fun attendanceUser(id_pegawai:String) = safeApiCall { api.attendance(id_pegawai) }
    suspend fun attendanceList(id_pegawai:String) = safeApiCall { api.attendanceList(id_pegawai) }
    suspend fun permission(id_pegawai:String) = safeApiCall { api.attendanceList(id_pegawai) }
    suspend fun spinnerList() = safeApiCall { api.spinnerList() }
}