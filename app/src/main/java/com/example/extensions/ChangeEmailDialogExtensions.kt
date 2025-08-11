package com.example.extensions

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import me.saket.bettermovementmethod.BetterLinkMovementMethod


fun Fragment.showChangeEmailCompleteDialog(email: String) {
    val supportEmail = getString(R.string.support_email).toSpannable()
    Linkify.addLinks(supportEmail, Linkify.EMAIL_ADDRESSES)

    val message = SpannableStringBuilder(getString(R.string.email_change_msg).format(email))
            .append(" ")
            .append(supportEmail)
            .append(".")

    AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
            .apply {
                findViewById<TextView>(android.R.id.message)?.let {
                    it.movementMethod = BetterLinkMovementMethod.getInstance()
                }
            }
}

//fun Fragment.showSearchRegionDialog(onSelected : (region : SearchRegion?) -> Unit){
//    SearchRegionBottomSheet(requireContext())
//        .setRegionSelectedCallback { onSelected.invoke(it) }
//        .show()
//}
//
//fun Fragment.showSearchSettlementDialog(region : String?, onSelected : (region : SearchRegion?) -> Unit){
//    SearchSettlementBottomSheet(requireContext(), region)
//        .setSettlementSelectedCallback {
//            onSelected.invoke(it)
//        }.show()
//}
//
//fun View.showSearchRegionDialog(onSelected : (region : SearchRegion?) -> Unit){
//    SearchRegionBottomSheet(context)
//        .setRegionSelectedCallback { onSelected.invoke(it) }
//        .show()
//}
//
//fun View.showSearchSettlementDialog(region : String?, onSelected : (region : SearchRegion?) -> Unit){
//    SearchSettlementBottomSheet(context, region)
//        .setSettlementSelectedCallback {
//            onSelected.invoke(it)
//        }.show()
//}

fun showEventAgreementDialog(context: Context, event: EventModel, onAccepted: (accept: Boolean) -> Unit) {
    //if (event.userAgreement?.uri.isNullOrEmpty()) onAccepted.invoke(false)
    //else if (event.state?.isAgreementAccepted() == true) onAccepted.invoke(false)
    if (event.state?.agreementState == "accepted") onAccepted.invoke(false)
    else EventAgreementBottomSheet(context, event.userAgreement ?: "")
        .setSelectCallback { if (it) onAccepted.invoke(true) }
        .show()
}

