package com.example.data.prefs

interface AppPrefs {
    var selectedEvent: String?
    var userToken: String?
    var isFCMTokenSent: Boolean
    var userId: Int
    var isStoriesShown: Boolean
}