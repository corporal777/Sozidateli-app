package com.example.repository

import io.reactivex.Completable

interface AuthRepository {

    fun authSN(): Completable

    fun authEmail(): Completable

    fun register(): Completable
}