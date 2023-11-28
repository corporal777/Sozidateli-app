package com.example.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomTextMenuViewBinding
import com.example.extensions.defaultDateFormatter
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.getDrawable
import com.example.util.initDropDownAdapter
import com.google.android.material.textfield.TextInputLayout
import initAsDatePicker
import onTextChanged
import java.util.*


class CustomTextMenuView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var titleText: CharSequence? = ""
    private var hintText: CharSequence? = ""
    private var inputIconMode: Int = TextInputLayout.END_ICON_DROPDOWN_MENU
    private var inputIconDrawable: Drawable? = getDrawable(R.drawable.drawable_drop_down_switch)
    private var inputWithCheckBox: Boolean = false

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextMenuView)
        titleText = a.getText(R.styleable.CustomTextMenuView_inputTitleText)
        hintText = a.getText(R.styleable.CustomTextMenuView_inputHintText)
        inputIconDrawable = a.getDrawable(R.styleable.CustomTextMenuView_inputIconDrawable)
        inputIconMode = a.getInt(R.styleable.CustomTextMenuView_inputIconMode, 3)
        inputWithCheckBox = a.getBoolean(R.styleable.CustomTextMenuView_inputHasCheckBox, false)
        a.recycle()

        layoutView.etInput.hint = hintText
        layoutView.tvTitle.apply {
            isVisible = !titleText.isNullOrEmpty()
            text = titleText
        }
        layoutView.tilInput.apply {
            endIconMode = inputIconMode
            endIconDrawable = inputIconDrawable ?: getDrawable(R.drawable.drawable_drop_down_switch)
        }
        layoutView.scCheck.isVisible = inputWithCheckBox
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private val layoutView =
        LayoutCustomTextMenuViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutView.apply {
            etInput.onTextChanged {
                onTextChanged.invoke(it.toString())
            }
        }
    }

    fun showError(show: Boolean) {
        layoutView.apply {
            btnAction.isVisible = show
            btnAction.isEnabled = !show
            tilInput.isEndIconVisible = !show
            if (show) tvTitle.setTextColor(ContextCompat.getColor(context, R.color.red_new))
            else {
                tvTitle.text = titleText
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.chat_list_date))
            }
        }
    }

    fun setText(text: String?) = layoutView.etInput.setText(text)
    fun setIconVisibility(visible : Boolean) = layoutView.tilInput.run { isEndIconVisible = visible }
    override fun setEnabled(enabled: Boolean) = layoutView.tilInput.run { isEnabled = enabled }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        layoutView.etInput.setText(text)
        this.onTextChanged = onTextChanged
    }

    fun initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        layoutView.scCheck.isChecked = checked
        layoutView.scCheck.setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    fun initAsDropDown(text: String? = null, list: List<String>, onTextChanged: (text: CharSequence?) -> Unit){
        layoutView.etInput.setText(text)
        layoutView.etInput.initDropDownAdapter(list.toMutableList())
        this@CustomTextMenuView.onTextChanged = onTextChanged
    }

    fun initAsDateTimePicker(birthday : String?, onTextChanged: (text: CharSequence?) -> Unit){
        layoutView.etInput.setText(birthday)
        layoutView.tilInput.apply {
            val date = if (!birthday.isNullOrBlank()) defaultDateFormatter.parse(birthday) else null
            initAsDatePicker(
                startDate = date,
                maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -14) }.time
            ) { year, month, day ->
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }

            this@CustomTextMenuView.onTextChanged = onTextChanged
        }
    }

    fun initAsCustomMode(text: String? = null, onEndClick : () -> Unit){
        layoutView.etInput.setText(text)
        layoutView.tilInput.setEndIconOnClickListener {
            onEndClick.invoke()
        }
        layoutView.etInput.setOnClickListener {
            onEndClick.invoke()
        }
    }

    fun getInputLayout() = layoutView.tilInput
}