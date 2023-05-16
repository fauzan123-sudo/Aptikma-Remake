package com.example.aptikma_remake.util

import java.text.SimpleDateFormat
import java.util.*

class Helper {
    fun formatDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val resultFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return resultFormat.format(date!!)
    }
}