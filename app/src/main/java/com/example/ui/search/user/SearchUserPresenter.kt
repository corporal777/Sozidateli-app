package com.example.ui.search.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import javax.inject.Inject

@InjectViewState
class SearchUserPresenter
@Inject constructor(
        appData: AppData,
        userRepository: UserRepository,
        commonRepository: CommonRepository,
        eventRepository: EventRepository
) : AbstractSearchUserPresenter<SearchUserContract.View>(appData, userRepository, commonRepository, eventRepository)