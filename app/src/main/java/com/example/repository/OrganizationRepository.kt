package com.example.repository

import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface OrganizationRepository {
    //fun subscribe(orgId: String): Completable
    //fun unsubscribe(orgId: String): Completable
    //fun getOrganizations(limit: Int, offset: Int, filter: Map<String, Any>? = null): Maybe<PaginationResponse<Organization?>>
    //fun getOrganizationById(id: String): Single<OrganizationData>
    //fun getMembers(limit: Int, offset: Int, orgId: String): Maybe<PaginationResponse<OrganizationMember>>
    fun searchOrganizations(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>>
    fun getOrganizationDetails(organizationId : String): Single<OrganizationNew>
    fun getFavoriteOrganization(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>>
    fun getOrganizationMembers(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNewMemberModel>>
    fun getOrganizationMembersWithoutPagination(map: Map<String, Any>): Maybe<List<OrganizationMemberModel>>
    fun searchOrganizationsNew(map: Map<String, Any>): Maybe<PaginationResponse<OrganizationNew?>>
}