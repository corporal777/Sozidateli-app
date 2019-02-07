package com.example.data.models

data class ApiResponse<T>(
        var server: Any? = null,
        var response: T,
        var response_detail: List<Any>? = null,
        var session: Any? = null,
        var code: Int = 0,
        var errors: List<Any>? = null
)