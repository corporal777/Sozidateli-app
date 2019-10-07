package com.example.repository

import com.example.data.models.Organization
import com.example.data.models.OrganizationData
import com.example.data.models.OrganizationMember
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface OrganizationRepository {
    fun subscribe(orgId: String): Completable
    fun unsubscribe(orgId: String): Completable
    fun subscribeList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>>
    fun getOrganizationList(): Single<List<Organization>>
    fun getOrganizationById(id: String): Single<OrganizationData>
    fun getMembers(limit: Int, offset: Int, orgId: String): Maybe<PaginationResponse<OrganizationMember>>
}