package com.example.ui.views.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.EXTRA_EMAIL
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.createMapInfo
import com.example.app.databinding.BottomSheetEventDetailInformationBinding
import com.example.extensions.markWon
import com.example.extensions.parsePhone
import com.example.ui.event.location.map.MapFragment
import com.example.ui.main.MainActivity
import com.example.ui.page.PageFragment
import com.example.ui.views.CustomSpannableString
import com.example.util.URLSpanNoUnderline
import com.example.util.getColor
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class EventDetailInformationBottomSheetDialog(
    private val activity : Context,
    private val event: EventNew
) : BottomSheetDialog(activity) {

    private val mBinding = BottomSheetEventDetailInformationBinding.inflate(LayoutInflater.from(activity))

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(true)

        mBinding.apply {
            btnClose.setOnClickListener { dismiss() }
            setData()
        }
    }

    private fun setData() {
        mBinding.apply {
            lnEventName.isVisible = !event.name.isNullOrEmpty()
            tvEventNameText.text = event.name

            lnEventFormat.isVisible = !event.getEventFormat().name.isNullOrEmpty()
            tvEventFormatText.text = event.getEventFormat().name

            lnEventDescription.isVisible = !event.description.isNullOrEmpty()
            tvEventDescriptionText.apply {
                text = getMarkdownText(event.description)
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            lnEventLocation.isVisible = !event.address?.getFullAddress().isNullOrEmpty()
            tvEventLocationText.apply {
                val goTo = CustomSpannableString(context.getString(R.string.how_to_go)).apply {
                    setColorSpan(R.color.bottom_nav_item_selected_color, context)
                    setClickSpan(tvEventLocationText) { openMap() }
                }

                val directions =
                    CustomSpannableString(" • " + context.getString(R.string.go_direction)).apply {
                        setColorSpan(R.color.bottom_nav_item_selected_color, context)
                        setClickSpan(tvEventLocationText) { openRoute(event.address?.fullValue) }
                    }
                text = SpannableStringBuilder().apply {
                    append(event.address?.getFullAddress())
                    append("\n")
                    append(goTo)
                    append(directions)
                }
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            lnEventPhone.isVisible = !event.phone.isNullOrEmpty()
            tvEventPhoneText.apply {
                text = SpannableStringBuilder().apply {
                    event.phone?.forEachIndexed { index, it ->
                        if (index > 0) append("\n\n")
                        append(CustomSpannableString(it.value?.parsePhone(context)).apply {
                            setColorSpan(R.color.bottom_nav_item_selected_color, context)
                            setClickSpan(tvEventPhoneText){ openSupportPhone(it.value) }
                        })
                        append("\n")
                        append(CustomSpannableString(it.title).apply {
                            setTextSizeSpan(R.dimen.user_short_name_text_size, context)
                            setColorSpan(R.color.register_event_go_to_profile_text_color, context)
                        })

                    }
                }
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            lnEventEmail.isVisible = !event.email.isNullOrEmpty()
            tvEventEmailText.apply {
                text = SpannableStringBuilder().apply {
                    event.email?.forEachIndexed { index, it ->
                        if (index > 0) append("\n\n")
                        append(CustomSpannableString(it.value).apply {
                            setColorSpan(R.color.bottom_nav_item_selected_color, context)
                            setClickSpan(tvEventEmailText){ openSupportEmail(it.value) }
                        })
                        append("\n")
                        append(CustomSpannableString(it.title).apply {
                            setTextSizeSpan(R.dimen.user_short_name_text_size, context)
                            setColorSpan(R.color.register_event_go_to_profile_text_color, context)
                        })

                    }
                }
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            lnEventLinks.isVisible = !event.site.isNullOrEmpty() || !event.socialLink.isNullOrEmpty()
            tvEventLinksText.apply {
                val sites = event.site ?: emptyList()
                val socialLinks = event.socialLink ?: emptyList()
                text = SpannableStringBuilder().apply {
                    sites.plus(socialLinks).forEachIndexed { index, site ->
                        if (index > 0) append("\n\n")
                        append(CustomSpannableString(site.value).apply {
                            setColorSpan(R.color.bottom_nav_item_selected_color, context)
                            setClickSpan(tvEventLinksText) { showCustomTabsBrowser(context, site.value ?: "") }
                        })
                    }
                }
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }

            lnEventPages.isVisible = !event.binds?.page.isNullOrEmpty()
            tvEventPagesText.apply {
                text = SpannableStringBuilder().apply {
                    event.binds?.page?.forEachIndexed { index, page ->
                        if (index > 0) append("\n\n")
                        append(CustomSpannableString(page.name).apply {
                            setColorSpan(R.color.bottom_nav_item_selected_color, context)
                            setFontSpan("fonts/sf_pro_text_medium.ttf", context)
                            setClickSpan(tvEventPagesText) { openPage(page.id) }
                        })
                    }
                }
                highlightColor = getColor(R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun openSupportEmail(email : String?) {
        if (email.isNullOrEmpty()) return
        try {
            val intent = Intent(ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(EXTRA_EMAIL, arrayOf(email))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSupportPhone(phone : String?) {
        if (phone.isNullOrEmpty()) return
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openRoute(address: String?) {
        val routeUrl = "https://yandex.ru/maps/?mode=search&text=$address"
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(routeUrl))
            activity.startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(activity, R.string.map_route_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun openMap(){
        if (event.address?.lat != null && event.address.lon != null){
            val mapInfo = event.createMapInfo()
            if (mapInfo != null)
                MapFragment(mapInfo).show((activity as MainActivity).supportFragmentManager)
        }
    }

    private fun openPage(id : Int?){
        if (event.id == null || id == null) return
        PageFragment(event.id.toString(), id.toString())
            .show((activity as MainActivity).supportFragmentManager)
    }

    private fun getMarkdownText(message: String?): SpannableStringBuilder? {
        if (message.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(message)
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
            }
        }
    }
}