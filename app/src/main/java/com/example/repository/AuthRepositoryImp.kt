package com.example.repository

import io.reactivex.Completable
import javax.inject.Inject

class AuthRepositoryImp
@Inject constructor():AuthRepository{

    override fun authSN() = Completable.complete()

    override fun authEmail() = Completable.complete()

    override fun register() = Completable.complete()
}