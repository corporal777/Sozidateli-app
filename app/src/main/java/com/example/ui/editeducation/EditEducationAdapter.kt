package com.example.ui.editeducation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.EducationLevel
import com.example.databinding.*
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.ui.editeducation.EditEducationModel.Companion.ADD_EDUCATION
import com.example.ui.editeducation.EditEducationModel.Companion.ADD_HIGHT_LEVEL
import com.example.ui.editeducation.EditEducationModel.Companion.EDUCATION_ITEM
import com.example.ui.editeducation.EditEducationModel.Companion.EDUCATION_LEVEL
import com.example.ui.editeducation.EditEducationModel.Companion.HIGHT_LEVEL_ITEM
import com.example.ui.editwork.EditWorksModel
import com.example.ui.state.ViewHolder
import com.example.ui.views.educationlist.EducationPopupWindow
import com.example.util.*
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsMonthYearPicker
import initDropDownView
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.*
import kotlinx.android.synthetic.main.item_profile_data_edit_education.*
import java.util.*
import kotlin.collections.ArrayList

class EditEducationAdapter(
    private val addHigthLevelClick: () -> Unit,
    private val addEducationClick: () -> Unit,
    private val hasAcademicDegree: (hasAcademic: Boolean) -> Unit,
    private val onRemoveDegreeClickListener: (position: Int) -> Unit,
    private val isDataValid: () -> Unit,
    private val onRemoveEducationClickListener: (position: Int) -> Unit
) : ListAdapter<EditEducationModel, ViewHolder<*>>(EditEducationDiffCallback()) {

    private lateinit var popupReceiving: EducationPopupWindow

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        return when (viewType) {
            EDUCATION_LEVEL -> ViewHolder(
                ItemEducationLevelBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
            HIGHT_LEVEL_ITEM -> ViewHolder(
                ItemHightEducationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
            ADD_HIGHT_LEVEL -> ViewHolder(
                ItemProfileButtonEditNewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
            EDUCATION_ITEM -> ViewHolder(
                ItemEditEducationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
            else -> ViewHolder(
                ItemProfileButtonEditNewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        when (getItemViewType(position)) {
            EDUCATION_LEVEL -> {
                val holderEdL = holder as ViewHolder<ItemEducationLevelBinding>
                setupDropDown(holderEdL.binding.tvEducationLevel,
                    holderEdL.binding.scAcadDegry,
                    getItem(position).availableEducations?.map { it.name } ?: emptyList(),
                    getItem(position).selectedDegree,
                    getItem(position)
                ) {
                    getItem(position).selectedDegree = it
                }
                holderEdL.binding.scEducation.initSwitch(
                    getItem(position).educationLevel?.showInProfile ?: false
                ) { getItem(position).educationLevel?.showInProfile = it }
                holderEdL.binding.scAcadDegry.isChecked = getItem(position).hasAcademicDegreee
                holderEdL.binding.scAcadDegry.setOnCheckedChangeListener { _, b ->
                    getItem(position).hasAcademicDegreee = b
                    hasAcademicDegree(b)
                }
                holderEdL.binding.executePendingBindings()
            }
            HIGHT_LEVEL_ITEM -> {
                val holderHightL = holder as ViewHolder<ItemHightEducationBinding>
                setupDegreeDropDown(
                    holderHightL.binding.tvDegreesLevel,
                    holderHightL.binding.tilDegreesLevel,
                    getItem(holder.adapterPosition).availableDegrees ?: emptyList(),
                    getItem(holder.adapterPosition).availableDegrees?.find { it.id == getItem(holder.adapterPosition).academicDegrees?.degree }?.name
                ) {
                    //mDegreesLevel = it?.name
                    getItem(holder.adapterPosition).academicDegrees?.degree = it?.id
                }

                val sciencesList = arrayListOf<EducationLevelNew>().apply {
                    addAll(getItem(holder.adapterPosition).availableSciences?: emptyList())
                }
                if (!sciencesList.isNullOrEmpty()) {
                    sciencesList.add(0, EducationLevelNew(777, "Не выбрано", 777))
                }

                setupDegreeDropDown(
                    holderHightL.binding.tvSciencesLevel,
                    holderHightL.binding.tilSciencesLevel,
                    //getItem(holder.adapterPosition).availableSciences ?: emptyList(),
                    sciencesList,
                    getItem(holder.adapterPosition).availableSciences?.find {
                        it.id == getItem(holder.adapterPosition).academicDegrees?.speciality
                    }?.name ?: sciencesList[0].name
                ) {

                    if (it?.id == 777 && it.name == "Не выбрано" && it.order == 777) {
                        getItem(holder.adapterPosition).academicDegrees?.speciality = null
                    } else getItem(holder.adapterPosition).academicDegrees?.speciality = it?.id
                }

                holderHightL.binding.btnRemove.setOnClickListener {
                    onRemoveDegreeClickListener(
                        holder.adapterPosition
                    )
                }

                holderHightL.binding.scEducation.initSwitch(
                    getItem(holder.adapterPosition).academicDegrees?.showInProfile ?: false
                ) { getItem(holder.adapterPosition).academicDegrees?.showInProfile = it }

                if (getItem(holder.adapterPosition).isDeleteVisible)
                    holderHightL.binding.btnRemove.visibility = View.VISIBLE
                else
                    holderHightL.binding.btnRemove.visibility = View.GONE
                holderHightL.binding.executePendingBindings()
            }
            ADD_HIGHT_LEVEL -> {
                val holderAddHL = holder as ViewHolder<ItemProfileButtonEditNewBinding>
                holderAddHL.binding.btnEdit.apply {
                    text = context.resources.getString(R.string.profile_sciences_add)
                    setOnClickListener {
                        addHigthLevelClick()
                    }
                }
                holderAddHL.binding.executePendingBindings()
            }
            EDUCATION_ITEM -> {
                val holderEduc = holder as ViewHolder<ItemEditEducationBinding>
                val now = Date()
                var isDateCheckboxWasSet = false
                val startDate = getItem(holder.adapterPosition).education?.begin?.parseToDate(
                    defaultServerDateFormatter
                )
                holderEduc.binding.etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
                holderEduc.binding.tilStart.initAsMonthYearPicker(
                    startDate,
                    minDate = getItem(holder.adapterPosition).birthday,
                    maxDate = now
                ) { year, month, day ->
                    holderEduc.binding.tilStart.error = null
                    getItem(holder.adapterPosition).education?.begin =
                        formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                    isDateValid(holderEduc.binding, getItem(holder.adapterPosition))
                    isDataValidd(getItem(holder.adapterPosition))
                    //validateEnableButton()
                    profileDateFormat(year, month, day)
                }

                val finishDate = getItem(holder.adapterPosition).education?.end?.parseToDate(
                    defaultServerDateFormatter
                )
                holderEduc.binding.etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
                holderEduc.binding.tilFinish.initAsMonthYearPicker(
                    finishDate,
                    minDate = getItem(holder.adapterPosition).birthday,
                    maxDate = now
                ) { year, month, day ->
                    holderEduc.binding.tilFinish.error = null
                    getItem(holder.adapterPosition).education?.end =
                        formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                    isDateValid(holderEduc.binding, getItem(holder.adapterPosition))
                    isDataValidd(getItem(holder.adapterPosition))
                    //validateEnableButton()
                    profileDateFormat(year, month, day)
                }

                setFinishEnabled(holderEduc.binding, !getItem(position).isNotFinishedSelected)
                if (!isDateCheckboxWasSet) {
                    holderEduc.binding.scFinish.initSwitch(getItem(position).isNotFinishedSelected) {
                        holderEduc.binding.tilFinish.error = null
                        getItem(holder.adapterPosition).education?.end = null
                        getItem(holder.adapterPosition).isNotFinishedSelected = it
                        holderEduc.binding.etFinish.text = null
                        setFinishEnabled(holderEduc.binding, !it)
                        isDataValidd(getItem(holder.adapterPosition))
                        //validateEnableButton()
                    }
                    isDateCheckboxWasSet = true
                }

                holderEduc.binding.etInstitution.initInput(getItem(holder.adapterPosition).education?.organization) {
                    holderEduc.binding.tilInstitution.error = null
                    getItem(holder.adapterPosition).education?.organization = it.toString()
                    isDataValidd(getItem(holder.adapterPosition))
                    //validateEnableButton()
                }
                holderEduc.binding.etSpeciality.initInput(getItem(holder.adapterPosition).education?.speciality) {
                    holderEduc.binding.tilSpeciality.error = null
                    if (it.toString().isNullOrEmpty()) {
                        getItem(holder.adapterPosition).education?.speciality = ""
                    } else {
                        getItem(holder.adapterPosition).education?.speciality = it.toString()
                    }

                    isDataValidd(getItem(holder.adapterPosition))
                    //validateEnableButton()
                }
                holderEduc.binding.scEducation.initSwitch(
                    getItem(holder.adapterPosition).education?.showInProfile ?: false
                ) {
                    getItem(holder.adapterPosition).education?.showInProfile = it
                }
                holderEduc.binding.btnRemove.setOnClickListener {
                    onRemoveEducationClickListener(
                        holder.adapterPosition
                    )
                }

                if (getItem(holder.adapterPosition).isDeleteVisible)
                    holderEduc.binding.btnRemove.visibility = View.VISIBLE
                else
                    holderEduc.binding.btnRemove.visibility = View.GONE

                showErrors(
                    getItem(holder.adapterPosition).showErrors,
                    holderEduc.binding,
                    getItem(holder.adapterPosition)
                )
                holderEduc.binding.executePendingBindings()
            }
            ADD_EDUCATION -> {
                val holderAddEduc = holder as ViewHolder<ItemProfileButtonEditNewBinding>
                holderAddEduc.binding.btnEdit.apply {
                    text = context.resources.getString(R.string.profile_institution_add)
                    setOnClickListener {
                        addEducationClick()
                    }
                }
                holderAddEduc.binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    private fun showErrors(
        isShow: Boolean,
        holder: ItemEditEducationBinding,
        item: EditEducationModel
    ) {
        if (isShow) {
            if (!isStartValid(item.education?.begin)) holder.tilStart.apply {
                error = resources.getString(R.string.required_field)
            }
            if (!isFinishValid(
                    item.education?.begin,
                    item.education?.end,
                    item.isNotFinishedSelected
                )
            ) holder.tilFinish.apply {
                error = resources.getString(R.string.profile_education_finish_error)
            }
            if (!isOrganizationValid(item.education?.organization)) holder.tilInstitution.apply {
                error = if ((item.education?.organization?.length
                        ?: 0) < 4 && (item.education?.organization?.length ?: 0) > 0
                )
                    resources.getString(R.string.ten_letters_error)
                else
                    resources.getString(R.string.profile_educate_institution_empty_error)
            }

            val model = this.currentList[0]
            if (model.selectedDegree?.contains("классов") == false) {
                if (!isSpecialityValid(item.education?.speciality)) holder.tilSpeciality.apply {
                    error = if ((item.education?.speciality?.length ?: 0) < 4 && (item.education?.speciality?.length ?: 0) > 0)
                        resources.getString(R.string.five_letters_error)
                    else
                        resources.getString(R.string.enter_specialty)
                }
            }
        }
    }

    private fun isDataValidd(item: EditEducationModel) {
        var isValid = true
        if (!isStartValid(item.education?.begin)) {
            isValid = false
        }
        if (!isFinishValid(
                item.education?.begin,
                item.education?.end,
                item.isNotFinishedSelected
            )
        ) {
            isValid = false
        }
        if (!isOrganizationValid(item.education?.organization)) {
            isValid = false
        }

        val model = this.currentList[0]
        if (model.selectedDegree?.contains("классов") == false) {
            if (!isSpecialityValid(item.education?.speciality)) {
                isValid = false
            }
        }
        item.isDataValid = isValid
        validateEnableButton()
    }

    private fun validateEnableButton() {
        isDataValid()
    }

    private fun isDateValid(viewHolder: ItemEditEducationBinding, item: EditEducationModel) {
        if (!item.education?.begin.isNullOrBlank() && !item.education?.end.isNullOrBlank()) {
            if (validateEndDate(item.education?.begin ?: "", item.education?.end ?: "")) {
                viewHolder.tilFinish.error =
                    viewHolder.tilFinish.context.getString(R.string.user_education_end_date_error)
            } else {
                viewHolder.tilFinish.error = null
            }
        }
    }

    private fun setFinishEnabled(viewHolder: ItemEditEducationBinding, enabled: Boolean) {
        viewHolder.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
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
                else startDate.timeInMillis < finishDate.timeInMillis || finishDate.isSameMonth(
                    startDate
                )
            }
        }
    }

    private fun isOrganizationValid(mInstitution: String?): Boolean =
        (mInstitution?.length ?: 0) >= 4

    private fun isSpecialityValid(mSpeciality: String?): Boolean =
        (mSpeciality?.length ?: 0) >= 4


    fun isDataValid(item: EditEducationModel) = isStartValid(item.education?.begin) &&
            isFinishValid(item.education?.begin, item.education?.end, item.isNotFinishedSelected) &&
            isOrganizationValid(item.education?.organization) && isSpecialityValid(item.education?.speciality)

    private fun setupDegreeDropDown(
        textView: AutoCompleteTextView,
        textInputLayout: TextInputLayout,
        variants: List<EducationLevelNew>,
        initialVariant: String?,
        onSelect: (EducationLevelNew?) -> Unit
    ) {
        textView.apply {
            setAdapter(
                NoFilterArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    variants.map { item -> item.name }.toTypedArray()
                )
            )
            setText(initialVariant)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                textInputLayout.error = null
                onSelect(variants.getOrNull(position))
            }
        }
    }

    private fun setupDropDown(
        textView: AppCompatTextView,
        cb: AppCompatCheckBox,
        variants: List<String>,
        initialVariant: String?,
        item: EditEducationModel,
        onSelect: (String?) -> Unit
    ) {
        textView.apply {
            popupReceiving = EducationPopupWindow(context, variants.toList())
            text = initialVariant ?: context.getString(R.string.search_filters_not_chosen)
            validateCheckbox(initialVariant, cb)
            setOnClickListener {
                if (!variants.isNullOrEmpty()){
                    popupReceiving.setCurrentSelection(textView.text.toString())
                    popupReceiving.showPopup(this)
                }
            }
            popupReceiving.setEducationClickCallback {
                if (it != text) {
                    text = it
                    validateCheckbox(it, cb)
                    if (isTrigger(it)) {
                        hasAcademicDegree(item.hasAcademicDegreee)
                    } else {
                        hasAcademicDegree(false)
                    }
                    onSelect(it)
                }
                popupReceiving.hidePopup()
            }
            popupReceiving.getOldEducationCallback {
                if (it.isAgree) {
                    popupReceiving.updateSelection(it.newEducation)
                } else {
                    popupReceiving.updateSelection(textView.text.toString())
                }
            }
        }
    }

    private fun validateCheckbox(text: String?, cb: AppCompatCheckBox) {
        cb.apply {
            visibility = if (isTrigger(text))
                View.VISIBLE
            else
                View.GONE
        }
    }

    private fun isTrigger(text: String?) = text == "Более одного высшего" || text == "Высшее"
}

class EditEducationDiffCallback : DiffUtil.ItemCallback<EditEducationModel>() {

    override fun areItemsTheSame(
        oldItem: EditEducationModel,
        newItem: EditEducationModel
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: EditEducationModel,
        newItem: EditEducationModel
    ): Boolean =
        oldItem == newItem
}