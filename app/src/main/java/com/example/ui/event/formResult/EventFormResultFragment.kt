package com.example.ui.event.formResult

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.app.databinding.BottomSheetEventFormResultBinding
import com.example.data.models.Argument
import com.example.data.models.EventNew
import com.example.data.models.eventRegister.EventRegisterField
import com.example.extensions.parcelableArgument
import com.example.holders.PlaceholderItem
import com.example.ui.base.bottomSheet.BaseBSFragment
import com.example.ui.event.formResult.items.EventFormResultFileItem
import com.example.ui.event.formResult.items.EventFormResultPassportItem
import com.example.ui.event.formResult.items.EventFormResultProfileGroup
import com.example.ui.event.formResult.items.EventFormResultStringItem
import com.example.ui.event.formResult.items.EventFormResultTitleItem
import com.xwray.groupie.GroupieAdapter
import dev.androidbroadcast.vbpd.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EventFormResultFragment : BaseBSFragment(), EventFormResultContract.View {

    @InjectPresenter(tag = EVENT_FORM_FRAGMENT_TAG)
    lateinit var presenter: EventFormResultPresenter

    @Inject
    lateinit var presenterProviderEmail: Provider<EventFormResultPresenter>

    @ProvidePresenter(tag = EVENT_FORM_FRAGMENT_TAG)
    fun providePresenter(): EventFormResultPresenter = presenterProviderEmail.get().apply {
        if (args.value != null) event = args.value!!
    }

    private val args by parcelableArgument<Argument<EventNew>>(EVENT_FORM_FRAGMENT_TAG)
    private val viewBinding by viewBinding(BottomSheetEventFormResultBinding::bind)
    private val groupAdapter by lazy {
        GroupieAdapter().apply {
            update(List(3) { PlaceholderItem(PlaceholderItem.Type.REGISTER_FIELD) })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            btnClose.setOnClickListener {
                hideBottomSheetFragment()
            }
        }
    }

    override fun setFormResult(fieldsData: List<EventRegisterField<*>>?) {
        if (fieldsData.isNullOrEmpty()) return
        groupAdapter.update(fieldsData.map {
            if (it.value == null) EventFormResultStringItem(it.field.id, it.field.name, null)
            else
                when (it) {
                    is EventRegisterField.Prefilled ->
                        EventFormResultProfileGroup(requireContext(), it.field, it.value)

                    is EventRegisterField.Title ->
                        EventFormResultTitleItem(it.field.id, it.field.name)

                    is EventRegisterField.Text ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.Phone ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.File ->
                        EventFormResultFileItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.Passport ->
                        EventFormResultPassportItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.SelectBox ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.RadioBox ->
                        EventFormResultStringItem(it.field.id, it.field.name, it.value)

                    is EventRegisterField.Checkbox -> EventFormResultStringItem(it.field.id, it.field.name, getCheckBoxValue(it.value))

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
        const val EVENT_FORM_FRAGMENT_TAG = "event_form_result_dialog"
    }

    override fun layout(): Int = R.layout.bottom_sheet_event_form_result
}