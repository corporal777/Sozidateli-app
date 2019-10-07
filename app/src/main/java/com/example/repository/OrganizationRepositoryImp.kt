package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Organization
import com.example.data.models.OrganizationData
import com.example.data.models.OrganizationMember
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


    override fun subscribe(orgId: String): Completable {
        return call(api.organizationSubscribe(orgId))
    }

    override fun unsubscribe(orgId: String): Completable {
        return call(api.organizationUnsubscribe(orgId))
    }

    override fun subscribeList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>> {
        return callPagination(api.organizationSubscribeList(limit, offset))
    }

    override fun getOrganizationList(): Single<List<Organization>> {
        return call(api.getOrganizationList())
    }

    override fun getOrganizationById(id: String): Single<OrganizationData> {
        return call(api.getOrganizationById(id))
    }

    override fun getMembers(limit: Int, offset: Int, orgId: String): Maybe<PaginationResponse<OrganizationMember>> {
        return callPagination(api.organizationMembers(orgId, limit, offset))
    }
}