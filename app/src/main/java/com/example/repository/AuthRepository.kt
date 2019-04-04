package com.example.repository

import com.example.data.models.AuthResponse
import com.example.data.models.AuthSNResponse
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRepository {

    fun authSocialNetwork(snType:String,token: String,email: String?): Single<AuthSNResponse>
    fun setEmailSocialNetwork(snType:String,email:String,token: String):Completable
    fun confirmEmailSocialNetwork(snType:String,id:String,code:String):Completable

    fun authEmail(email: String, password: String): Completable
    fun register(email: String, password: String, name: String, lastName: String): Completable
    fun registerConfirm(email: String, code: String): Completable

    fun sendRecoveryEmail(email: String): Completable
    fun checkRecoveryCode(email: String, code: String): Completable
    fun setPassword(email: String, code: String,password: String): Completable

}