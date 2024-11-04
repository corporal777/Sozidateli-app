package com.example.ui.event.formResult

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.UserFormResultModel
import com.example.app.databinding.BottomSheetEventFormResultBinding
import com.example.extensions.updateItems
import com.example.holders.PlaceholderItem
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.formResult.items.EventFormResultFileItem
import com.example.ui.event.formResult.items.EventFormResultPassportItem
import com.example.ui.event.formResult.items.EventFormResultProfileGroup
import com.example.ui.event.formResult.items.EventFormResultProfileItem
import com.example.ui.event.formResult.items.EventFormResultStringItem
import com.example.ui.event.formResult.items.EventFormResultTitleItem
import com.example.ui.event.registration.items.RegisterEventProfileItemsGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Provider

class EventFormResultFragment(
    private val form: UserFormResultModel
) : BaseBottomSheetFragment<BottomSheetEventFormResultBinding>(), EventFormResultContract.View {

    @InjectPresenter(tag = EVENT_FORM_FRAGMENT_TAG)
    lateinit var presenter: EventFormResultPresenter

    @Inject
    lateinit var presenterProviderEmail: Provider<EventFormResultPresenter>

    @ProvidePresenter(tag = EVENT_FORM_FRAGMENT_TAG)
    fun providePresenter(): EventFormResultPresenter = presenterProviderEmail.get().apply {
        this.formResult = form
    }


    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            update(List(3) { PlaceholderItem(PlaceholderItem.Type.REGISTER_FIELD) })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            btnClose.setOnClickListener {
                hideBottomSheetDialog()
            }
        }
    }

    override fun setFormResult(fieldsData: List<EventRegisterFieldData<*>>?) {
        if (fieldsData.isNullOrEmpty()) return
        groupAdapter.update(fieldsData.map {
            if (it.value == null) EventFormResultStringItem(it.field.id, it.field.name, null)
            else
                when (it) {
                    is EventRegisterFieldData.Prefilled ->
                        EventFormResultProfileGroup(requireContext(), it.field, it.value)

                    is EventRegisterFieldData.Title ->
                        EventFormResultTitleItem(it.field.id, it.field.name)

                    is EventRegisterFieldData.String ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterFieldData.File ->
                        EventFormResultFileItem(it.field.id, it.field.name, it.value)

                    is EventRegisterFieldData.Passport ->
                        EventFormResultPassportItem(it.field.id, it.field.name, it.value)

                    is EventRegisterFieldData.SelectBox ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterFieldData.RadioBox ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterFieldData.Checkbox ->
                        EventFormResultStringItem(
                            it.field.id,
                            it.field.name,
                            getCheckBoxValue(it.value)
                        )

                    else -> return
                }


        })
    }


    private fun getCheckBoxValue(it: Set<String>?): String {
        val str = StringBuilder()
        it?.forEachIndexed { index, s ->
            if (index == 0) str.append("∙ $s")
            else str.append("\n∙ $s")
        }
        return str.toString()
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, EVENT_FORM_FRAGMENT_TAG)

    companion object {
        const val EVENT_FORM_FRAGMENT_TAG = "event_form_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_event_form_result
}