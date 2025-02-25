package com.example.adapters.event

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemEventNewBinding
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.executePlaceholderLoadState
import com.example.extensions.formatToDefaultDate
import com.example.extensions.isSameDay
import com.example.extensions.parseColor
import com.example.extensions.parseToDate
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.loading.CustomLoadingButton
import com.example.util.getColorStateList
import com.example.util.setImage
import dev.androidbroadcast.vbpd.viewBinding

class EventPagingAdapter(
    val onRegister: (event: EventNew, withAccept: Boolean, position : Int) -> Unit,
    val onCancel: (event: EventNew, position : Int) -> Unit,
    val onShowEvent: (event: EventNew) -> Unit,
    val onShowAuth: (event: EventNew) -> Unit
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
            holder.pos = position
            holder.bind(it)
        }
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.onBindViewHolder(holder, position, payloads)
        else if (payload is Boolean) holder.viewBinding.btnEventAction.showProgressLoading(payload)
    }


    fun updateEventAction(event: EventNew) {
        snapshot().items.find { x -> x.id == event.id }.let { local ->
            if (local != null) {
                local.state?.agreement?.state = event.state?.agreement?.state
                local.setFieldsForActionButton(event)

                val pos = snapshot().items.indexOf(local)
                if (pos != -1) notifyItemChanged(pos)
            }
        }
    }

    fun executeButtonLoading(show: Boolean, pos: Int){
        if (pos != -1) notifyItemChanged(pos, show)
    }


    fun submitData(lifecycle: Lifecycle, data: PagingData<EventNew>, isTemp: Boolean, withAppUpdate: Boolean) {
        isTemporary = isTemp
        isAppUpdate = withAppUpdate
        submitData(lifecycle, data)
    }


    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewBinding by viewBinding(ItemEventNewBinding::bind)
        var pos = -1

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

            if (isTemporary) {
                setButtonText(context.getString(R.string.event_action_participate))
                setOnClickListener { onShowAuth.invoke(event) }
            } else if (event.isStatusActionAvailable() && !registrationClosed) {
                if (actions.contains("register")) {
                    setButtonText(context.getString(R.string.event_action_participate))
                    setOnClickListener { showRegisterAgreement(event) { onRegister.invoke(event, it, pos) } }

                } else if (actions.contains("withdraw")) {
                    setButtonText(context.getString(R.string.event_cancel_request))
                    setOnClickListener { onCancel.invoke(event, pos) }

                } else isVisible = false
            } else isVisible = false

            showProgressLoading(false)
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

            val data = if (status == Event.Status.FINISHED)
                Triple(true, R.color.event_status_finished_background, R.string.event_status_finished)
            else if (status == Event.Status.CANCELED)
                Triple(true, R.color.event_status_cancelled_background, R.string.event_status_cancelled)
            else if (userRegistration == Event.Status.APPROVED)
                Triple(true, R.color.event_status_approved_background, R.string.event_status_approved_new)
            else if (userRegistration == Event.Status.PENDING)
                Triple(true, R.color.event_status_wait_confirmation_background, R.string.event_status_wait_confirmation)
            else if (userRegistration == Event.Status.DECLINED)
                Triple(true, R.color.event_status_declined_background, R.string.event_status_decline_new)
            else if (userRegistration == Event.Status.REGISTRATION_FINISHED)
                Triple(true, R.color.event_status_wait_confirmation_background, R.string.event_closed_request)
            else Triple(false, R.color.event_status_finished_background, R.string.event_status_finished)

            isVisible = data.first
            text = context.getString(data.third)
            backgroundTintList = getColorStateList(data.second)
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

//                if (loadState.refresh is LoadState.Error)
//                    if (this.snapshot().isEmpty()) onEmpty.invoke(true)
//                    else onEmpty.invoke(false)
//                else onEmpty.invoke(false)
                executePlaceholderLoadState(loadState) { onEmpty.invoke(it) }
            }
            return ConcatAdapter(appUpdateAdapter, header, this, footer)
        }
    }
}