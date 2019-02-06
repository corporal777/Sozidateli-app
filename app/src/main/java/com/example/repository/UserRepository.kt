package com.example.repository

import com.example.data.models.ApiResponse
import com.example.data.models.user.User
import io.reactivex.Single

interface UserRepository {
    fun getUser(): Single<ApiResponse<User>>
}