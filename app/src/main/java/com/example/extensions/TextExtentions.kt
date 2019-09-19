package com.example.extensions

fun CharSequence.substringToWholeWord(maxLength: Int = this.length): CharSequence {
    return if (length > maxLength) {
        val maxString = subSequence(0, maxLength - 1).trim()
        val spaceIndex = maxString.indexOfLast { it.isWhitespace() }
        if (spaceIndex > 0) maxString.subSequence(0, spaceIndex) else maxString
    } else {
        this
    }
}