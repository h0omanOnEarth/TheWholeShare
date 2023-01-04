package com.clarissa.thewholeshare.models

data class Location(
    var id:Int,
    var address : String,
    var batch : Int,
    var deadline : String,
    var note : String,
    var status : String,
    var deleted_at : String?=null
) {
}