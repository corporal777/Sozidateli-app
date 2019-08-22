package com.example.repository

import com.example.api.ApiDataData
import com.example.data.DaDataRequestBody
import javax.inject.Inject

class DaDataRepositoryImp
@Inject constructor(
        private val apiDataData: ApiDataData
) : DaDataRepository {

    override fun suggestCity(query: String) = apiDataData.suggestCity(DaDataRequestBody(query))
}