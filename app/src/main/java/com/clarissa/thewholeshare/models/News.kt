package com.clarissa.thewholeshare.models

data class News(
    var id :Int,
    var title : String,
    var content : String,
    var request_id : Int,
    var batch : Int,
    var created_at : String? =null,
    var updated_at : String? = null,
    var deleted_at : String? = null,
) {
}