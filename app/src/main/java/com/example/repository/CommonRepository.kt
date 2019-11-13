package com.example.repository

import com.example.data.models.Agreement
import com.example.data.models.Interest
import io.reactivex.Maybe

interface CommonRepository {

    fun getInterests(): Maybe<List<Interest>>

    fun getAgreement(): Maybe<Agreement>
}