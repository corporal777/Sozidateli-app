package com.example.repository

import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class OrganizationRepositoryImp
@Inject constructor(
    private val api: Api,
    private val newApi: NewApi,
    appData: AppData
) : ApiRepository(appData), OrganizationRepository {


    /*override fun subscribe(orgId: String): Completable {
        return call(api.organizationSubscribe(orgId))
    }

    override fun unsubscribe(orgId: String): Completable {
        return call(api.organizationUnsubscribe(orgId))
    }

    override fun getOrganizations(limit: Int, offset: Int, filter: Map<String, Any>?): Maybe<PaginationResponse<Organization?>> {
        return callPagination(api.getOrganizations(limit, offset, filter))
    }

    override fun getOrganizationById(id: String): Single<OrganizationData> {
        return call(api.getOrganizationById(id))
    }

    override fun getMembers(limit: Int, offset: Int, orgId: String): Maybe<PaginationResponse<OrganizationMember>> {
        return callPagination(api.organizationMembers(orgId, limit, offset))
    }*/

    override fun getOrganizationMembers(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationMemberModel>> {
        return newApi.getOrganizationMembers(map)
            .map {
                PaginationResponse(it.totalCount, it.data)
            }
    }

    override fun getOrganizationMembersWithoutPagination(map: Map<String, Any>): Maybe<List<OrganizationMemberModel>> =
        newApi.getOrganizationMembersWithoutPagination(map)
            .map { it.data }

    override fun searchOrganizations(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>> {
        return newApi.searchOrganizations(map)
            .map {
                PaginationResponse(it.totalCount, it.data ?: arrayListOf())
            }
    }

    override fun getOrganizationDetails(organizationId: String): Single<OrganizationNew> {
        return newApi.getOrganizationDetails(
            organizationId,
            "rights,leader,member,user,userFavorite"
        )
    }

    override fun getFavoriteOrganization(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>> {
        return newApi.getFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds = OrganizationBindsModel(
                        userFavorite = EventUserFavorite(
                            org.id?.toLong(),
                            org.user
                        )
                    )
                }
                PaginationResponse(it.totalCount, it.data.map { org -> org.entity?.model })
            }
    }

    //+
    override fun searchOrganizationsNew(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>> {
        return newApi.searchDataNew(map)
            .map { PaginationResponse(it.organizations.count, it.organizations.data) }
    }

    override fun getOrganizationsWithActiveEvents(): Maybe<List<OrganizationNew>> {
        return newApi.getOrganizationsWithActiveEvents()
            .map { it.data }
    }
}