package com.example.repository

import io.reactivex.Completable

interface SpeakerRepository {
    fun addToFavorite(speakerId: Int): Completable
    fun removeFromFavorite(speakerId: Int): Completable
}