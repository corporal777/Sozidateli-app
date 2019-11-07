package com.example.data.models

class ApiError(
        val code: Int,
        val session: Session?,
        val errors: List<String>
) : Throwable() {

    fun toErrorsString(): String {
        return errors.joinToString("\n")
    }

    fun hasError(error: String): Boolean {
        return error.contains(error)
    }
}