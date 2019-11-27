package com.example.ui.search.user

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.holders.PlaceholderItem
import com.example.holders.SearchUserItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.kotlinandroidextensions.Item
import initDropDownView
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import kotlinx.android.synthetic.main.layout_filter_user.view.*
import kotlinx.android.synthetic.main.layout_filter_user.view.etAddress
import kotlinx.android.synthetic.main.layout_filter_user.view.etName
import kotlinx.android.synthetic.main.layout_filter_user.view.tilSpec
import kotlinx.android.synthetic.main.layout_filter_user.view.tvSpec
import kotlinx.android.synthetic.main.layout_filter_user.view.tvSubscription
import kotlinx.android.synthetic.main.layout_filter_user.view.tvTheme
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : SearchFragment<SearchUserPresenter, User, SearchFilter.User>(), SearchUserContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()

    override fun createItem(itemData: User?): Item {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.USER)
        else SearchUserItem(
                itemData.user_id,
                itemData.fullName,
                itemData.user_avatar,
                itemData.user_address
        ) { presenter.onUserClick(itemData) }
    }

    override fun showUser(user: User) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.user_id.toString()))
    }

    override fun createFilterView(filter: SearchFilter.User): View {
        return layoutInflater.inflate(R.layout.layout_filter_user, null).apply {
            initTextFilter(etName, filter.name) { filter.name = it }
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged { filter.address = it.toString() }
            }
            initTextFilter(etEmail, filter.email) { filter.email = it }
            initTextFilter(etPhone, filter.phone) { filter.phone = it }
            initBiFilter(tvSubscription, resources.getStringArray(R.array.favorites_status).toList(), filter.favorites) { filter.favorites = it }

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                llInterests.isVisible = false
            } else {
                initInterests(interests, tvTheme, tilSpec, tvSpec, filter.theme, filter.spec) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                llInterests.isVisible = true
            }

            val ageFrom = getString(R.string.search_filter_age_from)
            val ageTo = getString(R.string.search_filter_age_to)
            val availableAges = SearchFilter.User.AGE_MIN..SearchFilter.User.AGE_MAX
            initDropDownView(tvAgeFrom, availableAges.associateBy { "$ageFrom $it" }, filter.ageFrom?.let { "$ageFrom $it" }, null) {
                filter.ageFrom = it
            }
            initDropDownView(tvAgeTo, availableAges.associateBy { "$ageTo $it" }, filter.ageTo?.let { "$ageTo $it" }, null) {
                filter.ageTo = it
            }
        }
    }

    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etName.text = null
            etAddress.text = null
            etEmail.text = null
            etPhone.text = null
            tvSubscription.setText(filterNotChosenVariant)
            tvTheme.setText(filterNotChosenVariant)
            tvSpec.setText(filterNotChosenVariant)
            tvAgeFrom.text = null
            tvAgeTo.text = null
        }
    }
}