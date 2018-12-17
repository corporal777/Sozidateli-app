package com.example.ui.mySchedule.usersList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.ChatRepository
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class UserListPresenter
@Inject constructor(private val dummyRepository: DummyRepository
) : BasePresenter<UserListContract.View>(), UserListContract.Presenter {


    lateinit var subevent_id: String


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        SimplePagination { limit, offset -> dummyRepository.loadFavoriteSpeakers(limit, offset) }
                .create()
                .subscribe({ viewState.apply { setUsers(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }


}
