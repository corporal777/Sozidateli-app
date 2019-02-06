package com.example.ui.profile.settingChat

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.SETTING_TYPE_CHAT_ALL
import com.example.util.SETTING_TYPE_CHAT_FAVORITE
import javax.inject.Inject

@InjectViewState
class SettingChatPresenter
@Inject constructor(private val appData: AppData
) : BasePresenter<SettingChatContract.View>(), SettingChatContract.Presenter {

    override fun attachView(view: SettingChatContract.View?) {
        super.attachView(view)
        viewState.setSetting(appData.user)
    }

    override fun onChangeSetting(type: String, isEnabled: Boolean) {
        when (type) {
            SETTING_TYPE_CHAT_ALL -> appData.user.settings_chat_allow_msg_from_all = isEnabled
            SETTING_TYPE_CHAT_FAVORITE -> appData.user.settings_chat_allow_msg_from_fav = isEnabled
        }
    }
}
