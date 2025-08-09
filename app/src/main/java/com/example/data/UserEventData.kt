package com.example.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.examle.data.models.EventScheduleCalendarDay
import com.examle.data.models.UserEvent
import com.example.data.database.EventMemberDao
import com.example.data.database.UserEventDao
import com.example.data.database.UserQrImageDao
import com.examle.data.models.EventActivity
import com.examle.data.models.EventActivityModel
import com.examle.data.models.EventDate
import com.examle.data.models.EventMember
import com.examle.data.models.EventScheduleDay
import com.examle.data.models.ImageModel
import com.examle.data.models.MemberModel
import com.examle.data.models.Optional
import com.examle.data.models.Tag
import com.examle.data.models.UserQrImage
import com.examle.data.models.asOptional
import com.example.common.calendar
import com.example.common.defaultServerDateFormatter
import com.example.common.isSameDay
import com.examle.domain.repository.EventRepository
import com.example.util.toByArray
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom


class UserEventData(
    private val eventRepository: EventRepository,
    private val userEventDao: UserEventDao,
    private val eventMemberDao: EventMemberDao,
    private val userQrImageDao: UserQrImageDao
) {

    var days: List<EventScheduleCalendarDay>? = null
        private set
    private var userEvent: UserEvent? = null

    var isDataFromLocalStorage = false
    var dataLoadingDate = 0L

    private val compositeDisposable = CompositeDisposable()
    //private val onDataUpdateListeners: MutableList<OnDataUpdateListener> = mutableListOf()
//
//
//    fun loadUserQrImage(image: ImageModel?, qrLink: String): Single<Optional<Bitmap>> {
//        val imageName = if (image?.name == "default_avatar.png") image.name else image?.fileId
//        return userQrImageDao.getByName(imageName ?: "")
//            .map {
//                if (imageName == it.imageName && qrLink == it.imageQrUrl) {
//                    BitmapFactory.decodeByteArray(it.imageData, 0, it.imageData.size).asOptional()
//                } else Optional(null)
//            }.onErrorResumeNext { Single.just(Optional(null)) }
//    }
//
//    fun insertUserQrImage(bitmap: Bitmap, image: ImageModel?, qrLink: String): Maybe<Bitmap> {
//        return userQrImageDao.deleteAll()
//            .andThen(Completable.fromAction {
//                val qrImage = UserQrImage(
//                    image?.id ?: "",
//                    if (image?.name == "default_avatar.png") image.name else image?.fileId ?: "",
//                    qrLink,
//                    bitmap.toByArray()
//                )
//                userQrImageDao.insert(qrImage)
//            }.andThen(Maybe.just(bitmap)))
//    }
//
//    fun loadEventData(eventId: String): Maybe<UserEvent> {
//        val eventRequest = eventRepository.getEventDetails(eventId)
//        val eventActivities = eventRepository.getEventActivities(eventId.toInt())
//
//        return Maybe.zip(eventRequest, eventActivities) { event, activities ->
//            val activityDates = activities.groupBy { it.holdingDate?.from?.split(" ")?.get(0) }
//            val dates = activityDates.map { EventDate(it.key ?: "", it.value.size) }
//            UserEvent(
//                event.id.toString(),
//                event,
//                EventActivity(
//                    activities,
//                    dates,
//                    event.binds?.tag?.map { Tag.EventTag(it.id.toString(), it.name ?: "") }
//                        ?: emptyList(),
//                    arrayListOf()),
//                System.currentTimeMillis())
//        }
//        //.doOnSuccess { userEventDao.insert(it) }
//        //.onErrorResumeNext(loadEventCache(eventId).toMaybe())
//    }
//
//    private fun loadEventCache(event: String): Single<UserEvent> {
//        return userEventDao.getById(event)
//            .doOnSuccess { it.isDataFromLocalStorage = true }
//    }
//
//
//    fun createEventScheduleDay(date: String?): EventScheduleDay? {
//        if (date.isNullOrEmpty()) return null
//
//        val millis = defaultServerDateFormatter.parse(date)?.time ?: 0
//        val cal = millis.calendar()
//        return EventScheduleDay(
//            ThreadLocalRandom.current().nextInt(0, 1000),
//            date,
//            millis,
//            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
//            cal.get(Calendar.DAY_OF_MONTH),
//            true
//        )
//    }
//
//    fun isHasEventSearchText(text: String, event: EventActivityModel): Boolean {
//        var isHas = false
//        if (event.description?.contains(text, true) == true) isHas = true
//        else if (event.title?.contains(text, true) == true) isHas = true
//        else {
//            if (event.binds == null || event.binds.users.isNullOrEmpty()) isHas = false
//            else {
//                val users = event.binds.users.filterNotNull()
//                isHas = users.any { x -> x.nameLastName.contains(text, true) }
//            }
//        }
//        return isHas
//    }
//
//
//    fun collectDatesToWeeks(dates: List<EventScheduleDay>): List<List<EventScheduleDay>> {
//        var countSize = 0
//        val listDays = arrayListOf<EventScheduleDay>()
//        val days = arrayListOf<List<EventScheduleDay>>()
//        dates.forEach {
//            listDays.add(it)
//            countSize++
//            if (listDays.size == 7) {
//                val list = arrayListOf<EventScheduleDay>()
//                list.addAll(listDays)
//                days.add(list)
//                listDays.clear()
//            } else {
//                if (countSize == dates.size) {
//                    val list = arrayListOf<EventScheduleDay>()
//                    list.addAll(listDays)
//                    days.add(list)
//                }
//            }
//        }
//        return days
//    }
//
//    fun findNearestDay(eventDates: List<EventScheduleDay>): EventScheduleDay? {
//        val date = System.currentTimeMillis()
//        val days = eventDates
//        val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
//        val other = Calendar.getInstance()
//        return days.find {
//            (dateCalendar.isSameDay(other.apply {
//                timeInMillis = it.millis
//            }) || it.millis - date > 0)
//        } ?: days.lastOrNull()
//    }
//
//
//    fun clear() {
//        days = null
//        userEvent = null
//        isDataFromLocalStorage = false
//        dataLoadingDate = 0L
//        onDataUpdateListeners.clear()
//    }
//
//    private fun performOnDataUpdate() {
//        onDataUpdateListeners.forEach { it.onDataUpdated() }
//    }
//
//    fun addOnDataUpdateListener(onDataUpdateListener: OnDataUpdateListener) {
//        if (!onDataUpdateListeners.contains(onDataUpdateListener))
//            onDataUpdateListeners.add(onDataUpdateListener)
//    }
//
//    fun removeOnDataUpdateListener(onDataUpdateListener: OnDataUpdateListener) {
//        onDataUpdateListeners.remove(onDataUpdateListener)
//    }
//
//    interface OnDataUpdateListener {
//        fun onDataUpdated()
//    }
//
//
//    fun insertEventMembers(eventMember: EventMember) {
//        eventMemberDao.insert(eventMember)
//    }
//
//    fun updateEventMembers(eventMember: EventMember) {
//        eventMemberDao.update(eventMember)
//    }
//
//    private fun getSortedSpeakersFromLocalDb(id: String): Single<EventMember> {
//        return eventMemberDao.getById(id)
//    }
//
//    fun loadSpeakers(id: String): Single<List<MemberModel>> {
//        return Single.create { emitter ->
//            val disposable = CompositeDisposable()
//            disposable += getSortedSpeakersFromLocalDb(id)
//                .subscribe(
//                    { emitter.onSuccess(it.members) },
//                    {
//                        it.printStackTrace()
//                        disposable += eventRepository.getSpeakersWithoutPagination(
//                            mapOf(
//                                MemberModel.MEMBER_EVENT to id,
//                                MemberModel.MEMBER_ROLE to MemberModel.MEMBER_ROLE_SPEAKER,
//                                MemberModel.MEMBER_BINDS to "user"
//                            )
//                        ).subscribe(
//                            { list ->
//                                val mSortedList = arrayListOf<MemberModel>()
//                                mSortedList.addAll(list.filter { x -> x.isLead == true }
//                                    .sortedBy { x -> x.binds?.user?.fullName })
//                                mSortedList.addAll(list.filter { x -> x.isLead == false }
//                                    .sortedBy { x -> x.binds?.user?.fullName })
//                                insertEventMembers(
//                                    EventMember(
//                                        id,
//                                        mSortedList,
//                                        System.currentTimeMillis()
//                                    )
//                                )
//                                emitter.onSuccess(mSortedList)
//                            },
//                            { error ->
//                                error.printStackTrace()
//                                emitter.onError(error)
//                            }
//                        )
//                    })
//            emitter.setDisposable(disposable)
//        }
//    }
}