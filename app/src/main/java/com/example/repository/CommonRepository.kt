package com.example.repository

import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody

interface CommonRepository {

    fun getInterests(): Maybe<List</*Interest*/InterestNew>>

    //fun getAgreement(): Maybe<Agreement>

    //fun getEventFormats(): Maybe<List<EventFormat/*NewEventFormat*/>>

    fun getFilterRegions() : Maybe<List<SearchRegion>>
    fun getFilterTowns(type : String, region : String) : Maybe<List<SearchTown>>
    fun getSettlements(region : String) : Maybe<List<SearchRegion>>


    fun getSupportData() : Maybe<List<SupportData>>
    fun sendSupportQuestion(body : RequestBody) : Completable
}