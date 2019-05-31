package com.example.repository

import com.example.api.Api
import com.example.api.ApiDataData
import com.example.data.AppData
import com.example.data.DataDataRequestBody
import com.example.data.models.DataDataResponse
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DataDataRepositoryImp
@Inject constructor(
        private val apiDataData: ApiDataData
) : DataDataRepository {

    override fun suggestCity(query: String) = apiDataData.suggestCity(DataDataRequestBody(query))
}