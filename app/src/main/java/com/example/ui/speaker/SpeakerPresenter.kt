package com.example.ui.speaker

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.User
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class SpeakerPresenter
@Inject constructor(private val chatRepository: ChatRepository
) : BasePresenter<SpeakerContract.View>(), SpeakerContract.Presenter{


    lateinit var user: User

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUser(user)
    }

    override fun onWriteMsgClick() {

    }

    override fun onAddFavoriteClick() {

    }
}
