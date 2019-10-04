package com.example.repository

import com.example.data.models.Organization
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface OrganizationRepository {
    fun subscribe(orgId: Int): Completable
    fun unsubscribe(orgId: Int): Completable
    fun subscribeList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>>
    fun addToFavorite(orgId: Int): Completable
    fun removeFromFavorite(orgId: Int): Completable
    fun favoriteList(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>>
    fun getOrganizationList(): Single<List<Organization>>
    fun getOrganizationById(id: String): Single<Organization>
}