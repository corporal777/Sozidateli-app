package com.example.repository

import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.models.*
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
            .map { interests ->
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

    override fun getFilterRegions(): Maybe<List<SearchRegion>> {
        return if (appData.filterRegionsList.isNullOrEmpty()) {
            newApi.getRegions().map {
                it.forEachIndexed { index, s ->
                    appData.filterRegionsList.add(SearchRegion(index, s))
                }
                appData.filterRegionsList
            }
        } else Maybe.just(appData.filterRegionsList)
    }

    override fun getFilterTowns(type: String, region: String): Maybe<List<SearchTown>> {
        return Maybe.defer {
            when (type) {
                "event" -> newApi.getEventsTowns(region)
                "user" -> newApi.getUsersTowns(region)
                else -> newApi.getOrganizationsTowns(region)
            }
        }.map { it.mapIndexed { index, s -> s.apply { id = index } } }
    }

    override fun getSettlements(region: String): Maybe<List<SearchRegion>> {
        return newApi.getSettlements(region).map {
            it.data.mapIndexed { index, s -> SearchRegion(index, s) }
        }
    }
}