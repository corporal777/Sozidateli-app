package com.example.data.models

data class ApiResponseUpload<T>(
        val server: Any? = null,
        val response: List<T>,
        val response_detail: ResponseDetail? = null,
        val session: Session? = null,
        val code: Int = 0,
        val errors: List<Any>? = null
)