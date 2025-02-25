package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
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
        return api.getOrganizationMembers(map)
            .map {
                PaginationResponse(it.totalCount, it.data)
            }
    }

    override fun getOrganizationMembersWithoutPagination(map: Map<String, Any>): Maybe<List<OrganizationMemberModel>> =
        api.getOrganizationMembersWithoutPagination(map)
            .map { it.data }


    override fun getOrganizationDetails(organizationId: String): Single<OrganizationNew> {
        return api.getOrganizationDetails(organizationId, "userFavorite")
    }

    override fun getFavoriteOrganization(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew>> {
        return api.getFavoritesList(map)
            .map {
                it.data.forEach { org ->
                    org.entity?.model?.binds = OrganizationBindsModel(
                        userFavorite = EventUserFavorite(
                            org.id?.toLong(),
                            org.user
                        )
                    )
                }
                PaginationResponse(it.totalCount, it.data.mapNotNull { org -> org.entity?.model })
            }
    }

    //+
    override fun searchOrganizations(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew>> {
        return api.searchGlobal(map)
            .map { PaginationResponse(it.organizations.count, it.organizations.data) }
    }

    override fun getOrganizationsWithActiveEvents(): Maybe<List<OrganizationNew>> {
        return if (appData.organizationsActiveEvents.isNullOrEmpty()) {
            api.getOrganizationsWithActiveEvents().map {
                it.data.forEach { s -> appData.organizationsActiveEvents.add(s) }
                appData.organizationsActiveEvents
            }
        } else Maybe.just(appData.organizationsActiveEvents)
    }

    override fun addOrRemoveOrgFavorite(org: OrganizationNew?): Single<Optional<EventUserFavorite>> {
        return if (org?.binds?.userFavorite != null)
            deleteFromFavorites(org.binds?.userFavorite?.id.toString())
                .andThen(Single.just(Optional(null)))
        else addOrgToFavorites(org?.id.toString())
            .map { Optional(EventUserFavorite(it.id, it.user)) }
    }

    private fun addOrgToFavorites(orgId: String): Single<AddFavoriteModel> {
        return if (appData.isTemporaryUser()) api.addToTempFavorite(
            AddToFavoriteModel(
                appData.getTempId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                    orgId.toInt()
                )
            )
        ).map { AddFavoriteModel(it.id, it.tempUser) }
        else api.addToFavorite(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                    orgId.toInt()
                )
            )
        )
    }

    private fun deleteFromFavorites(id: String): Completable {
        return if (appData.isTemporaryUser()) api.deleteFromTempFavorite(id)
        else api.deleteFromFavorite(id)
    }
}