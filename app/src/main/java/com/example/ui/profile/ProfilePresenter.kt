package com.example.ui.profile

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.data.models.Status
import com.example.data.models.User
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(private val appData:AppData) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.user = User("1","Иван Петрович","https://histrf.ru/uploads/media/person/0001/04/thumb_3560_person_full.jpeg","участник",Event("231","Тестовое мероприятие","","",0,0,"Test LDT", Status.APPROVED),"ivanivanov@yandex.ru","+7 (932) 451-21-33",0,"Москва","https://vk.com/id_0","Высшее политтехническое Депутат академии высших научных дипломированных специалистов в области шкафостроения Директор кандидатов наук",institution = "Коледж им. ПТУ", speciality = "Технолог")
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState?.apply {
            setUser(appData.user)
        }
    }

    override fun clickAboutStatus() = viewState.showAboutStatus()


    override fun clickFullProfile() = viewState.showFullProfile()

    override fun clickFavorite() = viewState.showFavorite()

    override fun clickMyEvents() = viewState.showMyEvents()

    override fun clickTabEvents() = viewState.showTabEvents()

    override fun clickCurrentEvent(event: Event) {
        appData.user.currentEvent?.let {
            viewState.showCurrentEvent(it)
        }
    }

    override fun clickAboutApp() = viewState.showAboutApp()

    override fun clickChatSetting() = viewState.showChatSetting()
}
