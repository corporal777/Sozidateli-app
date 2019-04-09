package com.example.ui.search.searchType

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SearchTypeEvent
import com.example.events.OnAddSearchTypeEvent
import com.example.repository.DummyRepository
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SearchTypePresenter
@Inject constructor(private val organizationRepository: OrganizationRepository,
                    private val eventRepository: EventRepository
) : BasePresenter<SearchTypeContract.View>(), SearchTypeContract.Presenter {

    private var data: MutableList<SearchTypeEvent> = ArrayList()
    private var selectedData: MutableList<SearchTypeEvent> = ArrayList()
    private var isOrganization: Boolean = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }


    override fun attachView(view: SearchTypeContract.View?) {
        super.attachView(view)
        if (isOrganization) {
            organizationRepository.getOrganizationList()
                    .flatMapObservable { Observable.fromIterable(it) }
                    .map { SearchTypeEvent(it.id.toString(), it.name) }
                    .toList()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        checkSelected(it)
                        viewState.setData(it)
                    }, {
                        it.printStackTrace()
                    })
                    .call(compositeDisposable)
        } else {
            eventRepository.getCategoriesList()
                    .flatMapObservable { Observable.fromIterable(it) }
                    .map { SearchTypeEvent(it.id, it.name) }
                    .toList()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        checkSelected(it)
                        viewState.setData(it)
                    }, {
                        it.printStackTrace()
                    })
                    .call(compositeDisposable)
        }
    }

    private fun checkSelected(list: List<SearchTypeEvent>) {
        data.addAll(list)
        data.forEach { item ->
            selectedData.forEach { selectedItem ->
                if (item.id == selectedItem.id) {
                    item.selected = true
                }
            }
        }
    }

    override fun setData(data: MutableList<SearchTypeEvent>, isPlaces: Boolean) {
        this.selectedData = data
        this.isOrganization = isPlaces
    }

    override fun save() {
        EventBus.getDefault().post(OnAddSearchTypeEvent(selectedData, isOrganization))
        viewState.navigateUp()
    }


    override fun selectItem(isSelect: Boolean, item: SearchTypeEvent) {
        if (isSelect) {
            selectedData.add(item)
        } else {
            selectedData.remove(item)
        }
    }
}
