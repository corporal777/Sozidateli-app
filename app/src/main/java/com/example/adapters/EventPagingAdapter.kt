package com.example.adapters

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.ItemEventNewBinding
import com.example.app.databinding.ItemUserBinding
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventRegistrationStateModel
import com.example.data.models.UserDetail
import com.example.exceptions.EmptyDataException
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.dp
import com.example.extensions.formatToDefaultDate
import com.example.extensions.isSameDay
import com.example.extensions.parseColor
import com.example.extensions.parseToDate
import com.example.extensions.setOnClickListener
import com.example.holders.redesign.EventListItem.OnEventClickListener
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.setCircleAvatar
import com.example.util.setImage
import com.example.util.weak

class EventPagingAdapter(
    val onRegister: (event: EventNew, withAccept : Boolean) -> Unit,
    val onCancel: (event: EventNew) -> Unit,
    val onShowEvent: (event: EventNew) -> Unit,
    val onShowAuth: (event: EventNew) -> Unit,
    val onShowState: () -> Unit,
) : PagingDataAdapter<EventNew, EventPagingAdapter.EventViewHolder>(AsyncDiffCallback) {

    private var isTemporary = false
    private var isAppUpdate = false
    private val appUpdateAdapter = AppUpdateAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EventViewHolder(layoutInflater.inflate(R.layout.item_event_new, parent, false))
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun updateEventAction(event: EventNew) {
        snapshot().items.find { x -> x.id == event.id }.let { local ->
            if (local != null) {
                local.state?.agreement?.state = event.state?.agreement?.state
                local.binds?.currentUserRegistration = event.binds?.currentUserRegistration
                local.binds?.currentUserRegistrationState = event.binds?.currentUserRegistrationState

                val position = snapshot().items.indexOf(local)
                notifyItemChanged(position)
            }
        }
    }

    fun submitData(
        lifecycle: Lifecycle,
        data: PagingData<EventNew>,
        isTemp: Boolean,
        withAppUpdate: Boolean
    ) {
        isTemporary = isTemp
        isAppUpdate = withAppUpdate
        submitData(lifecycle, data)
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemEventNewBinding::bind)

        fun bind(event: EventNew) {
            with(viewBinding) {
                itemContainer.setOnClickListener { onShowEvent.invoke(event) }
                tvDate.text = getEventDate(event.holdingDate?.from, event.holdingDate?.to)
                tvLocation.text = event.address?.getShortAddress()
                tvTitle.text = event.name
                ivLogo.apply {
                    if (event.image?.uri.isNullOrEmpty()) {
                        val bgColor = event.backgroundColor?.value.parseColor() ?: Color.DKGRAY
                        setImage(ColorDrawable(bgColor), 300)
                    } else setImage(event.image?.uri, 300)

                    colorFilter = if (event.status?.value != Event.Status.CANCELED) null
                    else ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

                    clipToOutline = true
                }
                tvEventState.setApproveStatus(event)
                btnEventAction.setActionStatus(event, isTemporary)

            }
        }

        private fun CustomLoadingButton.setActionStatus(event: EventNew, isTemporary: Boolean) {
            val registrationState = event.binds?.currentUserRegistrationState
            val registrationClosed = registrationState?.prohibitions?.registrationClosed ?: false
            val actions = registrationState?.availableActions ?: arrayListOf("")

            var clickAction: (() -> Unit)? = null
            var buttonText = ""

            if (isTemporary) {
                buttonText = context.getString(R.string.event_action_participate)
                clickAction = { onShowAuth.invoke(event) }
            } else if (event.isStatusActionAvailable() && !registrationClosed) {
                if (actions.contains("register")) {
                    buttonText = context.getString(R.string.event_action_participate)
                    clickAction = {
                        registrationState.checkStateLevel {
                            showRegisterAgreement(event){
                                showProgressLoading(true)
                                onRegister.invoke(event, it)
                            }
                        }
                    }
                } else if (actions.contains("withdraw")) {
                    buttonText = context.getString(R.string.event_action_cancel_request)
                    clickAction = {
                        registrationState.checkStateLevel {
                            showProgressLoading(true)
                            onCancel.invoke(event)
                        }
                    }
                } else clickAction = null
            } else clickAction = null


            showProgressLoading(false)
            setButtonText(buttonText)
            isVisible = clickAction != null
            setOnClickListener { clickAction?.invoke() }
        }

        private fun EventRegistrationStateModel?.checkStateLevel(hasLevel: () -> Unit) {
            if (this?.prohibitions?.profileLevelToLow?.value == false) hasLevel()
            else onShowState.invoke()
        }

        private fun showRegisterAgreement(event: EventNew, onAccepted: (accept: Boolean) -> Unit) {
            if (event.userAgreement?.uri.isNullOrEmpty()) onAccepted.invoke(false)
            else if (event.state?.isAgreementAccepted() == true) onAccepted.invoke(false)
            else EventAgreementBottomSheet(itemView.context, event.userAgreement?.uri!!)
                .setSelectCallback { if (it) onAccepted.invoke(true) }
                .show()
        }

        private fun TextView.setApproveStatus(event: EventNew) {
            val status = event.status?.value
            val userRegistration = event.binds?.currentUserRegistration?.status?.value
            var statusBackground = R.color.event_status_finished_background
            var statusText = R.string.event_status_finished

            val statusVisibility: Boolean
            when (status) {
                Event.Status.FINISHED -> {
                    statusVisibility = true
                    statusBackground = R.color.event_status_finished_background
                    statusText = R.string.event_status_finished
                }

                Event.Status.CANCELED -> {
                    statusVisibility = true
                    statusBackground = R.color.event_status_cancelled_background
                    statusText = R.string.event_status_cancelled
                }

                else -> {
                    when (userRegistration) {
                        Event.Status.APPROVED -> {
                            statusVisibility = true
                            statusBackground = R.color.event_status_approved_background
                            statusText = R.string.event_status_approved_new
                        }

                        Event.Status.PENDING -> {
                            statusVisibility = true
                            statusBackground = R.color.event_status_wait_confirmation_background
                            statusText = R.string.event_status_wait_confirmation
                        }

                        Event.Status.DECLINED -> {
                            statusVisibility = true
                            statusBackground = R.color.event_status_declined_background
                            statusText = R.string.event_status_decline_new
                        }

                        Event.Status.REGISTRATION_FINISHED -> {
                            statusVisibility = true
                            statusBackground = R.color.event_status_wait_confirmation_background
                            statusText = R.string.event_action_closed_request
                        }

                        else -> statusVisibility = false
                    }
                }
            }
            text = context.getString(statusText)
            backgroundTintList = ContextCompat.getColorStateList(context, statusBackground)
            isVisible = statusVisibility
        }

        private fun getEventDate(from: String?, to: String?): String? {
            val dateStart = from ?: return null
            val dateEnd = to ?: return null

            val startDate =
                dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null
            val finishDate =
                dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null

            return if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate()
            else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
        }
    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<EventNew>() {
        override fun areItemsTheSame(oldItem: EventNew, newItem: EventNew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventNew, newItem: EventNew): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        fun EventPagingAdapter.withLoadStateAdapters(
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addOnPagesUpdatedListener {}
            addLoadStateListener { loadState ->
                //refresh.loadState = if (isRefresh) refresh.notRefresh else loadState.refresh
                //refresh.loadState = loadState.refresh
                header.loadState = if (itemCount > 0) header.notRefresh else loadState.refresh
                footer.loadState = loadState.append

                appUpdateAdapter.loadState = loadState.refresh
                appUpdateAdapter.isNeedShowUpdate = isAppUpdate

                if (loadState.refresh is LoadState.Error)
                    if (this.snapshot().isEmpty()) onEmpty.invoke(true)
                    else onEmpty.invoke(false)
                else onEmpty.invoke(false)
            }
            return ConcatAdapter(appUpdateAdapter, header, this, footer)
        }
    }
}