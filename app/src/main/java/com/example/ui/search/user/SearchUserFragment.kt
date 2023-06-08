package com.example.ui.search.user

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SearchFilter
import com.example.data.models.SearchUserData
import com.example.data.models.UserDetail
import com.example.databinding.LayoutFilterEventSearchBinding
import com.example.databinding.LayoutFilterUserBinding
import com.example.databinding.LayoutFilterUserSearchBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.search.SearchFragment
import com.example.ui.search.event.SearchEventPresenter
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import com.example.util.initInput
import com.xwray.groupie.Group
import initDropDownView
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : SearchFragment<SearchUserPresenter, UserDetail, SearchFilter.UserNew>(),
    SearchUserContract.View {

    @InjectPresenter
    override lateinit var searchPresenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()


    override fun createItem(itemData: UserDetail?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.USER)
        else UserItem(
            itemData.id,
            itemData.nameLastName,
            itemData.address?.city,
            itemData.loadUserImage(),
            { searchPresenter.onUserClick(itemData) },
            itemData.getUserSubscribeAction(),
            { searchPresenter.onUserActionCLick(itemData) })
    }


    override fun updateUser(user: UserDetail) {
        val idLong = user.id.toLong()
        val item = adapter.findItemBy { userItem: UserItem -> userItem.id == idLong } ?: return
        item.notifyChanged(user.getUserSubscribeAction())
    }

    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }


    override fun createFilterView(filter: SearchFilter.UserNew): View {
        return LayoutFilterUserSearchBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            .apply {
//                etAddress.apply {
//                    setTextWithoutSearch(filter.address)
//                    onTextChanged {
//                        filter.address = it.toString()
//                        if (filter.address.isNullOrBlank()) filter.setAddressFilter(null)
//                    }
//                    onDataSelectedListener = { filter.setAddressFilter(it) }
//                }

                initRegions(filter, tvRegion, tilRegion, tilTown)
                initTowns(filter, tvTown, tilTown)

                initUserInterests(filter, this)
                initAgeFrom(filter, this)
                initAgeTo(filter, this)
            }.root
    }


    private fun initUserInterests(filter: SearchFilter.UserNew, binding: LayoutFilterUserSearchBinding) {
        binding.apply {
            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                tilTheme.isVisible = false
                tilSpec.isVisible = false
            } else {
                initInterests(
                    interests,
                    tvTheme,
                    tilSpec,
                    tvSpec,
                    filter.theme,
                    filter.spec
                ) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                tilTheme.isVisible = true
                tilSpec.isVisible = true
            }
        }
    }

    private fun initAgeFrom(filter: SearchFilter.UserNew, binding: LayoutFilterUserSearchBinding) {
        binding.apply {
            initDropDownView(
                tvAgeFrom,
                searchPresenter.getAgesList(null),
                searchPresenter.getAgesList(null).find { it.toInt() == filter.ageFrom },
                null,
                { it },
                { it },
                {
                    filter.ageFrom = it?.toInt()
                    initAgeTo(filter, binding)
                }
            )
        }
    }

    private fun initAgeTo(filter: SearchFilter.UserNew, binding: LayoutFilterUserSearchBinding) {
        binding.apply {
            initDropDownView(
                tvAgeTo,
                searchPresenter.getAgesList(filter.ageFrom),
                searchPresenter.getAgesList(filter.ageFrom).find { it.toInt() == filter.ageTo },
                null,
                { it },
                { it },
                { filter.ageTo = it?.toInt() }
            )
        }
    }

    override fun clearFilterView(filterView: View) {
        LayoutFilterUserSearchBinding.bind(filterView).apply {
            //etAddress.text = null
            tvRegion.text = null
            tvTown.text = null
            tvTheme.text = null
            tvSpec.text = null
            tvAgeFrom.text = null
            tvAgeTo.text = null
        }
    }
}