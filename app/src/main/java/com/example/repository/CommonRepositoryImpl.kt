package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Interest
import io.reactivex.Maybe
import javax.inject.Inject

class CommonRepositoryImpl
@Inject constructor(
        private val appData: AppData,
        private val api: Api
) : ApiRepository(appData), CommonRepository {

    override fun getInterests(): Maybe<List<Interest>> {
        val cachedInterests = appData.interests
        return if (cachedInterests.isNullOrEmpty()) call(api.getInterestsList())
                .doOnSuccess { appData.interests = it }
        else Maybe.just(cachedInterests)
    }
}