package com.example.ui.event.schedule

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import call
import com.example.data.UserEventData
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.SubEvent
import com.example.data.models.SubEventCheckLast
import com.example.data.models.Tag
import com.example.holders.SubEventItem
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import parseTimestamp
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.text.SimpleDateFormat
import java.util.*

abstract class EventSchedulePresenter
constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData
) : BasePresenter<EventScheduleContract.View>(), EventScheduleContract.Presenter {

    private val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    protected val event = userEventData.event!!

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var selectedTags: List<Tag> = emptyList()

    private val onSubEventClickListener = object : SubEventItem.OnSubEventClickListener {
        override fun onSubEventClick(event: SubEvent) {

        }

        override fun onAddToScheduleClick(event: SubEvent) {
            processChangeEventInCalendarStatusRequest(eventRepository.addEventToCalendar(this@EventSchedulePresenter.event.id, event.id))
        }

        override fun onRemoveToScheduleClick(event: SubEvent) {
            processChangeEventInCalendarStatusRequest(eventRepository.removeEventFromCalendar(this@EventSchedulePresenter.event.id, event.id))
        }
    }

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        val day = currentDay?.let { serverDateFormat.format(it.millis) }
                ?: return@PaginationDataSourceFactory Maybe.empty<PaginationResponse<SubEvent>>()
        val filter = createRequestFilter().toMutableMap().apply {
            put("date_start", "$day $DAY_START")
            put("date_end", "$day $DAY_END")
        }.toMap()

        eventRepository.getEventDaySchedule(event.id, filter, limit, offset)
    }.mapIndexedTotal { item, index, total ->
        SubEventItem(SubEventCheckLast(item, index == total?.minus(1)), selectedTags, onSubEventClickListener)
    }

    private val paginationConfig = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadEventScheduleStaticData()
                .andThen(findNearestDayFromEventDays(System.currentTimeMillis()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    viewState.apply {
                        userEventData.apply {
                            setDays(days)
                            setTags(categories as List<Tag> + tags as List<Tag>)
                        }
                        currentDay?.let { day -> selectDay(day) }
                    }
                }
                .observeOn(Schedulers.io())
                .andThen(RxPagedListBuilder(pagination, paginationConfig).buildFlowable(BackpressureStrategy.LATEST))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.apply { setSubEvents(it) }
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    private fun loadEventScheduleStaticData(): Completable {
        return if (userEventData.isDataLoaded) Completable.complete()
        else eventRepository.getEventInfo(event.id)
                .flatMapCompletable {
                    Completable.fromAction {
                        userEventData.apply {
                            days = createCalendarDays(it.dates.map { serverDateFormat.parseTimestamp(it.date) })
                            tags = it.tags
                            categories = it.categories
                            isDataLoaded = true
                        }
                    }
                }
    }

    private fun createCalendarDays(dates: List<Long>): List<EventScheduleCalendarDay> {
        if (dates.isEmpty()) return emptyList()
        val sortedDates = dates.sorted()
        val firsDate = sortedDates.first().let { Calendar.getInstance().apply { timeInMillis = it } }
        val lastDate = sortedDates.last().let { Calendar.getInstance().apply { timeInMillis = it } }
        val inDatesCalendar = Calendar.getInstance()

        val datesInRange = mutableListOf<EventScheduleCalendarDay>()
        while (firsDate.before(lastDate) || isSameDay(firsDate, lastDate)) {
            val dateInDates = sortedDates.find { isSameDay(inDatesCalendar.apply { timeInMillis = it }, firsDate) }

            val eventDay = EventScheduleCalendarDay(
                    firsDate.timeInMillis,
                    firsDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                            ?: "",
                    firsDate.get(Calendar.DAY_OF_MONTH),
                    dateInDates != null
            )

            datesInRange.add(eventDay)
            firsDate.add(Calendar.DATE, 1)
        }

        return datesInRange
    }

    private fun findNearestDayFromEventDays(date: Long): Completable {
        return Completable.fromAction {
            val days = userEventData.days ?: emptyList()
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (isSameDay(dateCalendar, other.apply { timeInMillis = it.millis }) || it.millis - date > 0)
            }
        }
    }

    private fun isSameDay(calendar: Calendar, other: Calendar): Boolean {
        return calendar.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == other.get(Calendar.YEAR)
    }

    private fun invalidateDay() {
        currentDay?.also { pagination.source?.invalidate() }
    }

    override fun onDaySelected(day: EventScheduleCalendarDay) {
        currentDay = day
        invalidateDay()
    }

    override fun onTagSelectedListChange(tags: List<Tag>) {
        selectedTags = tags
        invalidateDay()
    }

    private fun processChangeEventInCalendarStatusRequest(request: Completable) {
        request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ invalidateDay() }, { invalidateDay() })
                .call(compositeDisposable)
    }

    abstract fun createRequestFilter(): Map<String, Any>

    companion object {
        private const val DAY_START = "0:00"
        private const val DAY_END = "23:59"
    }
}
