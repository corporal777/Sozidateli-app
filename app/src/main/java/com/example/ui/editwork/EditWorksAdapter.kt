package com.example.ui.editwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.R
import com.example.databinding.ItemProfileButtonEditNewBinding
import com.example.databinding.ItemProfileDataEditNoWorkNewBinding
import com.example.databinding.ItemProfileDataEditWorkNewBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.ui.editwork.EditWorksModel.Companion.ADD_WORK
import com.example.ui.editwork.EditWorksModel.Companion.HAS_WORK
import com.example.ui.editwork.EditWorksModel.Companion.WORK_ITEM
import com.example.ui.state.ViewHolder
import com.example.util.*
import initAsMonthYearPicker
import kotlinx.android.synthetic.main.item_profile_data_edit_work.*
import java.util.*

class EditWorksAdapter(
        private val noWorkListener:(hasWork: Boolean, holder: ItemProfileDataEditNoWorkNewBinding) -> Unit,
        private val onRemoveClickListener: (position: Int) -> Unit,
        private val addMoreClick: () -> Unit,
        private val isDataValid: () -> Unit
) : ListAdapter<EditWorksModel, ViewHolder<*>>(EditWorksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        return when (viewType) {
            HAS_WORK -> ViewHolder(
                    ItemProfileDataEditNoWorkNewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false))
            ADD_WORK -> ViewHolder(
                    ItemProfileButtonEditNewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false))
            else -> ViewHolder(
                    ItemProfileDataEditWorkNewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        when (getItemViewType(position)) {
            HAS_WORK -> {
                val holderHas = holder as ViewHolder<ItemProfileDataEditNoWorkNewBinding>
                holderHas.binding.scNoExperience.initSwitch(getItem(holder.adapterPosition).hasWork) {
                    getItem(holder.adapterPosition).hasWork = it
                    noWorkListener(it, holderHas.binding)
                }
                holderHas.binding.executePendingBindings()
            }
            ADD_WORK -> {
                val holderAdd = holder as ViewHolder<ItemProfileButtonEditNewBinding>
                holderAdd.binding.btnEdit.setOnClickListener {
                    addMoreClick()
                }
                holderAdd.binding.btnEdit.isEnabled = !getItem(holder.adapterPosition).hasWork
                holderAdd.binding.executePendingBindings()
            }
            WORK_ITEM -> {
                val holderItem = holder as ViewHolder<ItemProfileDataEditWorkNewBinding>
                val birthday = getItem(holder.adapterPosition).birthday?.parseToDate(defaultServerDateFormatter)
                val now = Date()

                isEnabledItems(holderItem.binding, getItem(holder.adapterPosition).hasWork)
                holderItem.binding.apply {
                    val startDate = getItem(holder.adapterPosition).works?.begin?.parseToDate(defaultServerDateFormatter)
                    etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
                    tilStart.initAsMonthYearPicker(startDate, minDate = birthday, maxDate = now) { year, month, day ->
                        tilStart.error = null
                        getItem(holder.adapterPosition).works?.begin = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                        isDateValid(holderItem.binding, getItem(holder.adapterPosition))
                        //validateEnableButton()
                        isDataValidd(getItem(holder.adapterPosition), !getItem(holder.adapterPosition).isNotFinishedSelected)
                        profileDateFormat(year, month, day)
                    }

                    val finishDate = getItem(holder.adapterPosition).works?.end?.parseToDate(defaultServerDateFormatter)
                    etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
                    tilFinish.initAsMonthYearPicker(finishDate, minDate = birthday, maxDate = now) { year, month, day ->
                        tilFinish.error = null
                        getItem(holder.adapterPosition).works?.end = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                        isDateValid(holderItem.binding, getItem(holder.adapterPosition))
                        //validateEnableButton()
                        isDataValidd(getItem(holder.adapterPosition), !getItem(holder.adapterPosition).isNotFinishedSelected)
                        profileDateFormat(year, month, day)
                    }

                    setFinishEnabled(this, !getItem(holder.adapterPosition).isNotFinishedSelected/*getItem(holder.adapterPosition).works?.end != null*/)
                    scFinish.initSwitch(/*getItem(holder.adapterPosition).works?.end == null*/getItem(holder.adapterPosition).isNotFinishedSelected) {
                        tilFinish.error = null
                        getItem(holder.adapterPosition).works?.end = null
                        getItem(holder.adapterPosition).isNotFinishedSelected = it
                        etFinish.text = null
                        setFinishEnabled(this, !it)
                        //validateEnableButton()
                        isDataValidd(getItem(holder.adapterPosition), !getItem(holder.adapterPosition).isNotFinishedSelected)
                    }
                    etProject.initInput(getItem(holder.adapterPosition).works?.organization) {
                        tilProject.error = null
                        getItem(holder.adapterPosition).works?.organization = it.toString()
                        //validateEnableButton()
                        isDataValidd(getItem(holder.adapterPosition), !getItem(holder.adapterPosition).isNotFinishedSelected)
                    }
                    etPosition.initInput(getItem(holder.adapterPosition).works?.position) {
                        tilPosition.error = null
                        getItem(holder.adapterPosition).works?.position = it.toString()
                        //validateEnableButton()
                        isDataValidd(getItem(holder.adapterPosition), !getItem(holder.adapterPosition).isNotFinishedSelected)
                    }

                    scWork.apply {
                        isVisible = getItem(holder.adapterPosition).showInProfileButton
                        initSwitch(getItem(holder.adapterPosition).works?.showInProfile?: false) { getItem(holder.adapterPosition).works?.showInProfile = it }
                    }

                    btnRemove.setOnClickListener { onRemoveClickListener(holder.adapterPosition) }

                    if (getItem(position).isDeleteVisible)
                        btnRemove.visibility = View.VISIBLE
                    else
                        btnRemove.visibility = View.GONE

                    showErrors(getItem(holder.adapterPosition).showErrors, holderItem.binding, getItem(holder.adapterPosition), getItem(holder.adapterPosition).isNotFinishedSelected)
                }
                holderItem.binding.executePendingBindings()
            }
        }
    }

    private fun showErrors(isShow: Boolean, holder: ItemProfileDataEditWorkNewBinding, item: EditWorksModel, isNotFinished: Boolean) {
        if (isShow) {
            holder.apply {
                if (!isStartValid(item.works?.begin)) tilStart.apply {
                    error = resources.getString(R.string.required_field)
                }

                if (!isNotFinished) {
                    val start = item.works?.begin
                    val finish = item.works?.end
                    if (start == null || finish == null) {
                        tilFinish.apply {
                            error = resources.getString(R.string.profile_work_finish_error)
                        }
                    } else {
                        val finishDate = finish.parseToDate(defaultServerDateFormatter)?.calendar()
                        val startDate = start.parseToDate(defaultServerDateFormatter)?.calendar()
                        if (finishDate == null || startDate == null) {
                            tilFinish.apply {
                                error = resources.getString(R.string.profile_work_finish_error)
                            }
                        } else {
                            if (startDate.timeInMillis > finishDate.timeInMillis) {
                                tilFinish.apply {
                                    error = resources.getString(R.string.profile_work_finish_error)
                                }
                            }
                            if (finishDate.isSameMonth(startDate)) {
                                tilFinish.apply {
                                    error = resources.getString(R.string.profile_work_finish_error)
                                }
                            }
                        }
                    }
                }
                /*if (!isFinishValid(item.works?.begin, item.works?.end, isNotFinished)) tilFinish.apply {
                    error = resources.getString(R.string.profile_work_finish_error)
                }*/
                if ((item.works?.organization?.length?: 0) < 9) tilProject.apply {
                    error = if ((item.works?.organization?.length ?: 0) < 9 && (item.works?.organization?.length
                                    ?: 0) > 0)
                        resources.getString(R.string.ten_letters_error)
                    else
                        resources.getString(R.string.enter_organization)
                }
                if ((item.works?.position?.length?: 0) < 4) tilPosition.apply {
                    error = if ((item.works?.position?.length ?: 0) < 4 && (item.works?.position?.length ?: 0) > 0)
                        resources.getString(R.string.five_letters_error)
                    else
                        resources.getString(R.string.enter_position)
                }
            }
            item.showErrors = false
        }
    }

    private fun validateEnableButton() {
        isDataValid()
    }

    private fun isDataValidd(item: EditWorksModel, isNotFinished: Boolean) {
        var isValid = true
        if((item.works?.organization?.length?: 0) < 9) {
            isValid = false
        }

        if((item.works?.position?.length?: 0) < 4) {
            isValid = false
        }

        if(item.works?.begin == null/*isStartValid(item.works?.begin)*/) {
            isValid = false
        }

        //if (isFinishValid(item.works?.begin, item.works?.end))
        if (/*item.works?.end == null*/isNotFinished) {
            val start = item.works?.begin
            val finish = item.works?.end
            if (start == null || finish == null) { isValid = false }
            else {
                val finishDate = finish.parseToDate(defaultServerDateFormatter)?.calendar()
                val startDate = start.parseToDate(defaultServerDateFormatter)?.calendar()
                if (finishDate == null || startDate == null) isValid = false
                else {
                    if (startDate.timeInMillis > finishDate.timeInMillis) {
                        isValid = false
                    }
                    if (finishDate.isSameMonth(startDate)) {
                        isValid = false
                    }
                }
            }
        }
        item.isDataValid = isValid
        validateEnableButton()
    }

    private fun isStartValid(mStart: String?) = mStart != null
    private fun isFinishValid(mStart: String?, mFinish: String?, isNotFinished: Boolean): Boolean {
        return if (isNotFinished) {
            true
        } else {
            val start = mStart
            val finish = mFinish
            if (start == null || finish == null) false
            else {
                val finishDate = finish.parseToDate(defaultServerDateFormatter)?.calendar()
                val startDate = start.parseToDate(defaultServerDateFormatter)?.calendar()
                if (finishDate == null || startDate == null) false
                else startDate.timeInMillis < finishDate.timeInMillis || finishDate.isSameMonth(startDate)
            }
        }
    }

    private fun setFinishEnabled(viewHolder: ItemProfileDataEditWorkNewBinding, enabled: Boolean) {
        viewHolder.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
    }

    private fun isDateValid(viewHolder: ItemProfileDataEditWorkNewBinding, item: EditWorksModel) {
        if (!item.works?.begin.isNullOrBlank() && !item.works?.end.isNullOrBlank()) {
            if (validateEndDate(item.works?.begin?: "", item.works?.end?: "")) {
                viewHolder.tilFinish.error = viewHolder.tilFinish.context.getString(R.string.user_education_end_date_error)
            } else {
                viewHolder.tilFinish.error = null
            }
        }
    }

    private fun isEnabledItems(viewHolder: ItemProfileDataEditWorkNewBinding, isEditable: Boolean) {
        viewHolder.apply {
            etProject.isEnabled = !isEditable
            etPosition.isEnabled = !isEditable
            tilStart.isEnabled = !isEditable
            tilFinish.isEnabled = !isEditable
            scFinish.isEnabled = !isEditable
            btnRemove.isEnabled = !isEditable
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }
}

class EditWorksDiffCallback: DiffUtil.ItemCallback<EditWorksModel>() {

    override fun areItemsTheSame(oldItem: EditWorksModel, newItem: EditWorksModel): Boolean =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EditWorksModel, newItem: EditWorksModel): Boolean =
            oldItem == newItem
}