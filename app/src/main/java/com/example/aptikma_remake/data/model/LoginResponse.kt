package com.example.aptikma_remake.data.model

data class LoginResponse(
    val id: String,
    val image: String,
    val jabatan: String,
    val message: String,
    val nama: String,
    val success: Int,
    val username: String
)