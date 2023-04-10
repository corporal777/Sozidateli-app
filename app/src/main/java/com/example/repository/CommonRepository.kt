package com.example.repository

import com.example.data.models.*
import io.reactivex.Maybe

interface CommonRepository {

    fun getInterests(): Maybe<List</*Interest*/InterestNew>>

    //fun getAgreement(): Maybe<Agreement>

    //fun getEventFormats(): Maybe<List<EventFormat/*NewEventFormat*/>>

    fun getFilterRegions() : Maybe<List<SearchRegion>>
    fun getFilterTowns(type : String, region : String) : Maybe<List<SearchTown>>
}