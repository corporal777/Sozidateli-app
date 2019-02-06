package com.example.repository

import com.example.data.models.DefaultResponse
import com.example.data.models.user.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun getUser(): Single<DefaultResponse<User>>
}