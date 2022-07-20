package com.example.ui.userSessions

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentChatBinding
import com.example.databinding.FragmentUserSessionsBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventPageItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.chat.ChatContract
import com.example.ui.profile.ProfileFragmentArgs
import com.example.ui.profile.ProfilePresenter
import com.example.ui.userSessions.items.*
import com.example.ui.views.dialogs_new.SessionBottomSheet
import com.example.util.getDeviceId
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import javax.inject.Inject
import javax.inject.Provider

class UserSessionsFragment : BaseFragmentNew<FragmentUserSessionsBinding>(),
    UserSessionsContract.View {


    @InjectPresenter
    lateinit var presenter: UserSessionsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserSessionsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSessionsPresenter = presenterProvider.get().apply {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.deviceId = getDeviceId(requireContext())
    }

    private val currentSessionSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem("Текущий сеанс"))
            setHideWhenEmpty(true)
        }
    }
    private val otherSessionsSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem("Активные сеансы"))
            setHideWhenEmpty(true)
        }
    }
    private val sessionsHistorySection = Section()


    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(currentSessionSection)
            add(otherSessionsSection)
            add(sessionsHistorySection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvSessions.apply {
            adapter = groupAdapter
        }
    }


    override fun setCurrentSession(session: UserSessionModel) {
        currentSessionSection.update(listOf(CurrentSessionItem(session) {
            presenter.killAllSessionsClick()
        }))
    }

    override fun setOtherSessions(sessions: List<UserSessionModel?>) {
        otherSessionsSection.update(
            sessions.map {
                if (it == null) PlaceholderItem(PlaceholderItem.Type.SESSIONS)
                else
                    OtherSessionItem(it) { session ->
                        presenter.showSessionClick(session)
                    }
            }
        )

    }

    override fun showSessionsActionButton(action: SessionsAction) {
        sessionsHistorySection.update(listOf(SessionsHistoryActionItem(action) {
            presenter.showOrHideSessionsHistoryClick(it)
        }))
    }

    override fun showSessionBottomSheetDialog(session: UserSessionModel) {
        SessionBottomSheet(requireContext(), session).setSelectCallback {
            presenter.killUsersDeviceSessionClick(it)
        }
    }

    override fun hideSessionsActionButton() {
        sessionsHistorySection.clear()
    }

    override fun updateOtherSessions(sessions: List<UserSessionModel?>) {
        otherSessionsSection.update(
            sessions.map {
                OtherSessionItem(it) { session ->
                    presenter.showSessionClick(session)
                }
            }
        )
    }

    override fun updateSessionsActionButton(action: SessionsAction) {
        sessionsHistorySection.findItemBy<SessionsHistoryActionItem> { true }?.notifyChanged(action)
    }


    override fun layout(): Int = R.layout.fragment_user_sessions
}