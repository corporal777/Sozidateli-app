package com.example.repository

import com.example.data.models.EventRegisterResponse
import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import retrofit2.http.PartMap

interface EventRepository {
    fun getEventList(limit: Int, offset: Int): Maybe<PaginationResponse<Event>>
    fun getEventNewsList(eventId:Int,limit: Int, offset: Int): Maybe<PaginationResponse<News>>
    fun getNewsById(eventId:Int,newsId:Int): Single<News>
    fun getEventDocuments(eventId:Int,limit: Int, offset: Int): Maybe<PaginationResponse<Document>>
    fun getEventRegisterField(eventId:Int): Single<RegisterFieldResponse>
    fun eventRegister(eventId: Int, fields: Map<String, RequestBody?>, files: List<MultipartBody.Part?>?): Completable
    fun getEventRegister(eventId: Int): Single<EventRegisterResponse>
}