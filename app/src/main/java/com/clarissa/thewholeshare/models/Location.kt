package com.clarissa.thewholeshare.models

data class Location(
    var id:Int,
    var address : String,
    var note : String,
    var status : Int,
    var deleted_at : String?=null
) {
}