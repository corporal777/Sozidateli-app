package com.example.data.models

class ApiError(
        val session: Session?,
        val errors: List<String>
) : Throwable()