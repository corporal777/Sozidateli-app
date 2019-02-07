package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.user.User
import io.reactivex.Single
import javax.inject.Inject

class UserRepositoryImp
@Inject constructor(
        private val api: Api,
        private val appData: AppData
) : ApiRepository(appData), UserRepository {

    override fun getUser(): Single<User> = call(api.getUser())
            .doOnSuccess { appData.setUser(it) }

    override fun getLastNotification() = call(api.getLastNotification())
}