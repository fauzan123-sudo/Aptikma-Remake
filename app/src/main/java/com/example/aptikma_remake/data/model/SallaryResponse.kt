package com.example.aptikma_remake.data.model

data class SallaryResponse(
    val detail_potongan: List<DetailPotongan>,
    val detail_tunjangan: List<DetailTunjangan>,
    val gaji_bersih: Int,
    val gaji_pokok: Int,
    val status: Int,
    val total_potongan: Int,
    val total_tunjangan: Int
)