package com.example.ui.event.registration.items

import android.view.View
import android.widget.TextView
import com.example.R
import com.example.databinding.ItemRegisterEventProfileMainBinding
import com.example.holders.registerEvent.BaseRegisterProfileItem

class REProfileContactItem (
    val name: String,
    val field: String?,
    val isAbsent: Boolean,
    val onClick: () -> Unit
) : BaseRegisterProfileItem<ItemRegisterEventProfileMainBinding>(name.hashCode().toString().toLong()) {



    override fun bind(viewBinding: ItemRegisterEventProfileMainBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            btnAction.setOnClickListener { onClick.invoke() }
            prefilledFieldTitle.text = name
            prefilledFieldTextView.apply {
                maxLines = 10
                text = if (isAbsent) when (name) {
                    "Рабочий телефон" -> context.getString(R.string.user_profile_no_work_phone)
                    "Социальные сети" -> context.getString(R.string.user_profile_no_social_networks)
                    else -> context.getString(R.string.user_profile_no_site)
                } else field
            }

            //            append(CustomSpannableString(file.name).apply {
//                setClickSpan(tvFiles){
//                    if (file.isFilePDF())
//                        showFileBrowser(context, file.uri.toString())
//                    else showCustomTabsBrowser(context, file.uri.toString())
//                }
//            })
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is REProfileContactItem) return false
        if (name != other.name) return false
        if (field != other.field) return false
        if (isAbsent != other.isAbsent) return false
        return true
    }

    override fun getErrorFrameView(binding: ItemRegisterEventProfileMainBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventProfileMainBinding): TextView = binding.prefilledFieldTitle
    override fun getLayout(): Int = R.layout.item_register_event_profile_main
}