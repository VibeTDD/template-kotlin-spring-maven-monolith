package dev.vibetdd.api.commons.utils

import java.util.*

fun String.firstCharsOrFull(endIndex: Int): String {
    if (this.length <= endIndex) return this
    return this.substring(0, endIndex)
}

fun String.toLangName(): String {
    val name = Locale.forLanguageTag(this).displayLanguage
    return if (name.isNullOrBlank() || name.equals("unknown", ignoreCase = true)) "English" else name
}