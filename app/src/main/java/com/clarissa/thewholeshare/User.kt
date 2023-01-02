package com.clarissa.thewholeshare

import java.sql.Timestamp

data class User(
    var id:Int,
    var username:String,
    var password:String,
    var full_name:String,
    var phone:String,
    var address:String,
    var email:String,
    var role : Int,
    var deleted_at : String? = null
) {

}