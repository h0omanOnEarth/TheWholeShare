package com.clarissa.thewholeshare.models

import java.sql.Timestamp

data class Request(
    var id : Int,
    var location : String,
    var batch : Int,
    var deadline : String,
    var note : String,
    var status : Int,
    var created_at : String? = null,
    var updated_at : String? = null,
    var deleted_at : String? = null
) {
}