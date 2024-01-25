package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import javax.inject.Inject

class CommonRepositoryImpl
@Inject constructor(
    private val appData: AppData,
    private val api: Api
) : ApiRepository(appData), CommonRepository {

    override fun getInterests(): Maybe<List<InterestNew>> {
        val cachedInterests = appData.interests
        return if (cachedInterests.isNullOrEmpty()) api.getInterestsList(200, null)
            .map { interests ->
                val capitalizedInterests = interests.data
                appData.interests = capitalizedInterests
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
            api.getRegions().map {
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
                "event" -> api.getEventsTowns(region)
                "user" -> api.getUsersTowns(region)
                else -> api.getOrganizationsTowns(region)
            }
        }.map { it.mapIndexed { index, s -> s.apply { id = index } } }
    }

    override fun getSettlements(region: String): Maybe<List<SearchRegion>> {
        return api.getSettlements(region).map {
            it.data.mapIndexed { index, s -> SearchRegion(index, s) }
        }
    }

    override fun getSupportData(): Maybe<List<SupportData>> {
        return api.getSupportData().doOnSuccess { appData.supportQuestions = it }
    }

    override fun getSupportQuestion(id: String): Maybe<SupportData> {
        return api.getSupportData().doOnSuccess { appData.supportQuestions = it }
            .map { it.find { x -> x.id == id.toInt() } }
    }

    override fun sendSupportQuestion(body: RequestBody): Completable {
        return api.sendSupportData(body)
    }

    override fun searchSupportQuestion(search: String): Maybe<List<SupportData>> {
        return api.searchSupportQuestion(search)
    }
}