package com.example.ui.views.suggestFieldView.organization

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheetContract
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventOrgBottomSheetPresenter @Inject constructor(
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository
) : MvpPresenter<EventOrgBottomSheetContract.View>(),
    EventOrgBottomSheetContract.Presenter {

    val listOrganizations = arrayListOf<OrganizationNew>(getEmptyOrganization(""))
    private val compositeDisposable = CompositeDisposable()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (listOrganizations.isNullOrEmpty()) {
            compositeDisposable += organizationRepository.getOrganizationsWithActiveEvents()
                .doOnSuccess { listOrganizations.addAll(it) }
                .performOnBackgroundOutOnMain()
                .subscribeBy {
                    viewState.setOrganizations(listOrganizations)
                }
        } else viewState.setOrganizations(listOrganizations)
    }


    override fun onOrganizationChange(name: String) {
        compositeDisposable += Maybe.fromCallable {
            listOrganizations[0] = getEmptyOrganization(name)
            if (name.isNullOrBlank()) listOrganizations
            else listOrganizations.filter { isContainsOrg(it, name) }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setOrganizations(it)
            }
    }

    override fun onOrganizationSelected(name: String) {
        compositeDisposable +=  Maybe.defer {
            val item = listOrganizations.findLast { x -> isEqualsOrg(x, name) }
            if (item == null) Maybe.just(getEmptyOrganization(null))
            else Maybe.just(item)
        }
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.performOnItemSelected(it)
            }
    }

    private fun isContainsOrg(organization: OrganizationNew?, query: String): Boolean {
        return organization?.legalInformation?.name?.short?.contains(query, true) == true
    }

    private fun isEqualsOrg(organization: OrganizationNew?, query: String): Boolean {
        return organization?.legalInformation?.name?.short == query
                || organization?.legalInformation?.name?.full == query
    }

    private fun getEmptyOrganization(name : String?): OrganizationNew {
        return OrganizationNew(
            id = null,
            legalInformation = LegalInformationModel(LegalInformationNameModel(name, name))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}