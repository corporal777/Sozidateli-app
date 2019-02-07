package com.example.repository

import io.reactivex.Completable

interface AuthRepository {

    fun authVk(token: String, email: String?): Completable
    fun authFb(token: String): Completable
    fun authOk(token: String): Completable

    fun authEmail(email: String, password: String): Completable
    fun register(email: String, password: String, name: String): Completable
}