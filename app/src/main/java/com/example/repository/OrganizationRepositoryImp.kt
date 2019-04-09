package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Organization
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class OrganizationRepositoryImp
@Inject constructor(
        private val api: Api,
        appData: AppData
) : ApiRepository(appData), OrganizationRepository {


    override fun subscribe(orgId: Int): Completable {
        return call(api.organizationSubscribe(orgId))
    }

    override fun unsubscribe(orgId: Int): Completable {
        return call(api.organizationUnsubscribe(orgId))
    }

    override fun subscribeList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>> {
        return callPagination(api.organizationSubscribeList(limit, offset))
    }

    override fun addToFavorite(orgId: Int): Completable {
        return call(api.organizationAddToFavorite(orgId))
    }

    override fun removeFromFavorite(orgId: Int): Completable {
        return call(api.organizationRemoveFromFavorite(orgId))
    }

    override fun favoriteList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>> {
        return callPagination(api.organizationFavoriteList(limit, offset))
    }

    override fun getOrganizationList(): Single<List<Organization>> {
        return call(api.getOrganizationList())
    }
}