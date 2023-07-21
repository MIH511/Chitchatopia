package com.chating.chitchatopia.models

import java.io.Serializable

data class User (var email:String, var password:String, var user_name:String, var profileImage:String, var userID:String,var fcmToken:String): Serializable {

    constructor() : this("","","","","",""){

    }
}