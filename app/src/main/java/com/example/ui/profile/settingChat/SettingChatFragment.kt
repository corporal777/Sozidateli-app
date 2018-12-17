package com.example.ui.profile.settingChat

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.User
import com.example.ui.base.BaseFragment
import com.example.util.SETTING_TYPE_CHAT_ALL
import com.example.util.SETTING_TYPE_CHAT_FAVORITE
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_setting_chat.*
import javax.inject.Inject
import javax.inject.Provider

class SettingChatFragment : BaseFragment(), SettingChatContract.View {

    @InjectPresenter
    lateinit var presenter: SettingChatPresenter

    @Inject
    lateinit var presenterProvider: Provider<SettingChatPresenter>

    @ProvidePresenter
    fun providePresenter(): SettingChatPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchOnlyFavorite.setOnCheckedChangeListener { compoundButton, b ->
            presenter.onChangeSetting(SETTING_TYPE_CHAT_FAVORITE,b)
        }

        switchAllUsers.setOnCheckedChangeListener { compoundButton, b ->
            presenter.onChangeSetting(SETTING_TYPE_CHAT_ALL,b)
        }
    }

    override fun setSetting(user: User) {
        switchAllUsers.isChecked = user.chatAllowAllUsers
        switchOnlyFavorite.isChecked = user.chatAllowFromFavorite
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_setting_chat
}
