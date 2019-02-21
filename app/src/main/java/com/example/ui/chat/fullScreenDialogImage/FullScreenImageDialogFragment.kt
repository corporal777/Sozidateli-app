package com.example.ui.chat.fullScreenDialogImage

import android.graphics.Color
import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import bundleOf
import com.example.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_full_screen_dialog.view.*


class FullScreenImageDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_full_screen_dialog, container, false)

        view.flRoot.dismissListener = {
            dialog?.dismiss()
        }

        view.flRoot.positionChangeListener = {top: Int, left: Int, positionRangeRate: Float ->
            view.flRoot.setBackgroundColor(Color.argb(Math.round(255 * (1.0F - positionRangeRate)), 0, 0, 0))
        }

        view.ivImage.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
            view.flRoot.isDragEnabled = scaleFactor <= 1F
        }

        arguments?.let {
            Picasso.get().load(it.getString(ARG_IMAGE,null)).into(view.ivImage)
        }

        return view
    }

   override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.let {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                it.window?.setLayout(width, height)
            }
        }
    }

    companion object {

        var TAG = "FullScreenDialog"

       private const val ARG_IMAGE = "image"


        fun newInstance(imageUrl:String):FullScreenImageDialogFragment{
            val fragment = FullScreenImageDialogFragment()
            fragment.arguments = bundleOf(
                    ARG_IMAGE to imageUrl
            )
            return fragment
        }
    }
}