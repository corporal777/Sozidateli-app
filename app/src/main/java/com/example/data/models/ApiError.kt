package com.example.data.models

class ApiError(
        var code: Int,
        val session: Session?,
        val errors: List<String>
) : Throwable() {

    fun toErrorsString(): String {
        return errors.joinToString("\n")
    }

    fun hasError(vararg error: String): Boolean {
        return errors.intersect(error.toList()).isNotEmpty()
    }
}