package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.Agreement
import com.example.data.models.EventFormat
import com.example.data.models.Interest
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class CommonRepositoryImpl
@Inject constructor(
        private val appData: AppData,
        private val api: Api
) : ApiRepository(appData), CommonRepository {

    override fun getInterests(): Maybe<List<Interest>> {
        val cachedInterests = appData.interests
        return if (cachedInterests.isNullOrEmpty()) call(api.getInterestsList())
                .map { interests ->
                    val capitalizedInterests = interests.map { Interest(it.id, it.parent, it.value.capitalize()) }
                    appData.interests = capitalizedInterests
                    capitalizedInterests
                }
        else Maybe.just(cachedInterests)
    }

    override fun getAgreement(): Maybe<Agreement> {
        return call(api.getUserAgreement())
    }

    override fun getEventFormats(): Maybe<List<EventFormat>> {
        return call(api.getEventFormats())
    }
}