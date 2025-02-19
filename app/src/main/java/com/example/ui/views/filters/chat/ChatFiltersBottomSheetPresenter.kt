package com.example.ui.views.filters.chat

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import com.example.ui.views.filters.user.UserFiltersBottomSheetContract
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class ChatFiltersBottomSheetPresenter
@Inject constructor(
    val appData: AppData,
    commonRepository: CommonRepository,
    eventRepository: EventRepository
) : BaseFiltersBottomSheetPresenter<ChatFiltersBottomSheetContract.View>(commonRepository, eventRepository),
    ChatFiltersBottomSheetContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.initDaDataAddress()
    }

}