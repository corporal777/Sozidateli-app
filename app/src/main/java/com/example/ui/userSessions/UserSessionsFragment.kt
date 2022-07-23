package com.example.ui.userSessions

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentUserSessionsBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.userSessions.items.*
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
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

    private val currentSessionSection = Section()
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
        mBinding.rvSessions.apply {
            startPostponedEnterTransition()
            adapter = groupAdapter
        }
        mBinding.ivInfo.setOnClickListener {
            showSessionInfoDialog()
        }
    }


    override fun setCurrentSession(session: UserSessionModel) {
        currentSessionSection.update(
            listOf(
                SessionsHeaderItem(getString(R.string.current_session_label)),
                CurrentSessionItem(
                    session,
                    { presenter.killAllSessionsClick() },
                    { s -> showSessionDialog({ presenter.killAllSessionsClick() }, s) }
                )
            )
        )
    }

    override fun setOtherSessions(sessions: List<UserSessionModel?>) {
        otherSessionsSection.update(
            sessions.map {
                OtherSessionItem(it) { s ->
                    showSessionDialog(
                        { presenter.killUsersDeviceSessionClick(s.sessionId) },
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

    override fun showSessionBottomSheetDialog(session: UserSessionModel) {
//        SessionBottomSheet(actionClick, requireContext(), session).onKillDeviceSession {
//            if (getDeviceId(requireContext()) == it.deviceId) {
//                presenter.killAllSessionsClick()
//            } else {
//                presenter.killUsersDeviceSessionClick(it.sessionId)
//            }
//        }
    }

    override fun hideSessionsActionButton() {
        sessionsHistorySection.clear()
    }

    override fun updateSessionsActionButton(action: SessionsAction) {
        sessionsHistorySection.findItemBy<SessionsHistoryActionItem> { true }?.notifyChanged(action)
    }

    private fun showSessionDialog(actionClick: () -> Unit, session: UserSessionModel) {
        SessionBottomSheet(actionClick, requireContext(), session)
    }

    private fun showSessionInfoDialog(){
        val message = getString(R.string.session_info_message)
        MessageDialogWithGreenButton(requireContext(), message)
    }

    override fun layout(): Int = R.layout.fragment_user_sessions
}