package com.clarissa.thewholeshare

data class Location(
    var id:Int,
    var address : String,
    var note : String,
    var status : Int,
    var deleted_at : String?=null
) {
}