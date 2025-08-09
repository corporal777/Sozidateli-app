package com.examle.data.bodies

data class BindSocialAccountBody(
    val uuid : String,
    val userId : Int,
    val socialNetwork : String,
    val newBind : Boolean
)