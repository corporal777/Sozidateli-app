package com.example.ui.userSessions

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentUserSessionsBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.userSessions.items.*
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.SessionBottomSheet
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class UserSessionsFragment : BaseFragmentNew<FragmentUserSessionsBinding>(),
    UserSessionsContract.View, SimpleTitleToolbar {


    @InjectPresenter
    lateinit var presenter: UserSessionsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserSessionsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSessionsPresenter = presenterProvider.get().apply {
    }


    private val currentSessionSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem(getString(R.string.current_session_label)))
            setHideWhenEmpty(true)
        }
    }
    private val otherSessionsSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem(getString(R.string.active_sessions_label)))
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
        val userId = getString(R.string.sessions_label)
        val actionIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_about_session)
        setToolbarTitleAndIcon(userId, actionIcon, action = {
            showSessionInfoDialog()
        })

        mBinding.rvSessions.apply {
            adapter = groupAdapter
            onScrolled { _, _ ->
                presenter.changeAppBarElevation(this.computeVerticalScrollOffset())
            }
        }
    }


    override fun setCurrentSession(session: UserSessionModel) {
        currentSessionSection.update(
            listOf(
                CurrentSessionItem(
                    session,
                    { presenter.killAllSessionsClick() },
                    { s ->
                        showSessionDialog(true, { presenter.killAllSessionsClick() }, s)
                    }
                )
            )
        )
    }

    override fun setOtherSessions(sessions: List<UserSessionModel?>) {
        otherSessionsSection.update(
            sessions.map {
                OtherSessionItem(it) { s ->
                    showSessionDialog(
                        false,
                        { presenter.killUsersDeviceSessionClick(s.sessionId.toInt()) },
                        s
                    )
                }
            }
        )

    }

    override fun showSessionsLoadingPlaceholder() {
        currentSessionSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SESSIONS)))
    }


    override fun showSessionsActionButton(action: SessionsAction) {
        sessionsHistorySection.update(listOf(SessionsHistoryActionItem(action) {
            presenter.showOrHideSessionsHistoryClick(it)
        }))
    }


    override fun hideSessionsActionButton() {
        sessionsHistorySection.clear()
    }

    override fun updateSessionsActionButton(action: SessionsAction) {
        sessionsHistorySection.findItemBy<SessionsHistoryActionItem> { true }?.notifyChanged(action)
    }

    private fun showSessionDialog(
        isCurrent: Boolean,
        actionClick: () -> Unit,
        session: UserSessionModel
    ) {
        SessionBottomSheet(isCurrent, actionClick, requireContext(), session)
    }

    private fun showSessionInfoDialog() {
        val message = getString(R.string.session_info_message)
        MessageDialogWithBrownButton(requireContext(), message)
    }

    override fun layout(): Int = R.layout.fragment_user_sessions
}