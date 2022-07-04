package com.example.ui.event.speakers.list

import android.util.Log
import androidx.room.EmptyResultSetException
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.speakers.list.items.ActionTypeEventSpeakers
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository
) : BasePresenter<EventSpeakersContract.View>(appData), EventSpeakersContract.Presenter {

    lateinit var eventId: String

    val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getSpeakers(
            mapOf(
                MemberModel.MEMBER_EVENT to eventId,
                MemberModel.MEMBER_ROLE to MemberModel.MEMBER_ROLE_SPEAKER,
                /*MemberModel.MEMBER_LIMIT to limit,*/
                /*MemberModel.MEMBER_OFFSET to offset,*/
                MemberModel.MEMBER_BINDS to /*"user,userFavorite"*/"user"
            )
        )
    }
        .buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })
        loadData()
    }

    private fun loadData() {
        compositeDisposable += userEventData.getSortedSpeakersFromLocalDb(eventId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is EmptyResultSetException) {
                        getOrUpdateMembersDataFromNetwork(ActionTypeEventSpeakers.INSERT)
                    }
                    it.printStackTrace()
                }, onSuccess = { eventMember ->
                    val mList = eventMember.members
                    viewState.apply { setData(mList) }
                    getOrUpdateMembersDataFromNetwork(ActionTypeEventSpeakers.UPDATE)
                })
    }

    private fun getOrUpdateMembersDataFromNetwork(actionType: ActionTypeEventSpeakers) {
        val sortedList = arrayListOf<MemberModel>()
        processLoadMembersFromNetwork(
            actionType,
            sortedList,
            eventRepository.getSpeakersWithoutPagination(
                mapOf(
                    MemberModel.MEMBER_EVENT to eventId,
                    MemberModel.MEMBER_ROLE to MemberModel.MEMBER_ROLE_SPEAKER,
                    MemberModel.MEMBER_BINDS to "user"
                )
            )
                .flatMapCompletable { list ->
                    Completable.fromAction {
                        sortedList.addAll(list.filter { x -> x.isLead == true }
                            .sortedBy { x -> x.binds?.user?.fullName })
                        sortedList.addAll(list.filter { x -> x.isLead == false }
                            .sortedBy { x -> x.binds?.user?.fullName })
                        val eventMember =
                            EventMember(eventId, sortedList, System.currentTimeMillis())
                        when (actionType) {
                            ActionTypeEventSpeakers.INSERT -> {
                                userEventData.insertEventMembers(eventMember)
                            }
                            ActionTypeEventSpeakers.UPDATE -> {
                                userEventData.updateEventMembers(eventMember)
                            }
                        }

                    }
                }
        )
    }

    private fun processLoadMembersFromNetwork(
        actionType: ActionTypeEventSpeakers,
        members: List<MemberModel>,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    it.printStackTrace()
                },
                onComplete = {
                    when (actionType) {
                        ActionTypeEventSpeakers.INSERT -> {
                            viewState.setData(members)
                        }
                    }
                })


    }

    override fun onSpeakerClick(speaker: MemberModel) = viewState.showSpeaker(eventId, speaker)

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        loadData()
    }

}
