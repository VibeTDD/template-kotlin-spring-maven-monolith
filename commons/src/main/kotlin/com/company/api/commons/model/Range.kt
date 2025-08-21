package com.company.api.commons.model

data class Range<T>(
    val from: T? = null,
    val to: T? = null,
) where T : Number, T : Comparable<T> {

    fun isIn(value: T): Boolean {
        val fromCheck = from?.let { value >= it } ?: true
        val toCheck = to?.let { value <= it } ?: true
        return fromCheck && toCheck
    }
}
