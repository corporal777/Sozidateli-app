package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class OrganizationRepositoryImp
@Inject constructor(
        private val api: Api,
        private val appData: AppData
) : ApiRepository(appData), OrganizationRepository {


    override fun subscribeOrganization(orgId: Int): Single<AuthResponse> {
        return call(api.subscribeOrganization(orgId))
    }

    override fun unsubscribeOrganization(orgId: Int): Single<AuthResponse> {
        return call(api.unsubscribeOrganization(orgId))
    }

    override fun getOrganizationSubscribers(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>> {
        return callPagination(api.getOrganizationSubscribers(limit,offset))
    }
}