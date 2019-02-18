package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
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

    override fun getNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>> {
        return callPagination(api.getUserNotifications(limit, offset))
    }

    override fun notificationsRegister(token: String): Completable {
        return call(api.notificationsRegister(token))
    }

    override fun notificationsUnregister(token: String): Completable {
        return call(api.notificationsUnregister(token))
    }

    override fun updateUser(user: User)= call(api.updateUser(user)
            .doOnSuccess { appData.setUser(it.response) })

    override fun searchUser(name: String, email: String,limit: Int, offset: Int): Maybe<PaginationResponse<User>> {

        return callPagination(api.userSearch(if(name.isEmpty()) " " else name,limit,offset))
    }
}