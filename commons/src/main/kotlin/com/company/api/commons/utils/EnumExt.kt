package com.company.api.commons.utils

inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (_: IllegalArgumentException) {
        null
    }
}