package com.example.ui.event.formResult.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventFormResultPassportBinding
import com.example.data.models.EventPassport
import com.example.extensions.formatToDefaultDate
import com.xwray.groupie.viewbinding.BindableItem

class EventFormResultPassportItem (
    val id: String?,
    val title: String?,
    val passport: EventPassport?,
) : BindableItem<ItemEventFormResultPassportBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultPassportBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvPassportSerialNumber.text = passport?.series + "  " + passport?.number
            tvPassportCode.text = passport?.issuedDepartment
            tvPassportDate.text = passport?.issuedDate?.formatToDefaultDate()
            tvPassportOrganization.text = passport?.issuedBy
        }
    }

    override fun initializeViewBinding(view: View) = ItemEventFormResultPassportBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_form_result_passport
}