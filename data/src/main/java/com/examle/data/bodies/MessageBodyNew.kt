package com.examle.data.bodies

import java.io.File

data class MessageBodyNew(
    val chat : Int,
    val message : String? = null,
    val file : File? = null
)