package com.example.aptikma_remake.data.model

data class LoginResponse(
    var id_pegawai: String,
    var image: String,
    var jabatan: String,
    var message: String,
    var nama: String,
    var success: Int,
    var username: String
)