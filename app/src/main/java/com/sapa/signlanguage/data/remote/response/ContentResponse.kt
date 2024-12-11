package com.sapa.signlanguage.data.remote.response

data class ContentResponse(
    val id: String,
    val title: String,
    val description: String,
    val images: List<String>, // Sesuaikan jika hanya satu URL
    val createdAt: String,
    val updatedAt: String
)