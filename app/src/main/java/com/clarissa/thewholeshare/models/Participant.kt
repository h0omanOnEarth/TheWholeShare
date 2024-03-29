package com.clarissa.thewholeshare.models

data class Participant(
    var id : Int,
    var user_id : Int,
    var request_id : Int,
    var courier_id : Int?,
    var pickup : String,
    var note : String,
    var status : Int,
    var fullname : String ="",
    var location : String = "",
    var created_at : String?= null,
    var updated_at : String? = null
) {
}