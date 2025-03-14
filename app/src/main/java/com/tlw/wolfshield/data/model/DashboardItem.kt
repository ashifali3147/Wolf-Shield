package com.tlw.wolfshield.data.model

data class DashboardItem(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val approved: Boolean = false // Store drawable name as a string
)