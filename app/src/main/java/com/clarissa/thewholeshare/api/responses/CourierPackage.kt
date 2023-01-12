package com.clarissa.thewholeshare.api.responses

import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.User

data class CourierPackage(
    val id: Int,
    val user_id: Int,
    val request_id: Int,
    val courier_id: Int,
    val pickup: String,
    val full_name: String
)