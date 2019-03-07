package com.example.repository

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Field

interface OrganizationRepository {
    fun subscribeOrganization(orgId: Int):Single<AuthResponse>
    fun unsubscribeOrganization(orgId: Int):Single<AuthResponse>
    fun getOrganizationSubscribers(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>>
}