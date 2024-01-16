package com.example.data.models

import com.example.ui.base.BasePresenter
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception

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
