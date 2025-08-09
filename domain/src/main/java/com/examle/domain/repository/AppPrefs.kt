package com.examle.domain.repository

interface AppPrefs {
    var selectedEvent: String?
    var userToken: String?
    var temporaryToken: String?
    var temporaryUserId: Int
    var isFCMTokenSent: Boolean
    var userId: Int
    var isStoriesShown: Boolean
    var uniqueDeviceId: String?
    var attemptsOfChangePassword: Int
    var updateTime : Long
}