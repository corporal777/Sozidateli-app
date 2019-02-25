package com.example.data.models

data class ApiResponse<T>(
        val server: Any? = null,
        val response: T,
        val response_detail: ResponseDetail? = null,
        val session: Session? = null,
        val code: Int = 0,
        val errors: List<String>? = null
)