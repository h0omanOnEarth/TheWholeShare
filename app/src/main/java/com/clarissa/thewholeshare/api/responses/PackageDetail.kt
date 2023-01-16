package com.clarissa.thewholeshare.api.responses

data class PackageDetail(
    val id: Int,
    val pickup: String,
    val full_name: String,
    val location: String,
)