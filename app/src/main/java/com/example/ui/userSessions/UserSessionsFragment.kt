package com.example.ui.userSessions

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentUserSessionsBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userSessions.items.CurrentSessionItem
import com.example.ui.userSessions.items.OtherSessionItem
import com.example.ui.userSessions.items.SessionsHeaderItem
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.ui.views.dialogs.SessionBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserSessionsFragment : BaseFragment<FragmentUserSessionsBinding>(),
    UserSessionsContract.View, ToolbarFragment {


    @InjectPresenter
    lateinit var presenter: UserSessionsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserSessionsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSessionsPresenter = presenterProvider.get()


    private val currentSessionSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem(getString(R.string.current_session_label)))
            setPlaceholder(PlaceholderItem(PlaceholderItem.Type.SESSIONS))
        }
    }
    private val otherSessionsSection by lazy {
        Section().apply {
            setHeader(SessionsHeaderItem(getString(R.string.active_sessions_label)))
            setHideWhenEmpty(true)
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(currentSessionSection)
            add(otherSessionsSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvSessions.apply {
            adapter = groupAdapter
            doOnPreDraw { startPostponedEnterTransition() }
        }
    }


    override fun setCurrentSession(session: UserSessionModel, isHasSessions: Boolean) {
        currentSessionSection.updateItem(
            CurrentSessionItem(session, isHasSessions,
                { presenter.onKillSessionsClick() },
                { showSessionDialog(true, it) }
            )
        )
    }

    override fun setOtherSessions(sessions: List<UserSessionModel?>) {
        otherSessionsSection.update(sessions.map {
            OtherSessionItem(it) { session ->
                showSessionDialog(false, session)
            }
        })
    }

    override fun updateKillSessionsButton(isHasSessions: Boolean) {
        currentSessionSection.findItemBy<CurrentSessionItem> { true }?.notifyChanged(isHasSessions)
    }


    private fun showSessionDialog(isCurrent: Boolean, session: UserSessionModel) {
        SessionBottomSheet(requireContext(), isCurrent, session)
            .setOnKillSession {
                if (isCurrent) presenter.onKillSessionsClick()
                else presenter.onKillSessionClick(session.sessionId)
            }.show()
    }

    private fun showSessionInfoDialog() {
        val message = getString(R.string.session_info_message)
        MessageDialogWithBrownButton(requireContext(), message)
    }

    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout(): Int = R.layout.fragment_user_sessions
    override val title: CharSequence by lazy { getString(R.string.sessions_label) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context).apply {
                setImageAsIcon(R.drawable.ic_about_session)
                setOnClickListener { showSessionInfoDialog() }
            })
        }
    }

    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}