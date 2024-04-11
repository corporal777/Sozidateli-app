package com.example.ui.views.phoneText

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.R
import com.example.extensions.onFocusChanged

class PhoneFormatEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    private var onInputFocusChanged: (hasFocus: Boolean) -> Unit = {}
    private var onInputTextChanged: (text: CharSequence?) -> Unit = {}

    private var mask: String? = "+7 9## ### ## ##"
    private var charRepresentation = '#'
    private var keepHint = false
    private var rawToMask: IntArray = IntArray(0)
    private var rawText = RawText()
    private var editingBefore = false
    private var editingOnChanged = false
    private var editingAfter = false
    private var maskToRaw: IntArray = IntArray(0)
    private var textSelection = 0
    private var initialized = false
    private var ignore = false
    private var maxRawLength = 0
    private var lastValidMaskPosition = 0
    private var selectionChanged = false
    private var allowedChars: String? = "1234567890"
    private var deniedChars: String? = null
    private var isKeepingText = false


    init {
        cleanUp()
        onFocusChanged { hasFocus ->
            onInputFocusChanged.invoke(hasFocus)
            if (hasFocus) {
                selectionChanged = false
                this@PhoneFormatEditText.setSelection(lastValidPosition())
            }
        }
        doBeforeTextChanged { text, start, count, after ->
            if (!editingBefore) {
                editingBefore = true
                if (start > lastValidMaskPosition) ignore = true

                var rangeStart = start
                if (after == 0) rangeStart = erasingStart(start)

                val range: Range = calculateRange(rangeStart, start + count)
                if (range.start != -1) rawText.subtractFromString(range)
                if (count > 0) textSelection = previousValidPosition(start)
            }
        }

        doAfterTextChanged {
            if (!editingAfter && editingBefore && editingOnChanged) {
                editingAfter = true
                if (hasHint() && (keepHint || rawText.length() == 0)) {
                    setText(makeMaskedTextWithHint())
                } else setText(makeMaskedText())

                selectionChanged = false
                setSelection(textSelection)
                editingBefore = false
                editingOnChanged = false
                editingAfter = false
                ignore = false
            }
            onInputTextChanged.invoke(text.toString())
        }

        doOnTextChanged { s, start, before, c ->
            var count = c
            if (!editingOnChanged && editingBefore) {
                editingOnChanged = true
                if (ignore) return@doOnTextChanged

                if (count > 0) {
                    val startingPosition = maskToRaw[nextValidPosition(start)]
                    val addedString = s?.subSequence(start, start + count).toString()
                    count = rawText.addToString(clear(addedString), startingPosition, maxRawLength)
                    if (initialized) {
                        val currentPosition = if (startingPosition + count < rawToMask.size) rawToMask[startingPosition + count] else lastValidMaskPosition + 1
                        textSelection = nextValidPosition(currentPosition)
                    }
                }
            }
        }
    }



    private fun cleanUp() {
        initialized = false
        if (mask == null || mask!!.isEmpty()) return

        generatePositionArrays()
        if (!isKeepingText || rawText == null) {
            rawText = RawText()
            textSelection = rawToMask[0]
        }
        editingBefore = true
        editingOnChanged = true
        editingAfter = true

        if (hasHint() && rawText.length() == 0) setText(makeMaskedTextWithHint())
        else setText(makeMaskedText())

        editingBefore = false
        editingOnChanged = false
        editingAfter = false
        maxRawLength = maskToRaw[previousValidPosition(mask!!.length - 1)] + 1
        lastValidMaskPosition = findLastValidMaskPosition()
        initialized = true
    }

    private fun findLastValidMaskPosition(): Int {
        for (i in maskToRaw.indices.reversed()) {
            if (maskToRaw[i] != -1) return i
        }
        throw RuntimeException("Mask must contain at least one representation char")
    }

    private fun hasHint(): Boolean = hint != null

    fun setShouldKeepText(shouldKeepText: Boolean) {
        isKeepingText = shouldKeepText
    }

    fun setMask(mask: String?) {
        this.mask = mask
        cleanUp()
    }

    fun getMask(): String? {
        return mask
    }

    fun getRawText(): String {
        return rawText.getText()
    }

    fun setCharRepresentation(charRepresentation: Char) {
        this.charRepresentation = charRepresentation
        cleanUp()
    }

    fun getCharRepresentation(): Char {
        return charRepresentation
    }

    private fun generatePositionArrays() {
        val aux = IntArray(mask!!.length)
        maskToRaw = IntArray(mask!!.length)
        var charsInMaskAux = ""
        var charIndex = 0
        for (i in 0 until mask!!.length) {
            val currentChar = mask!![i]
            if (currentChar == charRepresentation) {
                aux[charIndex] = i
                maskToRaw[i] = charIndex++
            } else {
                val charAsString = Character.toString(currentChar)
                if (!charsInMaskAux.contains(charAsString)) {
                    charsInMaskAux += charAsString
                }
                maskToRaw[i] = -1
            }
        }
        if (charsInMaskAux.indexOf(' ') < 0) charsInMaskAux += SPACE
        val charsInMask = charsInMaskAux.toCharArray()
        rawToMask = IntArray(charIndex)
        System.arraycopy(aux, 0, rawToMask, 0, charIndex)
    }


    private fun erasingStart(st: Int): Int {
        var start = st
        while (start > 0 && maskToRaw[start] == -1) {
            start--
        }
        return start
    }

    private fun isKeepHint(): Boolean = keepHint

    fun setKeepHint(keepHint: Boolean) {
        this.keepHint = keepHint
        setText(getRawText())
    }

    override fun onSelectionChanged(selS: Int, selE: Int) {
        var selStart = selS
        var selEnd = selE
        if (!text.isNullOrEmpty()){
            if (initialized) {
                if (!selectionChanged) {
                    selStart = fixSelection(selStart)
                    selEnd = fixSelection(selEnd)

                    // exactly in this order. If getText.length() == 0 then selStart will be -1
                    if (selStart > text!!.length) selStart = text!!.length
                    if (selStart < 0) selStart = 0

                    // exactly in this order. If getText.length() == 0 then selEnd will be -1
                    if (selEnd > text!!.length) selEnd = getText()!!.length
                    if (selEnd < 0) selEnd = 0
                    setSelection(selStart, selEnd)
                    selectionChanged = true
                } else {
                    //check to see if the current selection is outside the already entered text
                    if (selStart > rawText.length() - 1) {
                        val start = fixSelection(selStart)
                        val end = fixSelection(selEnd)
                        if (start >= 0 && end < text!!.length) {
                            setSelection(start, end)
                        }
                    }
                }
            }
        }

        super.onSelectionChanged(selStart, selEnd)
    }

    private fun fixSelection(selection: Int): Int {
        return if (selection > lastValidPosition()) lastValidPosition()
        else nextValidPosition(selection)
    }

    private fun nextValidPosition(position: Int): Int {
        var currentPosition = position
        while (currentPosition < lastValidMaskPosition && maskToRaw[currentPosition] == -1) {
            currentPosition++
        }
        return if (currentPosition > lastValidMaskPosition) lastValidMaskPosition + 1 else currentPosition
    }

    private fun previousValidPosition(position: Int): Int {
        var currentPosition = position
        while (currentPosition >= 0 && maskToRaw[currentPosition] == -1) {
            currentPosition--
            if (currentPosition < 0) {
                return nextValidPosition(0)
            }
        }
        return currentPosition
    }

    private fun lastValidPosition(): Int {
        return if (rawText.length() == maxRawLength) {
            rawToMask[rawText.length() - 1] + 1
        } else nextValidPosition(rawToMask[rawText.length()])
    }

    private fun changeTextColor(maskedText : String){
        if (rawText.text.isNullOrEmpty()){
            if (maskedText.replace(" ", "").length == 3)
                setTextColor(currentHintTextColor)
        }
        else setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun makeMaskedText(): String {
        val maskedTextLength = if (rawText.length() < rawToMask.size) {
            rawToMask[rawText.length()]
        } else mask!!.length

        val maskedText = CharArray(maskedTextLength)
        for (i in maskedText.indices) {
            val rawIndex = maskToRaw[i]
            if (rawIndex == -1) maskedText[i] = mask!![i]
            else maskedText[i] = rawText.charAt(rawIndex)
        }

        changeTextColor(String(maskedText))
        return String(maskedText)
    }

    private fun makeMaskedTextWithHint(): CharSequence {
        val ssb = SpannableStringBuilder()
        var mtrv: Int
        val maskFirstChunkEnd = rawToMask[0]
        for (i in 0 until mask!!.length) {
            mtrv = maskToRaw[i]
            if (mtrv != -1) {
                if (mtrv < rawText.length()) ssb.append(rawText.charAt(mtrv))
                else ssb.append(hint.get(maskToRaw[i]))
            } else ssb.append(mask!![i])

            if ((keepHint && rawText.length() < rawToMask.size && i >= rawToMask[rawText.length()] || !keepHint) && i >= maskFirstChunkEnd) {
                ssb.setSpan(ForegroundColorSpan(getCurrentHintTextColor()), i, i + 1, 0)
            }
        }
        return ssb
    }

    private fun calculateRange(start: Int, end: Int): Range {
        val range = Range()
        var i = start
        while (i <= end && i < mask!!.length) {
            if (maskToRaw[i] != -1) {
                if (range.start == -1) {
                    range.start = maskToRaw[i]
                }
                range.end = maskToRaw[i]
            }
            i++
        }
        if (end == mask!!.length) {
            range.end = rawText.length()
        }
        if (range.start == range.end && start < end) {
            val newStart = previousValidPosition(range.getStart() - 1)
            if (newStart < range.start) {
                range.start = newStart
            }
        }
        return range
    }

    private fun clear(str: String): String {
        var string = str
        if (deniedChars != null) {
            for (c in deniedChars!!.toCharArray()) {
                string = string.replace(Character.toString(c), "")
            }
        }
        if (allowedChars != null) {
            val builder = StringBuilder(string.length)
            for (c in string.toCharArray()) {
                if (allowedChars!!.contains(c.toString())) {
                    builder.append(c)
                }
            }
            string = builder.toString()
        }
        return string
    }

    fun onInputFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit) {
        this.onInputFocusChanged = onFocusChanged
    }

    fun onInputTextChanged(onTextChanged: (text: CharSequence?) -> Unit) {
        this.onInputTextChanged = onTextChanged
    }

    companion object {
        const val SPACE = " "
    }
}