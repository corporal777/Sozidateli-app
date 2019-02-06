package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import javax.inject.Inject

class UserRepositoryImp
@Inject constructor(private val api: Api, private val appData: AppData) : UserRepository {

    override fun getUser() = api.getUser()
            .map {
                this.appData.user = it.response
                return@map it
            }
}