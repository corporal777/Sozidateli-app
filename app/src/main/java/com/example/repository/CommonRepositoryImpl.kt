package com.example.repository

import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.models.Agreement
import com.example.data.models.EventFormat
import com.example.data.models.Interest
import com.example.data.models.InterestNew
import com.example.extensions.groupByNotNull
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class CommonRepositoryImpl
@Inject constructor(
        private val appData: AppData,
        private val api: Api,
        private val newApi: NewApi
) : ApiRepository(appData), CommonRepository {

    override fun getInterests(): Maybe<List<InterestNew>> {
        val cachedInterests = appData.interestsNew
        return if (cachedInterests.isNullOrEmpty()) newApi.getInterestsList(200, null)
                .map {interests ->
                    val capitalizedInterests = interests.data
                    appData.interestsNew = capitalizedInterests
                    capitalizedInterests
                } else Maybe.just(cachedInterests)
    }

    /*override fun getAgreement(): Maybe<Agreement> {
        return call(api.getUserAgreement())
    }

    override fun getEventFormats(): Maybe<List<EventFormat>> {
        return call(api.getEventFormats())
    }*/
}