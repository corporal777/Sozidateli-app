package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.EventPassport
import com.example.databinding.ItemEventFormResultBinding
import com.example.databinding.ItemEventFormResultPassportBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultPassportItem (
    val id: String?,
    val title: String?,
    val passport: EventPassport?,
) : BindableItem<ItemEventFormResultPassportBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultPassportBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvPassportSerialNumber.text = passport?.series + " " + passport?.number
            tvPassportCode.text = passport?.issuedBy
            tvPassportDate.text = passport?.issuedDate
            tvPassportOrganization.text = passport?.issuedDepartment
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_passport
}