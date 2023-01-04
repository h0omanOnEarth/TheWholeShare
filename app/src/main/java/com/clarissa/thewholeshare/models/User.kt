package com.clarissa.thewholeshare.models

data class User(
    var id:Int,
    var username:String,
    var password:String,
    var full_name:String,
    var phone:String,
    var address:String,
    var email:String,
    var role : Int,
    var created_at : String? = null,
    var updated_at : String? = null,
    var deleted_at : String? = null
) : java.io.Serializable