package com.company.api.commons.utils

import java.util.*

fun uuidStrOf(vararg params: Any): String = uuidOf(*params).toString()

fun uuidOf(vararg params: Any): UUID =
    if (params.isEmpty()) UUID.randomUUID()
    else UUID.nameUUIDFromBytes(params.joinToString().toByteArray())

fun String.toUUID(): UUID = UUID.fromString(this)