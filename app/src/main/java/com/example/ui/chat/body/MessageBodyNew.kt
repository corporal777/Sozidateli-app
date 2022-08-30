package com.example.ui.chat.body

import java.io.File

data class MessageBodyNew(
    val chat : Int,
    val message : String? = null,
    val file : File? = null
)