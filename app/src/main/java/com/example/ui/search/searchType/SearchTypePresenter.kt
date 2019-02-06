package com.example.ui.search.searchType

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SearchTypeEvent
import com.example.events.OnAddSearchTypeEvent
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SearchTypePresenter
@Inject constructor(private val dummyRepository: DummyRepository
) : BasePresenter<SearchTypeContract.View>(), SearchTypeContract.Presenter {

    private var data: MutableList<SearchTypeEvent> = ArrayList()
    private var selectedData: MutableList<SearchTypeEvent> = ArrayList()
    private var isPlaces: Boolean = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }


    override fun attachView(view: SearchTypeContract.View?) {
        super.attachView(view)
        dummyRepository.loadSearchType()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    data.addAll(it)
                    data.forEach { item ->
                        selectedData.forEach { selectedItem ->
                            if (item.id == selectedItem.id) {
                                item.selected = true
                            }
                        }
                    }
                    viewState.setData(data)
                }, {

                }).call(compositeDisposable)
    }

    override fun setData(data: MutableList<SearchTypeEvent>, isPlaces: Boolean) {
        this.selectedData = data
        this.isPlaces = isPlaces
    }

    override fun save() {
        EventBus.getDefault().post(OnAddSearchTypeEvent(selectedData, isPlaces))
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
