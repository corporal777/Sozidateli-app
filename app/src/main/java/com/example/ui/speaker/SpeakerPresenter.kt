package com.example.ui.speaker

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SpeakerPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository
) : BasePresenter<SpeakerContract.View>(), SpeakerContract.Presenter {


    lateinit var speaker: Speaker

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun attachView(view: SpeakerContract.View?) {
        super.attachView(view)
        viewState.setSpeaker(speaker)
    }

    override fun onWriteMsgClick() {
        compositeDisposable += chatRepository.startChat(speaker.uid.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(speaker.name, speaker.photo, it.chat_id.toString())
                }, { it.printStackTrace() })
    }

    override fun onAddFavoriteClick() {
        val id = speaker.id.toString()
        compositeDisposable += speaker.isInFavorite.let {
            if (!it) userRepository.addToFavorite(id)
            else userRepository.removeFromFavorite(id)
        }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    speaker.isInFavorite = !speaker.isInFavorite
                    viewState.setSpeaker(speaker)
                }, { it.printStackTrace() })
    }
}
