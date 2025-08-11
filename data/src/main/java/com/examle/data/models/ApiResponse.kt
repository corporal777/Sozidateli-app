package com.examle.data.models

data class ApiResponse<T>(
        val server: Any? = null,
        val response: T,
        val session: Session? = null,
        val code: Int = 0
)