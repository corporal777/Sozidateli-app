package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import io.reactivex.Completable
import javax.inject.Inject

class SpeakerRepositoryImp
@Inject constructor(
        private val api: Api,
        appData: AppData
) : ApiRepository(appData), SpeakerRepository {

    override fun addToFavorite(speakerId: Int): Completable = api.speakerAddToFavorite(speakerId)

    override fun removeFromFavorite(speakerId: Int): Completable = api.speakerRemoveFromFavorite(speakerId)
}